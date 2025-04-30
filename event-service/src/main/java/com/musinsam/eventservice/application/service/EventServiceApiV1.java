package com.musinsam.eventservice.application.service;

import com.musinsam.common.user.CurrentUserDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPutByEventProductIdDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPutDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetProductByEventIdDtoApiV1;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EventServiceApiV1 {

  void createEvent(@Valid ReqEventPostDtoApiV1 dto, CurrentUserDtoApiV1 currentUser);

  void addEventProduct(UUID eventId, @Valid ReqEventPostByEventIdDtoApiV1 dto);

  ResEventGetDtoApiV1 getEventList(CurrentUserDtoApiV1 currentUser, Boolean active, int page,
      int size);

  ResEventGetByEventIdDtoApiV1 getEvent(UUID eventId, CurrentUserDtoApiV1 currentUser);

  void updateEvent(UUID eventId, @Valid ReqEventPutDtoApiV1 dto, CurrentUserDtoApiV1 currentUser);

  void deleteEvent(UUID eventId, CurrentUserDtoApiV1 currentUser);

  ResEventGetProductByEventIdDtoApiV1 getEventProductList(UUID eventId,
      CurrentUserDtoApiV1 currentUser, int page, int size);

  void updateEventProduct(UUID eventId, UUID eventProductId, CurrentUserDtoApiV1 currentUser,
      @Valid ReqEventPutByEventProductIdDtoApiV1 dto);

  void deleteEventProduct(UUID eventId, UUID eventProductId, CurrentUserDtoApiV1 currentUser);

  void startEvent(UUID eventId);

  LocalDateTime getEndTime(UUID productId);

  Integer getDiscountRate(UUID productId);
}
