package com.musinsam.eventservice.domain.event.vo;

import com.musinsam.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EventErrorCode implements ErrorCode {

  EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이벤트가 존재하지 않습니다.", -1),
  EVENT_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이벤트 상품이 존재하지 않습니다.", -2),
  PRODUCT_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록된 상품입니다.", -3),
  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않아 등록할 수 없습니다.", -4),
  EVENT_SCHEDULING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이벤트 스케줄링이 실패하였습니다.", -5);

  private final HttpStatus httpStatus;
  private final String message;
  private final Integer code;
}
