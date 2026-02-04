package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummaryDTO {
    private Long userId;
    private String username;
    private BigDecimal walletBalance;
    private BigDecimal totalInvested;
    private BigDecimal totalCurrentValue;
    private BigDecimal totalBalance;      // wallet + portfolio value
    private BigDecimal totalGainLoss;
    private BigDecimal totalGainLossPercent;
    private BigDecimal dailyGainLoss;
    private BigDecimal dailyGainLossPercent;
    private int totalAssets;
    private List<HoldingDTO> holdings;
}
