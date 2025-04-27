package com.musinsam.eventservice.application.dto.response;

import com.musinsam.eventservice.domain.event.entity.EventEntity;
import com.musinsam.eventservice.domain.event.vo.EventStatus;
import java.time.ZonedDateTime;
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
public class ResEventGetDtoApiV1 {

  private EventPage eventPage;


  public static ResEventGetDtoApiV1 of(Page<EventEntity> eventEntityPage) {
    return ResEventGetDtoApiV1.builder()
        .eventPage(new EventPage(eventEntityPage))
        .build();
  }


  @Getter
  @ToString
  public static class EventPage extends PagedModel<EventPage.Event> {

    public EventPage(Page<EventEntity> eventEntityPage) {
      super(
          new PageImpl<>(
              Event.from(eventEntityPage.getContent()),
              eventEntityPage.getPageable(),
              eventEntityPage.getTotalElements()
          )
      );
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


      public static Event from(EventEntity eventEntity) {
        return Event.builder()
            .id(eventEntity.getId())
            .name(eventEntity.getName())
            .startTime(eventEntity.getStartTime())
            .endTime(eventEntity.getEndTime())
            .status(eventEntity.getStatus())
            .build();
      }

      public static List<Event> from(List<EventEntity> eventEntityList) {
        return eventEntityList.stream()
            .map(eventEntity -> Event.from(eventEntity))
            .toList();
      }

    }

  }
}
