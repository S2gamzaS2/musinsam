package com.musinsam.productservice.application.service.V2;

import com.musinsam.common.exception.CustomException;
import com.musinsam.common.user.CurrentUserDtoApiV1;
import com.musinsam.common.user.UserRoleType;
import com.musinsam.productservice.application.dto.request.ReqProductPatchByProductIdDtoApiV1;
import com.musinsam.productservice.application.dto.request.ReqProductPostDtoApiV1;
import com.musinsam.productservice.application.dto.request.ReqProductPutByProductIdDtoApiV1;
import com.musinsam.productservice.application.dto.response.ResProductGetByProductIdDtoApiV1;
import com.musinsam.productservice.application.dto.response.ResProductGetDtoApiV1;
import com.musinsam.productservice.application.dto.response.ResProductGetStockDtoApiV1;
import com.musinsam.productservice.application.integration.CouponClientApiV1;
import com.musinsam.productservice.application.integration.EventClientApiV1;
import com.musinsam.productservice.application.integration.ShopClientApiV1;
import com.musinsam.productservice.domain.product.entity.ProductEntity;
import com.musinsam.productservice.domain.product.entity.ProductImageEntity;
import com.musinsam.productservice.domain.product.repository.ProductImageRepository;
import com.musinsam.productservice.domain.product.repository.ProductRepository;
import com.musinsam.productservice.domain.product.repository.ProductRepositoryCustom;
import com.musinsam.productservice.domain.product.vo.ProductStatus;
import com.musinsam.productservice.global.exception.ProductErrorCode;
import com.musinsam.productservice.infrastructure.dto.res.ReqProductSaveProductsDtoApiV1;
import com.musinsam.productservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import com.musinsam.productservice.infrastructure.dto.res.ResShopCouponDtoApiV1;
import com.musinsam.productservice.infrastructure.dto.res.ResShopCouponDtoApiV1.Coupon;
import com.musinsam.productservice.infrastructure.s3.S3Folder;
import com.musinsam.productservice.infrastructure.s3.service.S3Service;
import io.micrometer.common.util.StringUtils;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImplApiV2 implements ProductServiceApiV2 {

  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final ProductRepositoryCustom productRepositoryCustom;
  private final S3Service s3Service;
  private final ShopClientApiV1 shopClientApiV1;
  private final CouponClientApiV1 couponClientApiV1;
  private final EventClientApiV1 eventClientApiV1;
  private final ValueOperations<String, ResProductGetByProductIdDtoApiV1> valueOps;
  private final RedisTemplate<String, ResProductGetByProductIdDtoApiV1> redisTemplate;


  @Value("${file.image-extension}")
  private String imageExtension;

  private static final String PRODUCT_CACHE_KEY_PREFIX = "product:";


  @Override
  @Transactional
  public void createProduct(CurrentUserDtoApiV1 currentUser, ReqProductPostDtoApiV1 dto,
      List<MultipartFile> images) {

    ProductEntity product = dto.getProduct().toEntity();
    productRepository.save(product);

    processProductImage(images, product.getId(), null, null);
  }


  @Override
  @Transactional(readOnly = true)
  public ResProductGetByProductIdDtoApiV1 getById(UUID productId) {

    String redisKey = buildProductCacheKey(productId);
    ResProductGetByProductIdDtoApiV1 cachedProduct = valueOps.get(redisKey);

    if (cachedProduct != null) {
      log.info("########## 캐시 히트 - productId: {}", productId);
      return cachedProduct;
    }

    log.info("########## 캐시 미스 - productId: {}, 이벤트 상품이 아닙니다.", productId);

    ProductEntity product = findProductEntityById(productId);
    List<ProductImageEntity> productImages = getProductImages(productId);

    UUID shopId = product.getShopId();
    String shopName = shopClientApiV1.getShopInfo(shopId).getShop().getName();

    ResShopCouponDtoApiV1 shopCouponDto = couponClientApiV1.getShopCouponList(shopId);

    return ResProductGetByProductIdDtoApiV1.of(product,
        productImages, shopName, shopCouponDto.getCouponList());
  }


  @Override
  @Transactional(readOnly = true)
  public ResProductGetDtoApiV1 getProductList(
      BigDecimal minPrice,
      BigDecimal maxPrice,
      ProductStatus status,
      String sortBy,
      int page,
      int size) {

    if (page < 0) {
      page = 0;
    }
    if (size < 1) {
      size = 10;
    }
    if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
      minPrice = BigDecimal.ZERO;
    }
    List<String> sorts = List.of("price_asc", "price_desc");
    if (sortBy != null && !sorts.contains(sortBy)) {
      sortBy = null;
    }

    Pageable pageable = PageRequest.of(page, size);
    Page<ProductEntity> productEntityPage = productRepositoryCustom.findProductWithConditions(
        minPrice, maxPrice, status, sortBy, pageable);

    return ResProductGetDtoApiV1.of(productEntityPage);
  }


  @Override
  @Transactional
  public void updateProduct(CurrentUserDtoApiV1 currentUser, UUID productId,
      ReqProductPutByProductIdDtoApiV1 dto, List<MultipartFile> newImages) {

    ProductEntity product = findProductEntityById(productId);
    validateShopManager(currentUser, product);

    processProductImage(newImages, productId, dto.getProduct().getDeletedImages(), currentUser);

    dto.getProduct().updateOf(product);

    log.info("########### DB 수정 완료");

    updateProductCache(productId, product);
  }


  @Override
  @Transactional
  public void deleteProduct(CurrentUserDtoApiV1 currentUser, UUID productId) {

    ProductEntity product = findProductEntityById(productId);

    validateShopManager(currentUser, product);

    List<ProductImageEntity> images = getProductImages(productId);

    product.softDelete(currentUser.userId(), ZoneId.systemDefault());

    for (ProductImageEntity productImage : images) {
      s3Service.removeFile(productImage.getImageUrl(), productImage.getS3Folder());
      productImage.softDelete(currentUser.userId(), ZoneId.systemDefault());
    }

    deleteProductCache(productId);

  }


  @Override
  @Transactional(readOnly = true)
  public ResProductGetStockDtoApiV1 getProductStock(CurrentUserDtoApiV1 currentUser,
      UUID productId) {

    ProductEntity product = findProductEntityById(productId);
    validateShopManager(currentUser, product);

    return ResProductGetStockDtoApiV1.of(product);
  }


  @Override
  @Transactional
  public void updateProductStock(CurrentUserDtoApiV1 currentUser, UUID productId,
      ReqProductPatchByProductIdDtoApiV1 dto) {

    ProductEntity product = findProductEntityById(productId);
    validateShopManager(currentUser, product);

    dto.getProduct().updateOf(product);
    updateProductCache(productId, product);
  }


  @Override
  @Transactional
  public Boolean checkAndReduceStock(UUID productId, Integer quantity) {

    ProductEntity product = findProductEntityById(productId);
    Integer stock = product.getStock();

    if (ProductStatus.SOLDOUT.equals(product.getStatus()) || stock < quantity) {
      return false;
    }

    product.setStock(stock - quantity);
    if (stock.equals(quantity)) {
      product.setStatus(ProductStatus.SOLDOUT);
    }

    updateProductCache(productId, product);
    return true;
  }


  @Override
  @Transactional
  public void restoreStock(UUID productId, Integer quantity) {
    ProductEntity product = findProductEntityById(productId);
    product.setStock(product.getStock() + quantity);

    if (ProductStatus.SOLDOUT.equals(product.getStatus())) {
      product.setStatus(ProductStatus.SHOW);
    }

    updateProductCache(productId, product);
  }


  /**
   * 이미지 업로드
   **/
  @Override
  public void saveImageFile(S3Folder s3Folder, MultipartFile file, UUID id) {

    if (s3Folder == null) {
      throw new CustomException(ProductErrorCode.S3_FOLDER_NOT_SPECIFIED);
    }

    String fileExtension = validateFileAndGetFileExtension(file);
    final String fileName = UUID.randomUUID() + "." + fileExtension;

    s3Service.uploadFile(fileName, s3Folder, file);

    if (s3Folder == S3Folder.PRODUCT) {
      saveProductImageInfo(s3Folder, file, id, fileName);
    }
  }


  @Override
  public void saveProductImageInfo(S3Folder s3Folder, MultipartFile file, UUID id,
      String fileName) {

    ProductEntity product = findProductEntityById(id);

    ProductImageEntity productImage = ProductImageEntity.builder()
        .originFileName(file.getOriginalFilename())
        .imageUrl(fileName)
        .fileSize(file.getSize())
        .s3Folder(s3Folder)
        .product(product)
        .build();

    productImageRepository.save(productImage);
  }


  /**
   * feign
   **/
  @Override
  public ResProductInfoGetByProductId getProductInfo(UUID productId) {

    ProductEntity productEntity = findProductEntityById(productId);
    return ResProductInfoGetByProductId.of(productEntity);
  }

  @Override
  @Transactional
  public void saveProductsToRedis(ReqProductSaveProductsDtoApiV1 dto) {

    List<ReqProductSaveProductsDtoApiV1.EventProduct> eventProductList = dto.getEventProductList();

    if (eventProductList == null || eventProductList.isEmpty()) {
      log.info("No products to save in Redis");
      return;
    }

    long ttlInSeconds = Duration.between(LocalDateTime.now(), dto.getEndTime()).getSeconds();
    log.info("######## 이벤트 종료시간: {}", dto.getEndTime());
    log.info("ttlInSeconds: {}", ttlInSeconds);

    if (ttlInSeconds < 0) {
      log.warn("이벤트 종료됨");
      return;
    }

    for (ReqProductSaveProductsDtoApiV1.EventProduct eventProduct : eventProductList) {

      UUID productId = eventProduct.getProductId();
      ProductEntity product = findProductEntityById(productId);

      Integer discountRate = eventProduct.getDiscountRate();
      BigDecimal discountPrice = calculateDiscountPrice(discountRate, product);
      product.setDiscountPrice(discountPrice);

      List<ProductImageEntity> productImages = getProductImages(productId);
      UUID shopId = product.getShopId();
      String shopName = shopClientApiV1.getShopInfo(shopId).getShop().getName();

//      ResShopCouponDtoApiV1 shopCouponDto = couponClientApiV1.getShopCouponList(shopId);
      ResShopCouponDtoApiV1 shopCouponDto = null;

      ResProductGetByProductIdDtoApiV1 resDto = ResProductGetByProductIdDtoApiV1.of(product,
          productImages, shopName, null);

      String redisKey = buildProductCacheKey(productId);
      valueOps.set(redisKey, resDto, ttlInSeconds, TimeUnit.SECONDS);

    }

  }

  @Override
  public void updateDiscountRate(UUID productId, Integer discountRate) {
    log.info("########## updateDiscountRate 서비스 메서드 시작");
    ProductEntity productEntity = findProductEntityById(productId);
    String redisKey = buildProductCacheKey(productId);
    ResProductGetByProductIdDtoApiV1 cachedProduct = valueOps.get(redisKey);

    if (cachedProduct != null) {
      LocalDateTime endTime = eventClientApiV1.getEndTime(productId);
      String shopName = cachedProduct.getProduct().getShop().getShopName();
      List<Coupon> coupons = cachedProduct.getProduct().getShop().getCoupons();
      List<ProductImageEntity> images = getProductImages(productId);

      BigDecimal discountPrice = calculateDiscountPrice(discountRate, productEntity);
      productEntity.setDiscountPrice(discountPrice);

      long ttlInSeconds = Duration.between(LocalDateTime.now(), endTime).getSeconds();

      if (ttlInSeconds > 0) {
        valueOps.set(redisKey, ResProductGetByProductIdDtoApiV1.of(productEntity,
            images, shopName, coupons), ttlInSeconds, TimeUnit.SECONDS);
        log.info("########### 캐싱 수정 완료");
      }
    }
  }

  @Override
  @Transactional
  public void deleteEventProduct(UUID productId) {
    deleteProductCache(productId);
    ProductEntity productEntity = findProductEntityById(productId);
    productEntity.setDiscountPrice(null);
  }


  /**
   * 유틸리티 메서드
   **/
  // ProductEntity 조회
  private ProductEntity findProductEntityById(UUID productId) {
    return productRepository.findByIdAndDeletedAtIsNull(productId)
        .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
  }

  // 담당 업체 확인
  private void validateShopManager(CurrentUserDtoApiV1 currentUser, ProductEntity product) {
    if ((UserRoleType.ROLE_COMPANY).equals(currentUser.role())) {
      UUID shopId = product.getShopId();
      if (!currentUser.userId().equals(shopClientApiV1.getShopInfo(shopId).getShop().getUserId())) {
        throw new CustomException(ProductErrorCode.UNAUTHORIZED_PRODUCT_ACCESS);
      }
    }
  }

  // 이미지 처리 (저장, 수정)
  private void processProductImage(List<MultipartFile> newImages, UUID productId,
      List<UUID> deletedImageIds, CurrentUserDtoApiV1 currentUser) {

    if (deletedImageIds != null && !deletedImageIds.isEmpty()) {
      List<ProductImageEntity> originImages = getProductImages(productId);

      for (ProductImageEntity originImage : originImages) {
        if (deletedImageIds.contains(originImage.getId())) {
          originImage.softDelete(currentUser.userId(), ZoneId.systemDefault());
          s3Service.removeFile(originImage.getImageUrl(), originImage.getS3Folder());
        }
      }
    }

    if (newImages != null && !newImages.isEmpty()) {
      for (MultipartFile newImage : newImages) {
        saveImageFile(S3Folder.PRODUCT, newImage, productId);
      }
    }
  }

  // 이미지 리스트
  private List<ProductImageEntity> getProductImages(UUID productId) {
    return productImageRepository.findByProductIdAndDeletedAtIsNull(productId);
  }

  // 레디스 키 생성
  private String buildProductCacheKey(UUID productId) {
    return PRODUCT_CACHE_KEY_PREFIX + productId.toString();
  }

  // 캐시 업데이트
  private void updateProductCache(UUID productId, ProductEntity productEntity) {
    String redisKey = buildProductCacheKey(productId);
    ResProductGetByProductIdDtoApiV1 cachedProduct = valueOps.get(redisKey);

    if (cachedProduct != null) {
      LocalDateTime endTime = eventClientApiV1.getEndTime(productId);
      String shopName = cachedProduct.getProduct().getShop().getShopName();
      List<Coupon> coupons = cachedProduct.getProduct().getShop().getCoupons();
      List<ProductImageEntity> images = getProductImages(productId);

      Integer discountRate = eventClientApiV1.getDiscountRate(productId);
      BigDecimal discountPrice = calculateDiscountPrice(discountRate, productEntity);
      productEntity.setDiscountPrice(discountPrice);

      long ttlInSeconds = Duration.between(LocalDateTime.now(), endTime).getSeconds();

      if (ttlInSeconds > 0) {
        valueOps.set(redisKey, ResProductGetByProductIdDtoApiV1.of(productEntity,
            images, shopName, coupons), ttlInSeconds, TimeUnit.SECONDS);
        log.info("########### 캐싱 수정 완료");
      }
    }
  }

  // 할인가 계산
  private BigDecimal calculateDiscountPrice(Integer discountRate, ProductEntity product) {
    return product.getPrice().multiply(
        BigDecimal.valueOf(100 - discountRate)
            .multiply(new BigDecimal("0.01")));
  }

  // 캐시 삭제
  private void deleteProductCache(UUID productId) {
    String redisKey = buildProductCacheKey(productId);
    redisTemplate.delete(redisKey);
  }

  // 파일 검증, 확장자 확인 및 반환
  private String validateFileAndGetFileExtension(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new CustomException(ProductErrorCode.FILE_IS_EMPTY);
    }

    final String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
    if (StringUtils.isBlank(fileExtension) || !imageExtension.contains(fileExtension)) {
      throw new CustomException(ProductErrorCode.INVALID_FILE_EXTENSION);
    }

    return fileExtension;
  }
}
