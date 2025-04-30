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
import com.musinsam.eventservice.infrastructure.dto.req.ReqProductSaveProductsDtoApiV1;
import com.musinsam.eventservice.infrastructure.dto.res.ResProductInfoGetByProductId;
import feign.FeignException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImplApiV1 implements EventServiceApiV1 {

  private final EventRepository eventRepository;
  private final EventProductRepository eventProductRepository;
  private final ProductClient productClient;
  private final ValueOperations<String, ResEventGetByEventIdDtoApiV1> valueOps;
  private final RedisTemplate<String, ResEventGetByEventIdDtoApiV1> redisTemplate;

  private final static String EVENT_CACHE_KEY_PREFIX = "event:";

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

    ResProductInfoGetByProductId productInfo;
    try {
      productInfo = productClient.getProductInfo(
          dto.getEventProduct().getProductId());
    } catch (FeignException e) {
      throw new RuntimeException("상품을 등록할 수 없음돠 아마도 상품이 없음");
    }

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

    String redisKey = buildEventCacheKey(eventId);
    ResEventGetByEventIdDtoApiV1 cachedEvent = valueOps.get(redisKey);

    if (cachedEvent != null) {
      log.info("########## 캐시된 이벤트 있음 레디스조회");
      return cachedEvent;
    }

    log.info("########## 캐시된 이벤트 없음 DB 조회");
    EventEntity eventEntity = findEventEntityById(eventId);
    List<EventProductEntity> eventProductEntityList = getEventProductEntityList(eventId);

    return ResEventGetByEventIdDtoApiV1.of(eventEntity, eventProductEntityList);
  }


  @Override
  @Transactional
  public void updateEvent(UUID eventId, ReqEventPutDtoApiV1 dto, CurrentUserDtoApiV1 currentUser) {

    EventEntity eventEntity = findEventEntityById(eventId);
    List<EventProductEntity> eventProductEntityList = getEventProductEntityList(eventId);
    dto.getEvent().updateOf(eventEntity);

    updateEventCache(eventId, eventEntity, eventProductEntityList);
  }


  @Override
  @Transactional
  public void deleteEvent(UUID eventId, CurrentUserDtoApiV1 currentUser) {

    EventEntity eventEntity = findEventEntityById(eventId);
    eventEntity.softDelete(currentUser.userId(), ZoneId.systemDefault());
    List<EventProductEntity> eventProductEntityList = getEventProductEntityList(eventId);

    if (!eventProductEntityList.isEmpty()) {
      for (EventProductEntity eventProduct : eventProductEntityList) {
        eventProduct.softDelete(currentUser.userId(), ZoneId.systemDefault());
      }
    }

    String redisKey = buildEventCacheKey(eventId);
    ResEventGetByEventIdDtoApiV1 cachedEvent = valueOps.get(redisKey);
    if (cachedEvent != null) {
      redisTemplate.delete(redisKey);
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

    EventProductEntity eventProductEntity = findEventProductEntityById(eventProductId);
    dto.getEventProduct().updateOf(eventProductEntity);

    EventEntity eventEntity = findEventEntityById(eventId);
    List<EventProductEntity> eventProductEntityList = getEventProductEntityList(eventId);

    String redisKey = buildEventCacheKey(eventId);
    ResEventGetByEventIdDtoApiV1 cachedEvent = valueOps.get(redisKey);

    if (isEventProductInCache(eventProductEntity.getProductId(), cachedEvent)) {
      updateEventCache(eventId, eventEntity, eventProductEntityList);
      log.info("######### productClient.updateDiscountRate 실행");
      productClient.updateDiscountRate(eventProductEntity.getProductId(),
          eventProductEntity.getDiscountRate());
      log.info("######### productClient.updateDiscountRate 종료");
    }
  }


  @Override
  @Transactional
  public void deleteEventProduct(UUID eventId, UUID eventProductId,
      CurrentUserDtoApiV1 currentUser) {

    String redisKey = buildEventCacheKey(eventId);
    ResEventGetByEventIdDtoApiV1 cachedEvent = valueOps.get(redisKey);

    EventProductEntity eventProductEntity = findEventProductEntityById(eventProductId);
    eventProductEntity.softDelete(currentUser.userId(), ZoneId.systemDefault());

    EventEntity eventEntity = findEventEntityById(eventId);
    List<EventProductEntity> eventProductEntityList = getEventProductEntityList(eventId);

    log.info("DB 수정");

    if (isEventProductInCache(eventProductEntity.getProductId(), cachedEvent)) {
      updateEventCache(eventId, eventEntity, eventProductEntityList);
      productClient.deleteEventProduct(eventProductEntity.getProductId());
    }

  }


  // 이벤트 시작 시
  @Override
  @Transactional
  public void startEvent(UUID eventId) {

    EventEntity eventEntity = findEventEntityById(eventId);
    List<EventProductEntity> eventProductEntityList = getEventProductEntityList(eventId);

    eventEntity.setStatus(EventStatus.ACTIVE);

    String redisKey = buildEventCacheKey(eventId);

    LocalDateTime endTime = eventEntity.getEndTime().toLocalDateTime();
    long ttlInSeconds = Duration.between(LocalDateTime.now(), endTime).getSeconds();

    valueOps.set(redisKey, ResEventGetByEventIdDtoApiV1.of(eventEntity, eventProductEntityList),
        ttlInSeconds, TimeUnit.SECONDS);

    log.info("########## 이벤트 레디스 저장 완료");

    // Product service에 레디스에 저장하라고 요청
    productClient.saveProductsToRedis(
        ReqProductSaveProductsDtoApiV1.of(eventProductEntityList, endTime));

    log.info("########## 상품서비스 레디스 저장 완료");
  }


  /**
   * feign
   **/
  @Override
  public LocalDateTime getEndTime(UUID productId) {

    EventProductEntity eventProductEntity = eventProductRepository.findByProductIdAndDeletedAtIsNull(
            productId)
        .orElseThrow(() -> new RuntimeException("해당 이벤트 상품이 없음"));

    return eventProductEntity.getEvent().getEndTime().toLocalDateTime();
  }


  @Override
  public Integer getDiscountRate(UUID productId) {

    EventProductEntity eventProductEntity = eventProductRepository.findByProductIdAndDeletedAtIsNull(
            productId)
        .orElseThrow(() -> new RuntimeException("해당 이벤트 상품이 없음"));

    return eventProductEntity.getDiscountRate();
  }


  /**
   * 유틸리티 메서드
   **/
  // 이벤트 엔티티 조회
  private EventEntity findEventEntityById(UUID eventId) {
    return eventRepository.findByIdAndDeletedAtIsNull(eventId)
        .orElseThrow(() -> new RuntimeException("해딩 이벤트 없음"));
  }

  // 이벤트 상품 조회
  private EventProductEntity findEventProductEntityById(UUID id) {
    return eventProductRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new RuntimeException("해당 이벤트 상품 없음"));
  }

  // 이벤트 상품 리스트 조회
  private List<EventProductEntity> getEventProductEntityList(UUID eventId) {
    return eventProductRepository.findByEventIdAndDeletedAtIsNull(
        eventId);
  }

  // 레디스 키 생성
  private String buildEventCacheKey(UUID eventId) {
    return EVENT_CACHE_KEY_PREFIX + eventId.toString();
  }

  // 캐시의 이벤트상품에 상품이 포함되어있는지
  private boolean isEventProductInCache(UUID productId,
      ResEventGetByEventIdDtoApiV1 cachedEvent) {

    log.info("######## productId: {}", productId);

    if (cachedEvent == null) {
      return false;
    }

    List<ResEventGetByEventIdDtoApiV1.Event.EventProduct> eventProductList = cachedEvent.getEvent()
        .getEventProduct();
    if (eventProductList == null) {
      return false;
    }

    for (ResEventGetByEventIdDtoApiV1.Event.EventProduct product : eventProductList) {
      log.info("######## product.getProductId: {}", product.getProductId());
      if (productId.equals(product.getProductId())) {
        return true;
      }
    }

    return false;
  }

  // 이벤트 캐시 업데이트
  private void updateEventCache(UUID eventId, EventEntity eventEntity,
      List<EventProductEntity> eventProductEntityList) {
    String redisKey = buildEventCacheKey(eventId);
    ResEventGetByEventIdDtoApiV1 cachedEvent = valueOps.get(redisKey);

    if (cachedEvent != null) {
      LocalDateTime endTime = eventEntity.getEndTime().toLocalDateTime();
      long ttlInSeconds = Duration.between(LocalDateTime.now(), endTime).getSeconds();
      valueOps.set(redisKey, ResEventGetByEventIdDtoApiV1.of(eventEntity, eventProductEntityList),
          ttlInSeconds, TimeUnit.SECONDS);
    }
  }
}
