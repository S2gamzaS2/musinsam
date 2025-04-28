package com.musinsam.eventservice.domain.event.repository;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import java.util.List;
import java.util.UUID;

public interface EventProductRepository {

  EventProductEntity save(EventProductEntity eventProductEntity);

  List<EventProductEntity> findByEventIdAndDeletedAtIsNull(UUID eventId);
}
