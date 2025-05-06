package com.musinsam.eventservice.application.scheduler;

import com.musinsam.eventservice.application.service.EventServiceApiV1;
import com.musinsam.eventservice.domain.event.entity.EventEntity;
import com.musinsam.eventservice.domain.event.repository.EventRepository;
import com.musinsam.eventservice.domain.event.vo.EventStatus;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Slf4j
@RequiredArgsConstructor
public class EventRecoveryRunner implements ApplicationRunner {

  private final EventRepository eventRepository;
  private final EventServiceApiV1 eventService;

  // 애플리케이션 시작 시 실행되는 메서드
  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("서버 재시작 - 미처리 이벤트 복구 시작");

    ZonedDateTime now = ZonedDateTime.now();

    // 1. 시작되어야 하지만 아직 SCHEDULED 상태인 이벤트
    List<EventEntity> missedStartEventList = eventRepository.findByStartTimeBeforeAndStatus(
        now,
        EventStatus.SCHEDULED);
    log.info("미처리 시작 이벤트 수: {}", missedStartEventList.size());

    for (EventEntity event : missedStartEventList) {
      log.info("미처리 이벤트 시작 복구: {}, (ID: {})", event.getName(), event.getId());

      try {
        eventService.startEvent(event.getId());
      } catch (Exception e) {
        log.error("이벤트 시작 복구 중 오류: {}", event.getId(), e);
      }
    }

    // 2. 종료되어야 하지만 아직 ACTIVE 상태인 이벤트
    List<EventEntity> missedEndEventList = eventRepository.findByEndTimeBeforeAndStatus(now,
        EventStatus.ACTIVE);
    log.info("미처리 종료 이벤트 수: {}", missedEndEventList.size());

    for (EventEntity event : missedEndEventList) {
      log.info("미처리 이벤트 종료 복구: {}, (ID: {})", event.getName(), event.getId());

      try {
        eventService.endEvent(event.getId());
      } catch (Exception e) {
        log.error("이벤트 종료 복구 중 오류: {}", event.getId(), e);
      }
    }

    log.info("미처리 이벤트 복구 완료");
  }
}
