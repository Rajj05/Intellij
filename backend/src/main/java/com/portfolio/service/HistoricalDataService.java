package com.portfolio.service;

import com.portfolio.dto.HistoricalDataDTO;
import com.portfolio.dto.HistoricalDataDTO.CandleData;
import com.portfolio.model.Stock;
import com.portfolio.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoricalDataService {

    private final WebClient webClient;
    private final StockRepository stockRepository;

    @Value("${stock.api.key:}")
    private String apiKey;

    @Value("${stock.api.enabled:false}")
    private boolean apiEnabled;

    //  Get historical data for a stock
    //  @param ticker Stock ticker symbol
    //  @param period Time period: 1D, 1W, 1M, 6M, 1Y, 5Y

    public HistoricalDataDTO getHistoricalData(String ticker, String period) {
        Stock stock = stockRepository.findById(ticker).orElse(null);
        
        if (!apiEnabled || apiKey.isEmpty()) {
            log.warn("API not enabled, returning simulated historical data");
            return generateSimulatedData(ticker, period, stock);
        }

        try {
            // Calculate time range
            long[] timeRange = calculateTimeRange(period);
            long fromTimestamp = timeRange[0];
            long toTimestamp = timeRange[1];
            String resolution = getResolution(period);

            // Fetch from Finnhub
            String url = String.format(
                "https://finnhub.io/api/v1/stock/candle?symbol=%s&resolution=%s&from=%d&to=%d&token=%s",
                ticker, resolution, fromTimestamp, toTimestamp, apiKey
            );

            Map<String, Object> response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

            if (response != null && "ok".equals(response.get("s"))) {
                return parseHistoricalResponse(ticker, period, response, stock);
            } else {
                log.warn("No data from Finnhub for {}, using simulated data", ticker);
                return generateSimulatedData(ticker, period, stock);
            }
        } catch (Exception e) {
            log.error("Error fetching historical data for {}: {}", ticker, e.getMessage());
            return generateSimulatedData(ticker, period, stock);
        }
    }

    private long[] calculateTimeRange(String period) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime from;

        switch (period.toUpperCase()) {
            case "1D":
                from = now.minusDays(1);
                break;
            case "1W":
                from = now.minusWeeks(1);
                break;
            case "1M":
                from = now.minusMonths(1);
                break;
            case "6M":
                from = now.minusMonths(6);
                break;
            case "1Y":
                from = now.minusYears(1);
                break;
            case "5Y":
                from = now.minusYears(5);
                break;
            default:
                from = now.minusMonths(1);
        }

        return new long[] { from.toEpochSecond(), now.toEpochSecond() };
    }

    private String getResolution(String period) {
        switch (period.toUpperCase()) {
            case "1D":
                return "5";  // 5 minute intervals
            case "1W":
                return "60"; // 1 hour intervals
            case "1M":
                return "D";  // Daily
            case "6M":
                return "D";  // Daily
            case "1Y":
                return "D";  // Daily
            case "5Y":
                return "W";  // Weekly
            default:
                return "D";
        }
    }

    private HistoricalDataDTO parseHistoricalResponse(String ticker, String period, 
            Map<String, Object> response, Stock stock) {
        
        List<CandleData> candles = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        List<Number> closes = (List<Number>) response.get("c");
        List<Number> highs = (List<Number>) response.get("h");
        List<Number> lows = (List<Number>) response.get("l");
        List<Number> opens = (List<Number>) response.get("o");
        List<Number> volumes = (List<Number>) response.get("v");
        List<Number> timestamps = (List<Number>) response.get("t");

        if (closes != null && !closes.isEmpty()) {
            for (int i = 0; i < closes.size(); i++) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(timestamps.get(i).longValue()),
                    ZoneId.of("America/New_York")
                );

                candles.add(CandleData.builder()
                    .timestamp(timestamps.get(i).longValue())
                    .date(dateTime.format(formatter))
                    .open(opens.get(i).doubleValue())
                    .high(highs.get(i).doubleValue())
                    .low(lows.get(i).doubleValue())
                    .close(closes.get(i).doubleValue())
                    .volume(volumes.get(i).longValue())
                    .build());
            }
        }

        // Calculate period statistics
        Double periodStartPrice = candles.isEmpty() ? 0 : candles.get(0).getClose();
        Double currentPrice = stock != null ? stock.getCurrentPrice().doubleValue() : 
            (candles.isEmpty() ? 0 : candles.get(candles.size() - 1).getClose());
        Double periodChange = currentPrice - periodStartPrice;
        Double periodChangePercent = periodStartPrice > 0 ? 
            (periodChange / periodStartPrice) * 100 : 0;
        
        Double periodHigh = candles.stream()
            .mapToDouble(CandleData::getHigh)
            .max().orElse(0);
        Double periodLow = candles.stream()
            .mapToDouble(CandleData::getLow)
            .min().orElse(0);

        return HistoricalDataDTO.builder()
            .ticker(ticker)
            .companyName(stock != null ? stock.getCompanyName() : ticker)
            .period(period)
            .candles(candles)
            .currentPrice(currentPrice)
            .periodStartPrice(periodStartPrice)
            .periodChange(periodChange)
            .periodChangePercent(periodChangePercent)
            .periodHigh(periodHigh)
            .periodLow(periodLow)
            .build();
    }

// Generate simulated historical data when API is not available

    private HistoricalDataDTO generateSimulatedData(String ticker, String period, Stock stock) {
        List<CandleData> candles = new ArrayList<>();
        Random random = new Random(ticker.hashCode());
        
        double basePrice = stock != null ? stock.getCurrentPrice().doubleValue() : 150.0;
        int dataPoints = getDataPointsForPeriod(period);
        
        // Start from a price that allows for realistic growth/decline to current price
        double volatility = getVolatilityForPeriod(period);
        double startPrice = basePrice * (1 - (random.nextDouble() - 0.3) * volatility);
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        double price = startPrice;
        double periodHigh = price;
        double periodLow = price;
        
        for (int i = dataPoints; i >= 0; i--) {
            LocalDateTime dateTime = getDateTimeForPeriod(now, period, i);
            
            // Random price movement
            double change = (random.nextDouble() - 0.48) * basePrice * 0.03;
            price = Math.max(price + change, basePrice * 0.5);
            
            double dayVolatility = random.nextDouble() * 0.02 * price;
            double open = price - dayVolatility + random.nextDouble() * dayVolatility * 2;
            double close = price;
            double high = Math.max(open, close) + random.nextDouble() * dayVolatility;
            double low = Math.min(open, close) - random.nextDouble() * dayVolatility;
            
            periodHigh = Math.max(periodHigh, high);
            periodLow = Math.min(periodLow, low);
            
            candles.add(CandleData.builder()
                .timestamp(dateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .date(dateTime.format(formatter))
                .open(Math.round(open * 100.0) / 100.0)
                .high(Math.round(high * 100.0) / 100.0)
                .low(Math.round(low * 100.0) / 100.0)
                .close(Math.round(close * 100.0) / 100.0)
                .volume((long) (1000000 + random.nextDouble() * 5000000))
                .build());
        }
        
        // Adjust last price to match current stock price
        if (!candles.isEmpty() && stock != null) {
            CandleData last = candles.get(candles.size() - 1);
            last.setClose(stock.getCurrentPrice().doubleValue());
            last.setHigh(Math.max(last.getHigh(), last.getClose()));
            last.setLow(Math.min(last.getLow(), last.getClose()));
        }
        
        Double periodStartPrice = candles.isEmpty() ? basePrice : candles.get(0).getClose();
        Double currentPrice = stock != null ? stock.getCurrentPrice().doubleValue() : basePrice;
        Double periodChange = currentPrice - periodStartPrice;
        Double periodChangePercent = periodStartPrice > 0 ? (periodChange / periodStartPrice) * 100 : 0;

        return HistoricalDataDTO.builder()
            .ticker(ticker)
            .companyName(stock != null ? stock.getCompanyName() : ticker)
            .period(period)
            .candles(candles)
            .currentPrice(currentPrice)
            .periodStartPrice(Math.round(periodStartPrice * 100.0) / 100.0)
            .periodChange(Math.round(periodChange * 100.0) / 100.0)
            .periodChangePercent(Math.round(periodChangePercent * 100.0) / 100.0)
            .periodHigh(Math.round(periodHigh * 100.0) / 100.0)
            .periodLow(Math.round(periodLow * 100.0) / 100.0)
            .build();
    }

    private int getDataPointsForPeriod(String period) {
        switch (period.toUpperCase()) {
            case "1D": return 78;   // 5-min intervals for trading day
            case "1W": return 7;    // Daily for a week
            case "1M": return 22;   // Trading days in a month
            case "6M": return 130;  // ~6 months of trading days
            case "1Y": return 252;  // Trading days in a year
            case "5Y": return 260;  // Weekly data for 5 years
            default: return 30;
        }
    }

    private double getVolatilityForPeriod(String period) {
        switch (period.toUpperCase()) {
            case "1D": return 0.05;
            case "1W": return 0.10;
            case "1M": return 0.15;
            case "6M": return 0.30;
            case "1Y": return 0.50;
            case "5Y": return 1.00;
            default: return 0.20;
        }
    }

    private LocalDateTime getDateTimeForPeriod(LocalDateTime now, String period, int index) {
        switch (period.toUpperCase()) {
            case "1D":
                return now.minusMinutes(index * 5);
            case "1W":
                return now.minusDays(index);
            case "1M":
                return now.minusDays(index);
            case "6M":
                return now.minusDays(index);
            case "1Y":
                return now.minusDays(index);
            case "5Y":
                return now.minusWeeks(index);
            default:
                return now.minusDays(index);
        }
    }
}
