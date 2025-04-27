package com.musinsam.eventservice.domain.event.repository;

import com.musinsam.eventservice.domain.event.entity.EventEntity;

public interface EventRepository {

  EventEntity save(EventEntity eventEntity);
}
