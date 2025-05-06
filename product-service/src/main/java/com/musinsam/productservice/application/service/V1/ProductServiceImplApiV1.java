package com.musinsam.productservice.application.service.V1;

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
import com.musinsam.productservice.application.integration.ShopClientApiV1;
import com.musinsam.productservice.domain.product.entity.ProductEntity;
import com.musinsam.productservice.domain.product.entity.ProductImageEntity;
import com.musinsam.productservice.domain.product.repository.ProductImageRepository;
import com.musinsam.productservice.domain.product.repository.ProductRepository;
import com.musinsam.productservice.domain.product.repository.ProductRepositoryCustom;
import com.musinsam.productservice.domain.product.vo.ProductErrorCode;
import com.musinsam.productservice.domain.product.vo.ProductStatus;
import com.musinsam.productservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import com.musinsam.productservice.infrastructure.dto.res.ResShopCouponDtoApiV1;
import com.musinsam.productservice.infrastructure.s3.S3Folder;
import com.musinsam.productservice.infrastructure.s3.service.S3Service;
import io.micrometer.common.util.StringUtils;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImplApiV1 implements ProductServiceApiV1 {

  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final ProductRepositoryCustom productRepositoryCustom;
  private final S3Service s3Service;
  private final ShopClientApiV1 shopClientApiV1;
  private final CouponClientApiV1 couponClientApiV1;

  @Override
  @Transactional
  public void createProduct(CurrentUserDtoApiV1 currentUser, ReqProductPostDtoApiV1 dto,
      List<MultipartFile> images) {

    ProductEntity product = dto.getProduct().toEntity();
    productRepository.save(product);

    for (MultipartFile image : images) {
      saveImageFile(S3Folder.PRODUCT, image, product.getId());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ResProductGetByProductIdDtoApiV1 getById(UUID productId) {

    ProductEntity product = findProductEntityById(productId);

    List<ProductImageEntity> productImages = productImageRepository.findByProductIdAndDeletedAtIsNull(
        productId);

    UUID shopId = product.getShopId();
    String shopName = shopClientApiV1.getShopInfo(shopId).getShop().getName();

    ResShopCouponDtoApiV1 shopCouponDto = couponClientApiV1.getShopCouponList(shopId);

    ResProductGetByProductIdDtoApiV1 resDto = ResProductGetByProductIdDtoApiV1.of(product,
        productImages, shopName, shopCouponDto.getCouponList());

    return resDto;
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

    List<ProductImageEntity> originImages = productImageRepository.findByProductIdAndDeletedAtIsNull(
        productId);
    List<UUID> deletedImageIds = dto.getProduct().getDeletedImages();

    for (ProductImageEntity originImage : originImages) {
      if (deletedImageIds.contains(originImage.getId())) {
        originImage.softDelete(currentUser.userId(), ZoneId.systemDefault());
        s3Service.removeFile(originImage.getImageUrl(), originImage.getS3Folder());
      }
    }

    for (MultipartFile image : newImages) {
      saveImageFile(S3Folder.PRODUCT, image, product.getId());
    }

    dto.getProduct().updateOf(product);
  }

  @Override
  @Transactional
  public void deleteProduct(CurrentUserDtoApiV1 currentUser, UUID productId) {

    ProductEntity product = findProductEntityById(productId);

    validateShopManager(currentUser, product);

    List<ProductImageEntity> images = productImageRepository.findByProductIdAndDeletedAtIsNull(
        productId);

    product.softDelete(currentUser.userId(), ZoneId.systemDefault());

    for (ProductImageEntity productImage : images) {
      s3Service.removeFile(productImage.getImageUrl(), productImage.getS3Folder());
      productImage.softDelete(currentUser.userId(), ZoneId.systemDefault());
    }

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
  }


  private ProductEntity findProductEntityById(UUID productId) {
    return productRepository.findByIdAndDeletedAtIsNull(productId)
        .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
  }

  private void validateShopManager(CurrentUserDtoApiV1 currentUser, ProductEntity product) {

    log.info("UserRole : {}", currentUser.role());
    log.info("UserId : {}", currentUser.userId());

    if ((UserRoleType.ROLE_COMPANY).equals(currentUser.role())) {
      UUID shopId = product.getShopId();
      if (!currentUser.userId().equals(shopClientApiV1.getShopInfo(shopId).getShop().getUserId())) {
        throw new CustomException(ProductErrorCode.UNAUTHORIZED_PRODUCT_ACCESS);
      }

    }
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
  }


  @Value("${file.image-extension}")
  private String imageExtension;


  @Override
  @Transactional
  public void saveImageFile(S3Folder s3Folder, MultipartFile file, UUID id) {
    // S3 폴더 파라미터 확인
    if (s3Folder == null) {
      throw new CustomException(ProductErrorCode.S3_FOLDER_NOT_SPECIFIED);
    }

    // 파일이 비어있는지 확인
    if (file == null || file.isEmpty()) {
      throw new CustomException(ProductErrorCode.FILE_IS_EMPTY);
    }

    // 파일 확장자 확인
    final String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
    if (StringUtils.isBlank(fileExtension) || !imageExtension.contains(fileExtension)) {
      throw new CustomException(ProductErrorCode.INVALID_FILE_EXTENSION);
    }

    // 파일 이름 생성
    final String fileName = UUID.randomUUID() + "." + fileExtension;

    // S3에 업로드
    s3Service.uploadFile(fileName, s3Folder, file);

    if (s3Folder == S3Folder.PRODUCT) {
      saveProductImageInfo(s3Folder, file, id, fileName);
    }
  }

  @Override
  @Transactional
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


  @Override
  public ResProductInfoGetByProductId getProductInfo(UUID productId) {

    ProductEntity productEntity = findProductEntityById(productId);

    return ResProductInfoGetByProductId.of(productEntity);
  }
}
