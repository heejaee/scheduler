package com.example.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class SchedulerExecutorConfig {

    @Bean
    public Executor schedulerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4); // 기본 스레드 수
        executor.setMaxPoolSize(8);  // 최대 스레드 수
        executor.setQueueCapacity(100); // 큐가 처리할 수 있는 작업 수
        executor.setThreadNamePrefix("scheduler-");

        executor.initialize();
        return executor;
    }
}
