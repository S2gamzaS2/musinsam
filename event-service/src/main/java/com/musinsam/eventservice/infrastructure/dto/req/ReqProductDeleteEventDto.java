package com.musinsam.eventservice.infrastructure.dto.req;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
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
public class ReqProductDeleteEventDto {

  List<UUID> productIdList;

  public static ReqProductDeleteEventDto of(List<EventProductEntity> eventProductEntityList) {
    return ReqProductDeleteEventDto.builder()
        .productIdList(eventProductEntityList.stream()
            .map(eventPRoductEntity -> eventPRoductEntity.getProductId()).toList())
        .build();
  }

}
