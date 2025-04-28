package com.musinsam.eventservice.domain.event.repository;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventProductRepository {

  EventProductEntity save(EventProductEntity eventProductEntity);

  List<EventProductEntity> findByEventIdAndDeletedAtIsNull(UUID eventId);

  Page<EventProductEntity> findByEventIdAndDeletedAtIsNull(UUID eventId, Pageable pageable);

  Optional<EventProductEntity> findByIdAndEventIdAndDeletedAtIsNull(UUID eventProductId,
      UUID eventId);
}
