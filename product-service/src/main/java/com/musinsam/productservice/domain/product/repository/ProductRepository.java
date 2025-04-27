package com.musinsam.productservice.domain.product.repository;

import com.musinsam.productservice.domain.product.entity.ProductEntity;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

  Optional<ProductEntity> findByIdAndDeletedAtIsNull(UUID productId);

  ProductEntity save(ProductEntity product);

}
