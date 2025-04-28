package com.musinsam.eventservice.infrastructure.persistence;

import com.musinsam.eventservice.domain.event.entity.EventEntity;
import com.musinsam.eventservice.domain.event.repository.EventRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<EventEntity, UUID>, EventRepository {

}
