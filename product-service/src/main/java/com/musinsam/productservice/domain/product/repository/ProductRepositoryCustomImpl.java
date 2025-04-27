package com.musinsam.productservice.domain.product.repository;

import static com.musinsam.productservice.domain.product.entity.QProductEntity.productEntity;

import com.musinsam.productservice.domain.product.entity.ProductEntity;
import com.musinsam.productservice.domain.product.vo.ProductStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;


  @Override
  public Page<ProductEntity> findProductWithConditions(
      BigDecimal minPrice,
      BigDecimal maxPrice,
      ProductStatus status,
      String sortBy,
      Pageable pageable
  ) {

    JPAQuery<ProductEntity> query = jpaQueryFactory
        .selectFrom(productEntity)
        .where(
            isNotDeleted(),
            priceGoe(minPrice),
            priceLoe(maxPrice),
            filterByStatus(status)
        );

    applySorting(query, sortBy);

    long total = query.fetchCount();

    // 페이징
    List<ProductEntity> result = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return new PageImpl<>(result, pageable, total);
  }

  private BooleanExpression isNotDeleted() {
    return productEntity.deletedAt.isNull();
  }

  private BooleanExpression priceGoe(BigDecimal minPrice) {
    if (minPrice != null) {
      return productEntity.price.goe(minPrice);
    }
    return null;
  }

  private BooleanExpression priceLoe(BigDecimal maxPrice) {
    if (maxPrice != null) {
      return productEntity.price.loe(maxPrice);
    }
    return null;
  }

  private BooleanExpression filterByStatus(ProductStatus status) {
    if (status != null) {
      return productEntity.status.in(ProductStatus.SHOW, ProductStatus.SOLDOUT);
    }

    // HIDE는 선택지에 없을건데, HIDE일 경우는 예외처리?

    return productEntity.status.in(ProductStatus.SHOW);
  }

  private void applySorting(JPAQuery<ProductEntity> query, String sortBy) {
    if (sortBy == null) {
      query.orderBy(productEntity.createdAt.desc());
    } else if ("price_desc".equals(sortBy)) {
      query.orderBy(productEntity.price.desc());
    } else if ("price_asc".equals(sortBy)) {
      query.orderBy(productEntity.price.asc());
    }
  }

}
