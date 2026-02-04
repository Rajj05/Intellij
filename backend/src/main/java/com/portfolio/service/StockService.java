package com.portfolio.service;

import com.portfolio.dto.StockDTO;
import com.portfolio.model.Stock;
import com.portfolio.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;

//Get all available stocks

    @Transactional(readOnly = true)
    public List<StockDTO> getAllStocks() {
        return stockRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

//Get stock by ticker

    @Transactional(readOnly = true)
    public StockDTO getStockByTicker(String ticker) {
        Stock stock = stockRepository.findById(ticker.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + ticker));
        return convertToDTO(stock);
    }

//Get top gainers (best performing stocks today)

    @Transactional(readOnly = true)
    public List<StockDTO> getTopGainers(int limit) {
        return stockRepository.findTopGainers().stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

// Get top losers (worst performing stocks today)

    @Transactional(readOnly = true)
    public List<StockDTO> getTopLosers(int limit) {
        return stockRepository.findTopLosers().stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

// Search stocks by ticker or company name

    @Transactional(readOnly = true)
    public List<StockDTO> searchStocks(String query) {
        return stockRepository.searchStocks(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


// Update stock price (called by scheduled task or external API)

    @Transactional
    public void updateStockPrice(String ticker, java.math.BigDecimal newPrice) {
        stockRepository.findById(ticker).ifPresent(stock -> {
            java.math.BigDecimal previousPrice = stock.getCurrentPrice();
            stock.setCurrentPrice(newPrice);
            stock.setPreviousClose(previousPrice);
            stock.setDayChange(newPrice.subtract(previousPrice));
            
            if (previousPrice.compareTo(java.math.BigDecimal.ZERO) != 0) {
                java.math.BigDecimal changePercent = stock.getDayChange()
                        .divide(previousPrice, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(new java.math.BigDecimal("100"));
                stock.setDayChangePercent(changePercent);
            }
            
            stockRepository.save(stock);
            log.info("Updated {} price to ${}", ticker, newPrice);
        });
    }

    private StockDTO convertToDTO(Stock stock) {
        return StockDTO.builder()
                .ticker(stock.getTicker())
                .companyName(stock.getCompanyName())
                .currentPrice(stock.getCurrentPrice())
                .previousClose(stock.getPreviousClose())
                .dayChange(stock.getDayChange())
                .dayChangePercent(stock.getDayChangePercent())
                .dayHigh(stock.getDayHigh())
                .dayLow(stock.getDayLow())
                .build();
    }
}
