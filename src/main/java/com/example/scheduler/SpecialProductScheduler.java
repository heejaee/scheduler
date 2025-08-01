package com.example.scheduler;

import com.example.scheduler.alarm.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.*;
@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialProductScheduler {

    private final RestTemplate restTemplate;
    private final AlarmService alarmService;
    private final Executor schedulerExecutor;

    // 매일 자정 스케줄러 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void processSpecialProducts2() {
        log.info("스케줄러 실행 시작");

        CompletableFuture<Void> deleteFuture = CompletableFuture.runAsync(() -> {
            try {
                restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/deleteExpired", null, Void.class);
                log.info("만료 특가상품 삭제 완료");
            } catch (Exception e) {
                log.error("만료 특가상품 삭제 실패", e);
            }
        }, schedulerExecutor);

        CompletableFuture<Void> approveFuture = CompletableFuture.runAsync(() -> {
            try {
                restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/approveStarting", null, Void.class);
                log.info("시작 예정 특가상품 승인 완료");
            } catch (Exception e) {
                log.error("시작 예정 특가상품 승인 실패", e);
            }
        }, schedulerExecutor);

        // 모든 작업 완료까지 대기
        CompletableFuture.allOf(deleteFuture, approveFuture).join();

        //마감임박 상품 변경시작
        alarmService.updateExpiringStatusInCache();
        restTemplate.getForObject("http://backend:8080/api/v1/specialProduct/active", Void.class);

        log.info("모든 스케줄 작업 완료");
    }
}
