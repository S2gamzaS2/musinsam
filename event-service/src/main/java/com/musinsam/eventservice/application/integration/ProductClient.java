package com.musinsam.eventservice.application.integration;

import com.musinsam.eventservice.infrastructure.dto.req.ReqProductSaveProductsDtoApiV1;
import com.musinsam.eventservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import java.util.UUID;

public interface ProductClient {

  ResProductInfoGetByProductId getProductInfo(UUID productId);

  void saveProductsToRedis(ReqProductSaveProductsDtoApiV1 dto);

  void updateDiscountRate(UUID productId, Integer discountRate);

  void deleteEventProduct(UUID productId);
}
