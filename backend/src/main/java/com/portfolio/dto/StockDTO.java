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
public class StockDTO {
    private String ticker;
    private String companyName;
    private BigDecimal currentPrice;
    private BigDecimal previousClose;
    private BigDecimal dayChange;
    private BigDecimal dayChangePercent;
    private BigDecimal dayHigh;
    private BigDecimal dayLow;
}
