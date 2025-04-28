package com.musinsam.eventservice.infrastructure.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResProductInfoGetByProductId {

  private Product product;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Product {

    private String name;

  }

}
