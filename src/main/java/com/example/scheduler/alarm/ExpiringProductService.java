package com.example.scheduler.alarm;

import com.example.scheduler.dto.SpecialProductDto;
import com.example.scheduler.lock.DistributedLock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiringProductService {

    private final RedisTemplate<String, SpecialProductDto> redisTemplate;

    // 각 상품별로 개별 락 적용
    @DistributedLock(key = "'specialProduct:' + #dto.specialProductId")
    public void markAsExpiring(SpecialProductDto dto, LocalDate today,String key) {
        if (ChronoUnit.DAYS.between(today, dto.getDiscountEndDate()) <= 5) {
            dto.setExpiring(true);
            redisTemplate.opsForValue().set(key, dto);
            log.info("Expiring 상태 반영 완료: {}", key);
        }
    }
}
