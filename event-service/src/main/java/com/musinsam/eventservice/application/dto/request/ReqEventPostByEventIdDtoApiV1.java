package com.musinsam.eventservice.application.dto.request;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import jakarta.validation.Valid;
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

  public static class EventProduct {

    @NotNull(message = "상품 ID를 입력해주세요.")
    private UUID productId;

    @NotNull(message = "할인율을 입력해주세요.")
    private Integer discountRate;


    public EventProductEntity toEntity() {
      return EventProductEntity.builder()
          .productId(productId)
          .discountRate(discountRate)
          .build();
    }
  }

}
