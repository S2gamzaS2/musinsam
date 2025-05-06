package com.musinsam.eventservice.domain.event.repository;

import com.musinsam.eventservice.domain.event.entity.EventEntity;
import com.musinsam.eventservice.domain.event.vo.EventStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepository {

  EventEntity save(EventEntity eventEntity);

  Optional<EventEntity> findByIdAndDeletedAtIsNull(UUID eventId);

  Page<EventEntity> findByDeletedAtIsNull(Pageable pageable);

  Page<EventEntity> findByStatusAndDeletedAtIsNull(EventStatus eventStatus, Pageable pageable);

  List<EventEntity> findByStartTimeBeforeAndStatus(ZonedDateTime now, EventStatus eventStatus);

  List<EventEntity> findByEndTimeBeforeAndStatus(ZonedDateTime now, EventStatus eventStatus);
}
