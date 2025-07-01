package com.example.scheduler;

import com.example.scheduler.alarm.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialProductScheduler {

    private final RestTemplate restTemplate;
    private final AlarmService alarmService;

    // 매일 자정 스케줄러 실행
//    @Scheduled(cron = "00 55 16 * * ?")
    @Scheduled(cron = "*/1 * * * * *") // 1초마다 실행
    public void processSpecialProducts() {
        try {
            log.info("스케줄러 정상 실행됨");
            restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/deleteExpired", null, Void.class);
            restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/approveStarting", null, Void.class);


            alarmService.updateExpiringStatusInCache();
            restTemplate.getForObject(
                    "http://backend:8080/api/v1/specialProduct/active",
                    null,
                    Void.class
            );
        } catch (Exception e) {
            log.error("스케줄러 실행 중 오류 발생", e);
        }
    }

//    @Scheduled(cron = "00 55 16 * * ?")
//    @Scheduled(cron = "*/1 * * * * *") // 1초마다 실행
//    public void updateExpiringStatusInCache() {
//        log.info("스케줄러 변경이 정상 실행됩니다");
//
//        alarmService.updateExpiringStatusInCache();
//        restTemplate.postForObject(
//                "http://backend:8080/api/v1/specialProduct/active",
//                null,
//                Void.class
//        );
//    }
}
