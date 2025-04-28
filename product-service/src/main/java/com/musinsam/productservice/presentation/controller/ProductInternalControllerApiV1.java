package com.musinsam.productservice.presentation.controller;

import com.musinsam.productservice.application.service.ProductServiceApiV1;
import com.musinsam.productservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/products")
@RequiredArgsConstructor
public class ProductInternalControllerApiV1 {

  private final ProductServiceApiV1 productService;

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

}
