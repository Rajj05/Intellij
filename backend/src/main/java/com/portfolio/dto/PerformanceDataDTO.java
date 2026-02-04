package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceDataDTO {
    private LocalDate date;
    private BigDecimal totalValue;
    private BigDecimal totalInvested;
    private BigDecimal cashBalance;
    private BigDecimal dailyGainLoss;
}
