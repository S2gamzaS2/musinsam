package com.musinsam.productservice.domain.product.vo;

import com.musinsam.common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ProductResponseCode implements SuccessCode {

  PRODUCT_CREATE_SUCCESS(0, "상품 등록 성공", HttpStatus.CREATED),
  PRODUCT_GET_SUCCESS(0, "단일 상품 조회 성공", HttpStatus.OK),
  PRODUCT_GET_LIST_SUCCESS(0, "상품 목록 조회 성공", HttpStatus.OK),
  PRODUCT_UPDATE_SUCCESS(0, "상품 수정 성공", HttpStatus.OK),
  PRODUCT_DELETE_SUCCESS(0, "상품 삭제 성공", HttpStatus.OK),
  PRODUCT_GET_STOCK_SUCCESS(0, "재고 조회 성공", HttpStatus.OK),
  PRODUCT_UPDATE_STOCK_SUCCESS(0, "재고 수정 성공", HttpStatus.OK),
  PRODUCT_APPLY_COUPON_SUCCESS(0, "쿠폰 적용 성공", HttpStatus.OK),
  PRODUCT_GET_COUPON_SUCCESS(0, "쿠폰 목록 조회 성공", HttpStatus.OK),
  PRODUCT_DELETE_COUPON_SUCCESS(0, "쿠폰 적용 삭제 성공", HttpStatus.OK);

  private final Integer code;
  private final String message;
  private final HttpStatus httpStatus;
}
