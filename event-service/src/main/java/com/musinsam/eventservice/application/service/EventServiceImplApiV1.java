package com.musinsam.eventservice.application.service;

import com.musinsam.common.user.CurrentUserDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostDtoApiV1;
import com.musinsam.eventservice.domain.event.entity.EventEntity;
import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import com.musinsam.eventservice.domain.event.repository.EventProductRepository;
import com.musinsam.eventservice.domain.event.repository.EventRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventServiceImplApiV1 implements EventServiceApiV1 {

  private final EventRepository eventRepository;
  private final EventProductRepository eventProductRepository;

  @Override
  @Transactional
  public void createEvent(ReqEventPostDtoApiV1 dto, CurrentUserDtoApiV1 currentUser) {

    EventEntity eventEntity = dto.getEvent().toEntity();
    eventRepository.save(eventEntity);
  }

  @Override
  @Transactional
  public void addEventProduct(UUID eventId, ReqEventPostByEventIdDtoApiV1 dto) {

    EventEntity event = eventRepository.findByIdAndDeletedAtIsNull(eventId)
        .orElseThrow(() -> new RuntimeException());

    //TODO: Product와 통신하여 해당 상품이 있는지 확인 & 이름 가져오기?

    EventProductEntity eventProductEntity = dto.getEventProduct().toEntity(event);

    eventProductRepository.save(eventProductEntity);
  }
}
