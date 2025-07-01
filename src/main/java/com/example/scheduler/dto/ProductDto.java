package com.example.scheduler.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long productId;
    private String productName;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private int runningTime;
    private int price;
    private String casting;
    private String notice;
    private Long salesCount;
}

