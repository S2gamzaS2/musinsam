package com.musinsam.eventservice.application.scheduler;

import com.musinsam.eventservice.application.service.EventServiceApiV1;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@RequiredArgsConstructor
public class EventEndJob implements Job {

  private final EventServiceApiV1 eventService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // Job 실행에 필요한 데이터 전달
    JobDataMap dataMap = context.getMergedJobDataMap();

    String eventIdStr = dataMap.getString("eventId");
    UUID eventId = UUID.fromString(eventIdStr);

    try {
      eventService.endEvent(eventId);
    } catch (Exception e) {
      throw new JobExecutionException("이벤트 종료 중 오류 발생: " + eventId, e);
    }
  }
}
