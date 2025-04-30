package com.musinsam.productservice.application.integration;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EventClientApiV1 {

  LocalDateTime getEndTime(UUID productId);

  Integer getDiscountRate(UUID productId);
}
