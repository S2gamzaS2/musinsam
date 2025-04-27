package com.musinsam.eventservice.application.dto.request;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqEventPutByEventProductIdDtoApiV1 {

  @Valid
  @NotNull(message = "수정할 이벤트 상품 정보를 입력하세요.")
  private EventProduct eventProduct;


  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class EventProduct {

    private Integer discountRate;

    public void updateOf(EventProductEntity eventProductEntity) {
      eventProductEntity.setDiscountRate(discountRate);
    }
  }

}
