package com.musinsam.productservice.infrastructure.dto.res;

import com.musinsam.productservice.domain.product.entity.ProductEntity;
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

  public static ResProductInfoGetByProductId of(ProductEntity productEntity) {
    return ResProductInfoGetByProductId.builder()
        .product(Product.from(productEntity))
        .build();
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Product {

    private String name;

    public static Product from(ProductEntity productEntity) {
      return Product.builder()
          .name(productEntity.getName())
          .build();
    }
  }

}
