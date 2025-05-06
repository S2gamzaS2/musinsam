package com.musinsam.eventservice.infrastructure.client;

import com.musinsam.eventservice.application.integration.ProductClient;
import com.musinsam.eventservice.infrastructure.dto.req.ReqProductDeleteEventDto;
import com.musinsam.eventservice.infrastructure.dto.req.ReqProductSaveProductsDtoApiV1;
import com.musinsam.eventservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductFeignClientApiV1 extends ProductClient {

  // 상품 정보
  @GetMapping("/internal/v1/products/{productId}")
  ResProductInfoGetByProductId getProductInfo(@PathVariable UUID productId);

  // 레디스에 이벤트 상품 저장
  @PostMapping("/internal/v1/products/save")
  void saveProductsToRedis(@RequestBody ReqProductSaveProductsDtoApiV1 dto);

  // 할인율 수정
  @PostMapping("/internal/v1/products/update-discount-rate")
  void updateDiscountRate(@RequestParam UUID productId,
      @RequestParam Integer discountRate);

  // 이벤트 상품 삭제
  @PostMapping("/internal/v1/products/delete")
  void deleteEventProduct(@RequestParam UUID productId);

  // 이벤트 삭제
  @PostMapping("/internal/v1/products/close-event")
  void closeEvent(@RequestBody ReqProductDeleteEventDto dto);

}
