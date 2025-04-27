package com.musinsam.eventservice.application.dto.response;

import com.musinsam.eventservice.domain.event.entity.EventEntity;
import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import com.musinsam.eventservice.domain.event.vo.EventStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResEventGetByEventIdDtoApiV1 {

  private Event event;

  public static ResEventGetByEventIdDtoApiV1 of(EventEntity eventEntity,
      List<EventProductEntity> eventProductEntityList) {
    return ResEventGetByEventIdDtoApiV1.builder()
        .event(Event.from(eventEntity, eventProductEntityList))
        .build();
  }


  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Event {

    private UUID id;
    private String name;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private EventStatus status;
    private List<EventProduct> eventProduct;


    public static Event from(EventEntity eventEntity,
        List<EventProductEntity> eventProductEntityList) {
      return Event.builder()
          .id(eventEntity.getId())
          .name(eventEntity.getName())
          .startTime(eventEntity.getStartTime())
          .endTime(eventEntity.getEndTime())
          .status(eventEntity.getStatus())
          .eventProduct(EventProduct.from(eventProductEntityList))
          .build();
    }

    @Builder
    public static class EventProduct {

      private UUID productId;
      private String productName;
      private BigDecimal salePrice;
      private BigDecimal originPrice;
      private Integer discountRate;
      private Integer soldQuantity;

      public static EventProduct from(EventProductEntity eventProductEntity) {
        return EventProduct.builder()
            .productId(eventProductEntity.getProductId())
            .discountRate(eventProductEntity.getDiscountRate())
            .soldQuantity(eventProductEntity.getSoldQuantity())
            .build();
      }

      public static List<EventProduct> from(List<EventProductEntity> eventProductEntityList) {
        return eventProductEntityList.stream()
            .map(eventProductEntity -> EventProduct.from(eventProductEntity))
            .toList();
      }
    }

  }

}
