package com.musinsam.productservice.presentation.controller;

import static com.musinsam.common.user.UserRoleType.ROLE_COMPANY;
import static com.musinsam.common.user.UserRoleType.ROLE_MASTER;
import static com.musinsam.common.user.UserRoleType.ROLE_USER;
import static com.musinsam.productservice.global.ProductResponseCode.PRODUCT_CREATE_SUCCESS;
import static com.musinsam.productservice.global.ProductResponseCode.PRODUCT_DELETE_SUCCESS;
import static com.musinsam.productservice.global.ProductResponseCode.PRODUCT_GET_LIST_SUCCESS;
import static com.musinsam.productservice.global.ProductResponseCode.PRODUCT_GET_STOCK_SUCCESS;
import static com.musinsam.productservice.global.ProductResponseCode.PRODUCT_GET_SUCCESS;
import static com.musinsam.productservice.global.ProductResponseCode.PRODUCT_UPDATE_STOCK_SUCCESS;
import static com.musinsam.productservice.global.ProductResponseCode.PRODUCT_UPDATE_SUCCESS;

import com.musinsam.common.aop.CustomPreAuthorize;
import com.musinsam.common.resolver.CurrentUser;
import com.musinsam.common.response.ApiResponse;
import com.musinsam.common.user.CurrentUserDtoApiV1;
import com.musinsam.productservice.application.dto.request.ReqProductPatchByProductIdDtoApiV1;
import com.musinsam.productservice.application.dto.request.ReqProductPostDtoApiV1;
import com.musinsam.productservice.application.dto.request.ReqProductPutByProductIdDtoApiV1;
import com.musinsam.productservice.application.dto.response.ResProductGetByProductIdDtoApiV1;
import com.musinsam.productservice.application.dto.response.ResProductGetDtoApiV1;
import com.musinsam.productservice.application.dto.response.ResProductGetStockDtoApiV1;
import com.musinsam.productservice.application.service.ProductServiceApiV1;
import com.musinsam.productservice.domain.product.vo.ProductStatus;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductControllerApiV1 {

  private final ProductServiceApiV1 productService;

  /**
   * 상품 등록
   */
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @CustomPreAuthorize(userRoleType = {ROLE_COMPANY, ROLE_MASTER})
  public ResponseEntity<ApiResponse<Void>> createProduct(
      @Valid @RequestPart ReqProductPostDtoApiV1 dto,
      @RequestPart List<MultipartFile> images,
      @CurrentUser CurrentUserDtoApiV1 currentUser) {

    productService.createProduct(currentUser, dto, images);

    return ResponseEntity.ok(new ApiResponse<>(
        PRODUCT_CREATE_SUCCESS.getCode(),
        PRODUCT_CREATE_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 단일 상품 조회
   */
  @GetMapping("/{productId}")
  @CustomPreAuthorize(userRoleType = {ROLE_USER, ROLE_COMPANY, ROLE_MASTER})
  public ResponseEntity<ApiResponse<ResProductGetByProductIdDtoApiV1>> getProduct(
      @PathVariable UUID productId
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        PRODUCT_GET_SUCCESS.getCode(),
        PRODUCT_GET_SUCCESS.getMessage(),
        productService.getById(productId)
    ));
  }

  /**
   * 상품 목록 조회
   */
  @GetMapping
  @CustomPreAuthorize(userRoleType = {ROLE_USER, ROLE_COMPANY, ROLE_MASTER})
  public ResponseEntity<ApiResponse<ResProductGetDtoApiV1>> getProductList(
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(required = false) ProductStatus status,
      @RequestParam(required = false) String sortBy,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        PRODUCT_GET_LIST_SUCCESS.getCode(),
        PRODUCT_GET_LIST_SUCCESS.getMessage(),
        productService.getProductList(minPrice, maxPrice, status, sortBy, page, size)
    ));
  }


  /**
   * 상품 수정
   */
  @PutMapping("/{productId}")
  @CustomPreAuthorize(userRoleType = {ROLE_COMPANY, ROLE_MASTER})
  public ResponseEntity<ApiResponse<Void>> updateProduct(
      @PathVariable UUID productId,
      @Valid @RequestPart ReqProductPutByProductIdDtoApiV1 dto,
      @RequestPart List<MultipartFile> images,
      @CurrentUser CurrentUserDtoApiV1 currentUser
  ) {

    productService.updateProduct(currentUser, productId, dto, images);

    return ResponseEntity.ok(new ApiResponse<>(
        PRODUCT_UPDATE_SUCCESS.getCode(),
        PRODUCT_UPDATE_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 상품 삭제
   */
  @DeleteMapping("/{productId}")
  @CustomPreAuthorize(userRoleType = {ROLE_COMPANY, ROLE_MASTER})
  public ResponseEntity<ApiResponse<Void>> deleteProduct(
      @PathVariable UUID productId,
      @CurrentUser CurrentUserDtoApiV1 currentUser
  ) {

    productService.deleteProduct(currentUser, productId);

    return ResponseEntity.ok(new ApiResponse<>(
        PRODUCT_DELETE_SUCCESS.getCode(),
        PRODUCT_DELETE_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 재고 조회
   */
  @GetMapping("/{productId}/stock")
  @CustomPreAuthorize(userRoleType = {ROLE_COMPANY, ROLE_MASTER})
  public ResponseEntity<ApiResponse<ResProductGetStockDtoApiV1>> getProductStock(
      @PathVariable UUID productId,
      @CurrentUser CurrentUserDtoApiV1 currentUser
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        PRODUCT_GET_STOCK_SUCCESS.getCode(),
        PRODUCT_GET_STOCK_SUCCESS.getMessage(),
        productService.getProductStock(currentUser, productId)
    ));
  }

  /**
   * 재고 수정
   */
  @PatchMapping("/{productId}/stock")
  @CustomPreAuthorize(userRoleType = {ROLE_COMPANY, ROLE_MASTER})
  public ResponseEntity<ApiResponse<Void>> updateProductStock(
      @PathVariable UUID productId,
      @Valid @RequestBody ReqProductPatchByProductIdDtoApiV1 dto,
      @CurrentUser CurrentUserDtoApiV1 currentUser
  ) {

    productService.updateProductStock(currentUser, productId, dto);

    return ResponseEntity.ok(new ApiResponse<>(
        PRODUCT_UPDATE_STOCK_SUCCESS.getCode(),
        PRODUCT_UPDATE_STOCK_SUCCESS.getMessage(),
        null
    ));
  }

}
