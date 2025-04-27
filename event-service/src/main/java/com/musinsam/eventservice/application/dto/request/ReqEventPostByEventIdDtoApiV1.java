package com.musinsam.eventservice.application.dto.request;

import com.musinsam.eventservice.domain.event.entity.EventEntity;
import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReqEventPostByEventIdDtoApiV1 {

  @Valid
  @NotNull(message = "등록할 이벤트 상품 정보를 입력해주세요.")
  private EventProduct eventProduct;

  @Getter
  @NoArgsConstructor
  @Builder
  @AllArgsConstructor
  public static class EventProduct {

    @NotNull(message = "상품 ID를 입력해주세요.")
    private UUID productId;

    @NotNull(message = "할인율을 입력해주세요.")
    @Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
    @Max(value = 100, message = "할인율은 100 이하이어야 합니다.")
    private Integer discountRate;


    public EventProductEntity toEntity(EventEntity eventEntity) {
      return EventProductEntity.builder()
          .productId(productId)
          .discountRate(discountRate)
          .soldQuantity(0)
          .event(eventEntity)
          .build();
    }
  }

}
