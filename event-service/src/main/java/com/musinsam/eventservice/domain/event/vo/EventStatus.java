package com.musinsam.eventservice.domain.event.vo;

import java.util.stream.Stream;

public enum EventStatus {

  SCHEDULED,
  ACTIVE,
  COMPLETED;

  public static EventStatus of(String status) {
    return valueOf(status);
  }

  public static EventStatus parsing(String inputValue) {
    return Stream.of(EventStatus.values())
        .filter(eventStatus -> eventStatus.toString().equals(inputValue.toUpperCase()))
        .findFirst()
        .orElse(null);
  }
}
