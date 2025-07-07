package com.example.scheduler.alarm;

import com.example.scheduler.dto.SpecialProductDto;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlarmService {

    private final RedisTemplate<String, SpecialProductDto> redisTemplate;

    public void updateExpiringStatusInCache() {
        log.info("마감임박 특가상품 확인 시작");
        Set<String> keys = redisTemplate.keys("specialProductCache::specialProduct:*");
        if (keys.isEmpty()) {
            log.warn("Redis에서 특가상품 키가 없음!");
            return;
        }

        LocalDate today = LocalDate.now();
        for (String key : keys) {
            SpecialProductDto dto = redisTemplate.opsForValue().get(key);
            if (ChronoUnit.DAYS.between(today, dto.getDiscountEndDate()) <= 5) {
                log.info("처리 중인 Redis 키: {}", key);

                dto.setExpiring(true);
                redisTemplate.opsForValue().set(key, dto);
            }
        }
    }
}
