package com.musinsam.productservice.presentation.controller;

import com.musinsam.productservice.application.service.V2.ProductServiceApiV2;
import com.musinsam.productservice.infrastructure.dto.req.ReqProductSaveProductsDtoApiV1;
import com.musinsam.productservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/v1/products")
@RequiredArgsConstructor
public class ProductInternalControllerApiV1 {

  private final ProductServiceApiV2 productService;

  /**
   * 주문 가능 확인 및 재고 감소
   */
  @PutMapping("/stock/reduce")
  public ResponseEntity<Boolean> checkAndReduceStock(
      @RequestParam UUID productId,
      @RequestParam Integer quantity
  ) {
    return ResponseEntity.ok().body(productService.checkAndReduceStock(productId, quantity));
  }

  /**
   * 재고 복구
   */
  @PutMapping("/stock/restore")
  public ResponseEntity<Void> updateProductStock(
      @RequestParam UUID productId,
      @RequestParam Integer quantity
  ) {
    productService.restoreStock(productId, quantity);
    return ResponseEntity.ok().build();
  }

  /**
   * 상품 정보 - Event
   */
  @GetMapping("/{productId}")
  public ResponseEntity<ResProductInfoGetByProductId> getProductInfo(
      @PathVariable UUID productId
  ) {
    return ResponseEntity.ok().body(productService.getProductInfo(productId));
  }

  /**
   * 레디스 저장 - Event
   */
  @PostMapping("/save")
  public ResponseEntity<Void> saveProductsToRedis(@RequestBody ReqProductSaveProductsDtoApiV1 dto) {
    productService.saveProductsToRedis(dto);
    return ResponseEntity.ok().build();
  }

  /**
   * 할인률 수정 - Event
   */
  @PostMapping("/update-discount-rate")
  public ResponseEntity<Void> updateDiscountRate(@RequestParam UUID productId,
      @RequestParam Integer discountRate) {
    log.info("********** updateDiscountRate 컨트롤러 호출 완료");
    productService.updateDiscountRate(productId, discountRate);
    return ResponseEntity.ok().build();
  }

  /**
   * 이벤트 상품 삭제
   */
  @PostMapping("/delete")
  public ResponseEntity<Void> deleteEventProduct(@RequestParam UUID productId) {
    log.info("********** deleteEventProduct 컨트롤러 호출 완료");
    productService.deleteEventProduct(productId);
    return ResponseEntity.ok().build();
  }

}
