package com.musinsam.eventservice.application.service;

import com.musinsam.common.user.CurrentUserDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetDtoApiV1;
import com.musinsam.eventservice.domain.event.entity.EventEntity;
import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import com.musinsam.eventservice.domain.event.repository.EventProductRepository;
import com.musinsam.eventservice.domain.event.repository.EventRepository;
import com.musinsam.eventservice.domain.event.vo.EventStatus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

  @Override
  public ResEventGetDtoApiV1 getEventList(CurrentUserDtoApiV1 currentUser, Boolean active, int page,
      int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<EventEntity> eventEntityPage;

    if (active != null && active) {
      eventEntityPage = eventRepository.findByStatusAndDeletedAtIsNull(EventStatus.ACTIVE,
          pageable);
    } else {
      eventEntityPage = eventRepository.findByDeletedAtIsNull(pageable);
    }

    return ResEventGetDtoApiV1.of(eventEntityPage);
  }

  @Override
  public ResEventGetByEventIdDtoApiV1 getEvent(UUID eventId, CurrentUserDtoApiV1 currentUser) {

    EventEntity eventEntity = eventRepository.findByIdAndDeletedAtIsNull(eventId)
        .orElseThrow(() -> new RuntimeException("해딩 이벤트 없음"));

    List<EventProductEntity> eventProductEntityList = eventProductRepository.findByEventIdAndDeletedAtIsNull(
        eventId);

    return ResEventGetByEventIdDtoApiV1.of(eventEntity, eventProductEntityList);
  }
}
