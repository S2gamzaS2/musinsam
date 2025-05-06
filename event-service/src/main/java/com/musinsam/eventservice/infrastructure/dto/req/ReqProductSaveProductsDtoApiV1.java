package com.musinsam.eventservice.infrastructure.dto.req;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import java.time.ZonedDateTime;
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
  private ZonedDateTime endTime;

  public static ReqProductSaveProductsDtoApiV1 of(List<EventProductEntity> eventProductEntityList,
      ZonedDateTime endTime) {
    return ReqProductSaveProductsDtoApiV1.builder()
        .eventProductList(EventProduct.from(eventProductEntityList))
        .endTime(endTime)
        .build();
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EventProduct {

    private UUID productId;
    private Integer discountRate;

    public static EventProduct from(EventProductEntity eventProductEntity) {
      return EventProduct.builder()
          .productId(eventProductEntity.getProductId())
          .discountRate(eventProductEntity.getDiscountRate())
          .build();
    }

    public static List<EventProduct> from(List<EventProductEntity> eventProductEntityList) {
      return eventProductEntityList.stream()
          .map(eventProductEntity -> EventProduct.from(eventProductEntity))
          .toList();
    }
  }

}
