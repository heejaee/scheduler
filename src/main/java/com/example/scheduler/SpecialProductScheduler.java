package com.example.scheduler;

import com.example.scheduler.alarm.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialProductScheduler {

    private final WebClient webClient;
    private final AlarmService alarmService;

    @Scheduled(cron = "0 0 15 * * ?")
    public void processSpecialProducts() {
        log.info("스케줄러 실행 시작");

        Mono<Void> deleteMono = webClient.post()
                .uri("/api/v1/specialProduct/deleteExpired")
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(response -> log.info("만료 특가상품 삭제 완료"))
                .doOnError(error -> log.error("만료 특가상품 삭제 실패", error))
                .then();

        Mono<Void> approveMono = webClient.post()
                .uri("/api/v1/specialProduct/approveStarting")
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(response -> log.info("시작 예정 특가상품 승인 완료"))
                .doOnError(error -> log.error("시작 예정 특가상품 승인 실패", error))
                .then();

        // 병렬 비동기 처리 후 후속 처리까지 연결
        Mono.when(deleteMono, approveMono)
                .then(Mono.fromRunnable(alarmService::updateExpiringStatusInCache))
                .then(webClient.get()
                        .uri("/api/v1/specialProduct/active")
                        .retrieve()
                        .bodyToMono(Void.class))
                .doOnSuccess(response -> log.info("모든 스케줄 작업 완료"))
                .doOnError(error -> log.error("마감 임박 특가상품 상태 변경 실패", error))
                .subscribe();
    }
}
