package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldingDTO {
    private Long id;
    private String ticker;
    private String companyName;
    private BigDecimal quantity;
    private BigDecimal averageCost;
    private BigDecimal totalInvested;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private BigDecimal profitLossPercent;
    private BigDecimal dayChange;
    private BigDecimal dayChangePercent;
}
