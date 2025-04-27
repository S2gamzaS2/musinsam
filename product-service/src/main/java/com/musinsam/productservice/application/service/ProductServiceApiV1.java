package com.musinsam.productservice.application.service;

import com.musinsam.common.user.CurrentUserDtoApiV1;
import com.musinsam.productservice.application.dto.request.ReqProductPatchByProductIdDtoApiV1;
import com.musinsam.productservice.application.dto.request.ReqProductPostDtoApiV1;
import com.musinsam.productservice.application.dto.request.ReqProductPutByProductIdDtoApiV1;
import com.musinsam.productservice.application.dto.response.ResProductGetByProductIdDtoApiV1;
import com.musinsam.productservice.application.dto.response.ResProductGetDtoApiV1;
import com.musinsam.productservice.application.dto.response.ResProductGetStockDtoApiV1;
import com.musinsam.productservice.domain.product.vo.ProductStatus;
import com.musinsam.productservice.infrastructure.s3.S3Folder;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface ProductServiceApiV1 {

  void createProduct(CurrentUserDtoApiV1 currentUser, @Valid ReqProductPostDtoApiV1 dto,
      List<MultipartFile> images);

  ResProductGetByProductIdDtoApiV1 getById(UUID productId);

  ResProductGetDtoApiV1 getProductList(BigDecimal minPrice,
      BigDecimal maxPrice,
      ProductStatus status,
      String sortBy,
      int page,
      int size);

  void updateProduct(CurrentUserDtoApiV1 currentUser, UUID productId,
      @Valid ReqProductPutByProductIdDtoApiV1 dto,
      List<MultipartFile> images);

  void deleteProduct(CurrentUserDtoApiV1 currentUser, UUID productId);

  ResProductGetStockDtoApiV1 getProductStock(CurrentUserDtoApiV1 currentUser, UUID productId);

  void updateProductStock(CurrentUserDtoApiV1 currentUser, UUID productId,
      @Valid ReqProductPatchByProductIdDtoApiV1 dto);

  void saveImageFile(S3Folder s3Folder, MultipartFile file, UUID id);

  void saveProductImageInfo(final S3Folder s3Folder,
      final MultipartFile file,
      final UUID id,
      final String fileName);

  Boolean checkAndReduceStock(UUID productId, Integer quantity);

  void restoreStock(UUID productId, Integer quantity);
}
