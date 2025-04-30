package com.musinsam.eventservice.presentation.controller;

import com.musinsam.eventservice.application.service.EventServiceApiV1;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/v1/events")
@RequiredArgsConstructor
public class EventInternalControllerApiV1 {

  private final EventServiceApiV1 eventService;

  /**
   * 이벤트 종료 시간
   */
  @GetMapping
  public ResponseEntity<LocalDateTime> getEndTime(@RequestParam UUID productId) {
    return ResponseEntity.ok().body(eventService.getEndTime(productId));
  }

  /**
   * 할인률
   */
  @GetMapping("/discount-rate")
  public ResponseEntity<Integer> getDiscountRate(@RequestParam UUID productId) {
    return ResponseEntity.ok().body(eventService.getDiscountRate(productId));
  }
}
