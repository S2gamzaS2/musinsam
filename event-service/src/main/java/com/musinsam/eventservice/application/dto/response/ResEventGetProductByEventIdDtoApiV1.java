package com.musinsam.eventservice.application.dto.response;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import com.musinsam.eventservice.domain.event.vo.EventStatus;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResEventGetProductByEventIdDtoApiV1 {

  private EventProductPage eventProductPage;

  public static ResEventGetProductByEventIdDtoApiV1 of(
      Page<EventProductEntity> eventProductEntityPage) {
    return ResEventGetProductByEventIdDtoApiV1.builder()
        .eventProductPage(new EventProductPage(eventProductEntityPage))
        .build();
  }

  @Getter
  @ToString
  public static class EventProductPage extends PagedModel<EventProductPage.EventProduct> {

    public EventProductPage(Page<EventProductEntity> eventProductEntityPage) {
      super(
          new PageImpl<>(
              EventProduct.from(eventProductEntityPage.getContent()),
              eventProductEntityPage.getPageable(),
              eventProductEntityPage.getTotalElements()
          )
      );
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventProduct {

      private UUID id;
      private UUID eventId;
      private UUID productId;
      private Integer discountRate;
      private Integer soldQuantity;
      private EventStatus status;


      public static List<EventProduct> from(List<EventProductEntity> eventProductEntityList) {
        return eventProductEntityList.stream()
            .map(eventProductEntity -> EventProduct.from(eventProductEntity))
            .toList();
      }

      public static EventProduct from(EventProductEntity eventProductEntity) {
        return EventProduct.builder()
            .id(eventProductEntity.getId())
            .eventId(eventProductEntity.getEvent().getId())
            .productId(eventProductEntity.getProductId())
            .discountRate(eventProductEntity.getDiscountRate())
            .soldQuantity(eventProductEntity.getSoldQuantity())
            .status(eventProductEntity.getEvent().getStatus())
            .build();
      }
    }

  }

}
