package com.musinsam.eventservice.domain.event.repository;

import com.musinsam.eventservice.domain.event.entity.EventEntity;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

  EventEntity save(EventEntity eventEntity);

  Optional<EventEntity> findByIdAndDeletedAtIsNull(UUID eventId);
}
