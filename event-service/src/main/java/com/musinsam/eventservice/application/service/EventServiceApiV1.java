package com.musinsam.eventservice.application.service;

import com.musinsam.common.user.CurrentUserDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetDtoApiV1;
import jakarta.validation.Valid;
import java.util.UUID;

public interface EventServiceApiV1 {

  void createEvent(@Valid ReqEventPostDtoApiV1 dto, CurrentUserDtoApiV1 currentUser);

  void addEventProduct(UUID eventId, @Valid ReqEventPostByEventIdDtoApiV1 dto);

  ResEventGetDtoApiV1 getEventList(CurrentUserDtoApiV1 currentUser, Boolean active, int page,
      int size);

  ResEventGetByEventIdDtoApiV1 getEvent(UUID eventId, CurrentUserDtoApiV1 currentUser);
}
