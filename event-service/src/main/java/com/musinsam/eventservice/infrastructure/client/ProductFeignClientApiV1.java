package com.musinsam.eventservice.infrastructure.client;

import com.musinsam.eventservice.application.integration.ProductClient;
import com.musinsam.eventservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductFeignClientApiV1 extends ProductClient {

  // 상품 정보
  @GetMapping("/internal/v1/products/{productId}")
  ResProductInfoGetByProductId getProductInfo(@PathVariable UUID productId);

}
