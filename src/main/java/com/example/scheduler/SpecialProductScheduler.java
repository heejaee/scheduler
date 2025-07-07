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

    // 매일 자정 스케줄러 실행
//    @Scheduled(cron = "00 55 16 * * ?")
//    @Scheduled(cron = "*/1 * * * * *") // 1초마다 실행
//    public void processSpecialProducts() {
//        try {
//            log.info("스케줄러 정상 실행됨");
//            // 할인 종료 날짜가 오늘 이전인 상품을 DB와 redis에서 삭제
//            restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/deleteExpired", null, Void.class);
//            // 조건에 만족하는 상품을 DB와 redis에 생성
//            restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/approveStarting", null, Void.class);
//
//            //마감임박 상품 변경시작
//            alarmService.updateExpiringStatusInCache();
//            restTemplate.getForObject("http://backend:8080/api/v1/specialProduct/active",Void.class);
//        } catch (Exception e) {
//            log.error("스케줄러 실행 중 오류 발생", e);
//        }
//    }


    @Scheduled(cron = "*/1 * * * * *")
    public void processSpecialProducts2() {
        log.info("스케줄러 실행 시작");

        CompletableFuture<Void> deleteFuture = CompletableFuture.runAsync(() -> {
            try {
                restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/deleteExpired", null, Void.class);
                log.info("만료 특가상품 삭제 완료");
            } catch (Exception e) {
                log.error("만료 특가상품 삭제 실패", e);
            }
        });

        CompletableFuture<Void> approveFuture = CompletableFuture.runAsync(() -> {
            try {
                restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/approveStarting", null, Void.class);
                log.info("시작 예정 특가상품 승인 완료");
            } catch (Exception e) {
                log.error("시작 예정 특가상품 승인 실패", e);
            }
        });

        CompletableFuture<Void> alarmFuture = CompletableFuture.runAsync(() -> {
            try {
                alarmService.updateExpiringStatusInCache();
                restTemplate.getForObject("http://backend:8080/api/v1/specialProduct/active", Void.class);
                log.info("마감임박 상태 업데이트 및 활성 특가상품 조회 완료");
            } catch (Exception e) {
                log.error("마감임박 상태 처리 실패", e);
            }
        });

        // 모든 작업 완료까지 대기
        CompletableFuture.allOf(deleteFuture, approveFuture, alarmFuture).join();
        log.info("모든 스케줄 작업 완료");
    }

}
