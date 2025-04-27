package com.musinsam.eventservice.domain.event.repository;

import com.musinsam.eventservice.domain.event.entity.EventProductEntity;

public interface EventProductRepository {

  EventProductEntity save(EventProductEntity eventProductEntity);
}
