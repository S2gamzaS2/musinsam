package com.musinsam.eventservice.domain.event.entity;

import com.musinsam.common.domain.BaseEntity;
import com.musinsam.eventservice.domain.event.vo.EventStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_event")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventEntity extends BaseEntity {

  @Id
  @UuidGenerator
  private UUID id;

  @Setter
  @Column(nullable = false)
  private String name;

  @Setter
  @Column(nullable = false)
  private ZonedDateTime startTime;

  @Setter
  @Column(nullable = false)
  private ZonedDateTime endTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EventStatus status = EventStatus.SCHEDULED;


}
