package com.example.scheduler;

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

    @Scheduled(cron = "0 */1 * * * ?")
    public void processSpecialProducts() {
        log.info("🟢 스케줄러 메서드가 실행되었습니다!");

        try {
            restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/deleteExpired", null, Void.class);
            restTemplate.postForObject("http://backend:8080/api/v1/specialProduct/approveStarting", null, Void.class);
            log.info("스케줄러 정상 실행됨");
        } catch (Exception e) {
            log.error("스케줄러 실행 중 오류 발생", e);
        }
    }
}
