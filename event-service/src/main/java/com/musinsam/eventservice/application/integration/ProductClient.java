package com.musinsam.eventservice.application.integration;

import com.musinsam.eventservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import java.util.UUID;

public interface ProductClient {

  ResProductInfoGetByProductId getProductInfo(UUID productId);

}
