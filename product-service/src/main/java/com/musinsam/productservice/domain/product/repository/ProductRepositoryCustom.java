package com.musinsam.productservice.domain.product.repository;

import com.musinsam.productservice.domain.product.entity.ProductEntity;
import com.musinsam.productservice.domain.product.vo.ProductStatus;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

  Page<ProductEntity> findProductWithConditions(
      BigDecimal minPrice,
      BigDecimal maxPrice,
      ProductStatus status,
      String sortBy,
      Pageable pageable);

}
