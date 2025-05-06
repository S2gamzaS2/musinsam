package com.musinsam.eventservice.domain.event.vo;

import com.musinsam.common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum EventResponseCode implements SuccessCode {

  EVENT_CREATE_SUCCESS(0, "이벤트 생성 성공", HttpStatus.CREATED),
  EVENT_GET_LIST_SUCCESS(0, "이벤트 목록 조회 성공", HttpStatus.OK),
  EVENT_UPDATE_SUCCESS(0, "이벤트 수정 성공", HttpStatus.OK),
  EVENT_DELETE_SUCCESS(0, "이벤트 삭제 성공", HttpStatus.OK),
  EVENT_GET_SUCCESS(0, "이벤트 상세 조회 성공", HttpStatus.OK),
  EVENT_GET_PRODUCT_LIST_SUCCESS(0, "이벤트 상품 목록 조회 성공", HttpStatus.OK),
  EVENT_ADD_PRODUCT_SUCCESS(0, "이벤트 상품 추가 성공", HttpStatus.OK),
  EVENT_UPDATE_PRODUCT_SUCCESS(0, "이벤트 상품 수정 성공", HttpStatus.OK),
  EVENT_DELETE_PRODUCT_SUCCESS(0, "이벤트 상품 삭제 성공", HttpStatus.OK);

  private final Integer code;
  private final String message;
  private final HttpStatus httpStatus;
}
