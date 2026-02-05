package com.portfolio.service;

import com.portfolio.model.Stock;
import com.portfolio.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StockPriceService {

    private static final Logger logger = LoggerFactory.getLogger(StockPriceService.class);

    private final StockRepository stockRepository;
    private final AlertNotificationService alertNotificationService;
    private final WebClient webClient;

    @Value("${stock.api.key:demo}")
    private String apiKey;

    @Value("${stock.api.enabled:false}")
    private boolean apiEnabled;

    public StockPriceService(StockRepository stockRepository, AlertNotificationService alertNotificationService) {
        this.stockRepository = stockRepository;
        this.alertNotificationService=alertNotificationService;
        this.webClient = WebClient.builder()
                .baseUrl("https://finnhub.io/api/v1")
                .build();
    }


    //  Update stock prices every 5 minutes (during market hours)
    //  Finnhub free tier allows 60 calls/minute

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void updateAllStockPrices() {
        if (!apiEnabled || "demo".equals(apiKey) || "YOUR_FINNHUB_API_KEY".equals(apiKey)) {
            logger.info("Stock API is disabled or not configured. Set stock.api.enabled=true and provide a valid API key.");
            return;
        }

        logger.info("Updating stock prices from Finnhub API...");
        List<Stock> stocks = stockRepository.findAll();
        
        for (Stock stock : stocks) {
            try {
                updateStockPrice(stock);
                // Rate limiting: wait 1 second between calls to stay within free tier
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.error("Error updating price for {}: {}", stock.getTicker(), e.getMessage());
            }
        }
        
        logger.info("Stock prices updated successfully!");
        alertNotificationService.checkAlertsAndCreateNotifications();
    }

//Update a single stock's price from Finnhub

    public void updateStockPrice(Stock stock) {
        try {
            Map<String, Object> quote = fetchQuote(stock.getTicker()).block();
            
            if (quote != null && quote.get("c") != null) {
                BigDecimal currentPrice = toBigDecimal(quote.get("c"));
                BigDecimal previousClose = toBigDecimal(quote.get("pc"));
                BigDecimal highPrice = toBigDecimal(quote.get("h"));
                BigDecimal lowPrice = toBigDecimal(quote.get("l"));
                
                if (currentPrice.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal dayChange = currentPrice.subtract(previousClose);
                    BigDecimal dayChangePercent = previousClose.compareTo(BigDecimal.ZERO) > 0 
                        ? dayChange.divide(previousClose, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                        : BigDecimal.ZERO;
                    
                    stock.setCurrentPrice(currentPrice);
                    stock.setPreviousClose(previousClose);
                    stock.setDayHigh(highPrice);
                    stock.setDayLow(lowPrice);
                    stock.setDayChange(dayChange);
                    stock.setDayChangePercent(dayChangePercent);
                    stock.setLastUpdated(LocalDateTime.now());
                    
                    stockRepository.save(stock);
                    logger.debug("Updated {}: ${} ({}%)", stock.getTicker(), currentPrice, dayChangePercent);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to update {}: {}", stock.getTicker(), e.getMessage());
        }
    }

//Fetch quote from Finnhub API

    private Mono<Map<String, Object>> fetchQuote(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorResume(e -> {
                    logger.error("API error for {}: {}", symbol, e.getMessage());
                    return Mono.empty();
                });
    }

// Manually trigger price update for a specific stock

    public Stock refreshStockPrice(String ticker) {
        Stock stock = stockRepository.findById(ticker).orElse(null);
        if (stock != null && apiEnabled) {
            updateStockPrice(stock);
            return stockRepository.findById(ticker).orElse(stock);
        }
        return stock;
    }

//Get real-time quote without saving (for display purposes)

    public Map<String, Object> getRealTimeQuote(String symbol) {
        if (!apiEnabled || "demo".equals(apiKey)) {
            return null;
        }
        return fetchQuote(symbol).block();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue()).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
