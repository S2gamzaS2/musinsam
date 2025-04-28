package com.musinsam.eventservice.presentation.controller;

import static com.musinsam.common.user.UserRoleType.ROLE_COMPANY;
import static com.musinsam.common.user.UserRoleType.ROLE_MASTER;
import static com.musinsam.common.user.UserRoleType.ROLE_USER;
import static com.musinsam.eventservice.global.config.EventResponseCode.EVENT_ADD_PRODUCT_SUCCESS;
import static com.musinsam.eventservice.global.config.EventResponseCode.EVENT_CREATE_SUCCESS;
import static com.musinsam.eventservice.global.config.EventResponseCode.EVENT_DELETE_PRODUCT_SUCCESS;
import static com.musinsam.eventservice.global.config.EventResponseCode.EVENT_DELETE_SUCCESS;
import static com.musinsam.eventservice.global.config.EventResponseCode.EVENT_GET_LIST_SUCCESS;
import static com.musinsam.eventservice.global.config.EventResponseCode.EVENT_GET_PRODUCT_LIST_SUCCESS;
import static com.musinsam.eventservice.global.config.EventResponseCode.EVENT_GET_SUCCESS;
import static com.musinsam.eventservice.global.config.EventResponseCode.EVENT_UPDATE_PRODUCT_SUCCESS;
import static com.musinsam.eventservice.global.config.EventResponseCode.EVENT_UPDATE_SUCCESS;

import com.musinsam.common.aop.CustomPreAuthorize;
import com.musinsam.common.resolver.CurrentUser;
import com.musinsam.common.response.ApiResponse;
import com.musinsam.common.user.CurrentUserDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPostDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPutByEventProductIdDtoApiV1;
import com.musinsam.eventservice.application.dto.request.ReqEventPutDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetByEventIdDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetDtoApiV1;
import com.musinsam.eventservice.application.dto.response.ResEventGetProductByEventIdDtoApiV1;
import com.musinsam.eventservice.application.service.EventServiceApiV1;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/events")
@RequiredArgsConstructor
public class EventControllerApiV1 {

  private final EventServiceApiV1 eventService;


  /**
   * 이벤트 등록
   */
  @PostMapping
  @CustomPreAuthorize(userRoleType = {ROLE_MASTER})
  public ResponseEntity<ApiResponse<Void>> createEvent(
      @Valid @RequestBody ReqEventPostDtoApiV1 dto,
      @CurrentUser CurrentUserDtoApiV1 currentUser
  ) {
    eventService.createEvent(dto, currentUser);

    return ResponseEntity.ok(new ApiResponse<>(
        EVENT_CREATE_SUCCESS.getCode(),
        EVENT_CREATE_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 이벤트 목록 조회
   */
  @GetMapping
  @CustomPreAuthorize(userRoleType = {
      ROLE_USER,
      ROLE_COMPANY,
      ROLE_MASTER}
  )
  public ResponseEntity<ApiResponse<ResEventGetDtoApiV1>> getEventList(
      @CurrentUser CurrentUserDtoApiV1 currentUser,
      @RequestParam(required = false) Boolean active,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        EVENT_GET_LIST_SUCCESS.getCode(),
        EVENT_GET_LIST_SUCCESS.getMessage(),
        eventService.getEventList(currentUser, active, page, size)
    ));
  }

  /**
   * 이벤트 수정
   */
  @PutMapping("/{eventId}")
  @CustomPreAuthorize(userRoleType = {ROLE_MASTER})
  public ResponseEntity<ApiResponse<Void>> updateEvent(
      @PathVariable UUID eventId,
      @CurrentUser CurrentUserDtoApiV1 currentUser,
      @Valid @RequestBody ReqEventPutDtoApiV1 dto
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        EVENT_UPDATE_SUCCESS.getCode(),
        EVENT_UPDATE_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 이벤트 삭제
   */
  @DeleteMapping("/{eventId}")
  @CustomPreAuthorize(userRoleType = {ROLE_MASTER})
  public ResponseEntity<ApiResponse<Void>> deleteEvent(
      @PathVariable UUID eventId,
      @CurrentUser CurrentUserDtoApiV1 currentUser
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        EVENT_DELETE_SUCCESS.getCode(),
        EVENT_DELETE_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 이벤트 상세 조회
   */
  @GetMapping("/{eventId}")
  @CustomPreAuthorize(userRoleType = {
      ROLE_USER,
      ROLE_COMPANY,
      ROLE_MASTER
  })
  public ResponseEntity<ApiResponse<ResEventGetByEventIdDtoApiV1>> getEvent(
      @PathVariable UUID eventId,
      @CurrentUser CurrentUserDtoApiV1 currentUser
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        EVENT_GET_SUCCESS.getCode(),
        EVENT_GET_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 이벤트 상품 추가
   */
  @PostMapping("/{eventId}")
  @CustomPreAuthorize(userRoleType = {
      ROLE_MASTER
  })
  public ResponseEntity<ApiResponse<Void>> addEventProduct(
      @PathVariable UUID eventId,
      @CurrentUser CurrentUserDtoApiV1 currentUser,
      @Valid @RequestBody ReqEventPostByEventIdDtoApiV1 dto
  ) {
    eventService.addEventProduct(eventId, dto);
    return ResponseEntity.ok(new ApiResponse<>(
        EVENT_ADD_PRODUCT_SUCCESS.getCode(),
        EVENT_ADD_PRODUCT_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 이벤트 상품 조회
   */
  @GetMapping("/{eventId}/products")
  @CustomPreAuthorize(userRoleType = {
      ROLE_USER,
      ROLE_COMPANY,
      ROLE_MASTER
  })
  public ResponseEntity<ApiResponse<ResEventGetProductByEventIdDtoApiV1>> getEventProductList(
      @PathVariable UUID eventId,
      @CurrentUser CurrentUserDtoApiV1 currentUser,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        EVENT_GET_PRODUCT_LIST_SUCCESS.getCode(),
        EVENT_GET_PRODUCT_LIST_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 이벤트 상품 수정
   */
  @PutMapping("/{eventId}/{eventProductId}")
  @CustomPreAuthorize(userRoleType = {ROLE_MASTER})
  public ResponseEntity<ApiResponse<Void>> updateEventProduct(
      @PathVariable UUID eventId,
      @PathVariable UUID eventProductId,
      @CurrentUser CurrentUserDtoApiV1 currentUser,
      @Valid @RequestBody ReqEventPutByEventProductIdDtoApiV1 dto
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        EVENT_UPDATE_PRODUCT_SUCCESS.getCode(),
        EVENT_UPDATE_PRODUCT_SUCCESS.getMessage(),
        null
    ));
  }

  /**
   * 이벤트 상품 삭제
   */
  @DeleteMapping("/{eventId}/{eventProductId}")
  @CustomPreAuthorize(userRoleType = {ROLE_MASTER})
  public ResponseEntity<ApiResponse<Void>> deleteEventProduct(
      @PathVariable UUID eventId,
      @PathVariable UUID eventProductId,
      @CurrentUser CurrentUserDtoApiV1 currentUser
  ) {
    return ResponseEntity.ok(new ApiResponse<>(
        EVENT_DELETE_PRODUCT_SUCCESS.getCode(),
        EVENT_DELETE_PRODUCT_SUCCESS.getMessage(),
        null
    ));
  }

}
