package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalDataDTO {
    private String ticker;
    private String companyName;
    private String period;
    private List<CandleData> candles;
    private Double currentPrice;
    private Double periodStartPrice;
    private Double periodChange;
    private Double periodChangePercent;
    private Double periodHigh;
    private Double periodLow;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandleData {
        private Long timestamp;
        private String date;
        private Double open;
        private Double high;
        private Double low;
        private Double close;
        private Long volume;
    }
}
