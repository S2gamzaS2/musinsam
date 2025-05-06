package com.musinsam.eventservice.domain.event.repository;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventProductRepository {

  EventProductEntity save(EventProductEntity eventProductEntity);

  List<EventProductEntity> findByEventIdAndDeletedAtIsNull(UUID eventId);

  Page<EventProductEntity> findByEventIdAndDeletedAtIsNull(UUID eventId, Pageable pageable);

  Optional<EventProductEntity> findByIdAndDeletedAtIsNull(UUID eventProductId);

  Optional<EventProductEntity> findByProductIdAndDeletedAtIsNull(UUID productId);

  boolean existsByEventIdAndProductIdAndDeletedAtIsNull(UUID eventId,
      @NotNull(message = "상품 ID를 입력해주세요.") UUID productId);
}
