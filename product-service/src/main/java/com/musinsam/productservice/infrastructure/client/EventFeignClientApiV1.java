package com.musinsam.productservice.infrastructure.client;

import com.musinsam.productservice.application.integration.EventClientApiV1;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "event-service")
public interface EventFeignClientApiV1 extends EventClientApiV1 {

  @GetMapping("/internal/v1/events")
  LocalDateTime getEndTime(@RequestParam UUID productId);

  @GetMapping("/internal/v1/events/discount-rate")
  Integer getDiscountRate(@RequestParam UUID productId);

}
