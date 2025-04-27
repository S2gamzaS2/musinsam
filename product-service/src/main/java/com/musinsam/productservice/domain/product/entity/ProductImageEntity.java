package com.musinsam.productservice.domain.product.entity;

import com.musinsam.common.domain.BaseEntity;
import com.musinsam.productservice.infrastructure.s3.S3Folder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_product_image")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImageEntity extends BaseEntity {

  @Id
  @UuidGenerator
  private UUID id;

  @Column(nullable = false)
  private String imageUrl;

  @Column(nullable = false, updatable = false)
  private String originFileName;

  @Column(nullable = false, updatable = false)
  private long fileSize;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private S3Folder s3Folder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private ProductEntity product;


}
