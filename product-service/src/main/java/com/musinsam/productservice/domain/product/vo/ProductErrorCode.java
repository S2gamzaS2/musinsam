package com.musinsam.productservice.domain.product.vo;

import com.musinsam.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않습니다.", -1),
  UNAUTHORIZED_PRODUCT_ACCESS(HttpStatus.FORBIDDEN, "해당 상품에 대한 접근 권한이 없습니다.", -2),
  S3_FOLDER_NOT_SPECIFIED(HttpStatus.BAD_REQUEST, "S3 저장 경로가 지정되지 않았습니다.", -3),
  FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "파일이 비어있거나 존재하지 않습니다.", -4),
  INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다.", -5);

  private final HttpStatus httpStatus;
  private final String message;
  private final Integer code;
}
