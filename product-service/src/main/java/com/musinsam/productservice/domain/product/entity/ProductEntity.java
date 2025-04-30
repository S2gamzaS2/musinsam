package com.musinsam.productservice.domain.product.entity;

import com.musinsam.common.domain.BaseEntity;
import com.musinsam.productservice.domain.product.vo.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_product")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseEntity {

  @Id
  @UuidGenerator
  private UUID id;

  @Column(nullable = false)
  private UUID shopId;

  @Setter
  @Column(nullable = false)
  private String name;

  @Setter
  @Column(nullable = false)
  private BigDecimal price;

  @Setter
  @Column
  private BigDecimal discountPrice;

  @Setter
  @Column(nullable = false)
  private Integer stock;

  @Setter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProductStatus status;

}
