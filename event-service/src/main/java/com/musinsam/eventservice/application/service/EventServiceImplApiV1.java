package com.musinsam.eventservice.application.service;

import com.musinsam.common.user.CurrentUserDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPutByEventProductIdDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPutDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetProductByEventIdDtoApiV1;
import com.musinsam.eventservice.application.integration.ProductClient;
import com.musinsam.eventservice.domain.event.entity.EventEntity;
import com.musinsam.eventservice.domain.event.entity.EventProductEntity;
import com.musinsam.eventservice.domain.event.repository.EventProductRepository;
import com.musinsam.eventservice.domain.event.repository.EventRepository;
import com.musinsam.eventservice.domain.event.vo.EventStatus;
import com.musinsam.eventservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import java.time.ZoneId;
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
  private final ProductClient productClient;

  @Override
  @Transactional
  public void createEvent(ReqEventPostDtoApiV1 dto, CurrentUserDtoApiV1 currentUser) {

    EventEntity eventEntity = dto.getEvent().toEntity();
    eventRepository.save(eventEntity);
  }

  @Override
  @Transactional
  public void addEventProduct(UUID eventId, ReqEventPostByEventIdDtoApiV1 dto) {

    EventEntity eventEntity = findEventEntityById(eventId);

    if (eventProductRepository.existsByProductIdAndDeletedAtIsNull(
        dto.getEventProduct().getProductId())) {
      throw new RuntimeException("이미 등록된 상품입니다.");
    }

    ResProductInfoGetByProductId productInfo = productClient.getProductInfo(
        dto.getEventProduct().getProductId());
    String productName = productInfo.getProduct().getName();

    EventProductEntity eventProductEntity = dto.getEventProduct()
        .toEntity(eventEntity, productName);
    eventProductRepository.save(eventProductEntity);
  }

  @Override
  @Transactional(readOnly = true)
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
  @Transactional(readOnly = true)
  public ResEventGetByEventIdDtoApiV1 getEvent(UUID eventId, CurrentUserDtoApiV1 currentUser) {

    EventEntity eventEntity = findEventEntityById(eventId);

    List<EventProductEntity> eventProductEntityList = eventProductRepository.findByEventIdAndDeletedAtIsNull(
        eventId);

    return ResEventGetByEventIdDtoApiV1.of(eventEntity, eventProductEntityList);
  }

  @Override
  @Transactional
  public void updateEvent(UUID eventId, ReqEventPutDtoApiV1 dto, CurrentUserDtoApiV1 currentUser) {

    EventEntity eventEntity = findEventEntityById(eventId);

    dto.getEvent().updateOf(eventEntity);
  }

  @Override
  @Transactional
  public void deleteEvent(UUID eventId, CurrentUserDtoApiV1 currentUser) {

    EventEntity eventEntity = findEventEntityById(eventId);

    eventEntity.softDelete(currentUser.userId(), ZoneId.systemDefault());

    List<EventProductEntity> eventProductEntityList = eventProductRepository.findByEventIdAndDeletedAtIsNull(
        eventId);

    if (!eventProductEntityList.isEmpty()) {
      for (EventProductEntity eventProduct : eventProductEntityList) {
        eventProduct.softDelete(currentUser.userId(), ZoneId.systemDefault());
      }
    }

  }

  @Override
  @Transactional(readOnly = true)
  public ResEventGetProductByEventIdDtoApiV1 getEventProductList(UUID eventId,
      CurrentUserDtoApiV1 currentUser, int page, int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<EventProductEntity> eventProductEntityPage = eventProductRepository.findByEventIdAndDeletedAtIsNull(
        eventId, pageable);
    return ResEventGetProductByEventIdDtoApiV1.of(eventProductEntityPage);
  }

  @Override
  @Transactional
  public void updateEventProduct(UUID eventId, UUID eventProductId, CurrentUserDtoApiV1 currentUser,
      ReqEventPutByEventProductIdDtoApiV1 dto) {

    EventProductEntity eventProductEntity = findEventProductEntityByIdAndEventId(eventProductId,
        eventId);

    dto.getEventProduct().updateOf(eventProductEntity);
  }

  @Override
  @Transactional
  public void deleteEventProduct(UUID eventId, UUID eventProductId,
      CurrentUserDtoApiV1 currentUser) {

    EventProductEntity eventProductEntity = findEventProductEntityByIdAndEventId(eventProductId,
        eventId);

    eventProductEntity.softDelete(currentUser.userId(), ZoneId.systemDefault());

  }

  private EventEntity findEventEntityById(UUID eventId) {
    return eventRepository.findByIdAndDeletedAtIsNull(eventId)
        .orElseThrow(() -> new RuntimeException("해딩 이벤트 없음"));
  }

  private EventProductEntity findEventProductEntityByIdAndEventId(UUID id, UUID eventId) {
    return eventProductRepository.findByIdAndEventIdAndDeletedAtIsNull(
            id, eventId)
        .orElseThrow(() -> new RuntimeException("해당 이벤트 상품 없음"));
  }
}
