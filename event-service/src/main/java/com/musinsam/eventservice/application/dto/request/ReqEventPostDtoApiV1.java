package com.musinsam.eventservice.application.dto.request;

import com.musinsam.eventservice.domain.event.entity.EventEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqEventPostDtoApiV1 {

  @Valid
  @NotNull(message = "이벤트 정보를 입력해주세요.")
  private Event event;


  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Event {

    @NotBlank(message = "이벤트 이름을 입력해주세요.")
    private String name;

    @NotNull(message = "이벤트 시작 일시를 입력해주세요.")
    private ZonedDateTime startTime;

    @NotNull(message = "이벤트 종료 일시를 입력해주세요.")
    private ZonedDateTime endTime;


    public EventEntity toEntity() {
      return EventEntity.builder()
          .name(name)
          .startTime(startTime)
          .endTime(endTime)
          .build();
    }

  }

}
