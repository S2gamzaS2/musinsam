package com.musinsam.productservice.infrastructure.dto.req;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqProductSaveProductsDtoApiV1 {

  private List<EventProduct> eventProductList;
  private LocalDateTime endTime;

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EventProduct {

    private UUID productId;
    private Integer discountRate;
  }
}
