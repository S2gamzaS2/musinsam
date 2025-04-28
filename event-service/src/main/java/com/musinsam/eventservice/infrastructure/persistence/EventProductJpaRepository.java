package com.musinsam.eventservice.infrastructure.persistence;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import com.musinsam.eventservice.domain.event.repository.EventProductRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventProductJpaRepository extends JpaRepository<EventProductEntity, UUID>,
    EventProductRepository {

}
