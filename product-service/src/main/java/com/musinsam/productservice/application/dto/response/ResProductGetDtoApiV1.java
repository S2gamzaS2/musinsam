package com.musinsam.productservice.application.dto.response;

import com.musinsam.productservice.domain.product.entity.ProductEntity;
import com.musinsam.productservice.domain.product.vo.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResProductGetDtoApiV1 {

  private ProductPage productPage;

  public static ResProductGetDtoApiV1 of(Page<ProductEntity> productEntityPage) {
    return ResProductGetDtoApiV1.builder()
        .productPage(new ProductPage(productEntityPage))
        .build();
  }

  @Getter
  @ToString
  public static class ProductPage extends PagedModel<ProductPage.Product> {

    public ProductPage(Page<ProductEntity> productEntityPage) {
      super(
          new PageImpl<>(
              Product.from(productEntityPage.getContent()),
              productEntityPage.getPageable(),
              productEntityPage.getTotalElements()
          )
      );
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {

      private String name;
      private BigDecimal price;
      private BigDecimal discountPrice;
      private ProductStatus status;

      public static Product from(ProductEntity productEntity) {
        return Product.builder()
            .name(productEntity.getName())
            .price(productEntity.getPrice())
            .discountPrice(productEntity.getDiscountPrice())
            .status(productEntity.getStatus())
            .build();
      }

      public static List<Product> from(List<ProductEntity> productEntityList) {
        return productEntityList.stream()
            .map(productEntity -> Product.from(productEntity))
            .toList();
      }

    }

  }


}
