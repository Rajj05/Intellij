package com.portfolio.service;

import com.portfolio.model.Stock;
import com.portfolio.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stock Price Service Tests")
class StockPriceServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private AlertNotificationService alertNotificationService;

    private StockPriceService stockPriceService;
    private Stock testStock;

    @BeforeEach
    void setUp() {
        stockPriceService = new StockPriceService(stockRepository, alertNotificationService);
        
        testStock = new Stock();
        testStock.setTicker("AAPL");
        testStock.setCompanyName("Apple Inc.");
        testStock.setCurrentPrice(new BigDecimal("185.50"));
        testStock.setPreviousClose(new BigDecimal("183.20"));
        testStock.setDayHigh(new BigDecimal("186.50"));
        testStock.setDayLow(new BigDecimal("183.00"));
        testStock.setLastUpdated(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should refresh stock price successfully")
    void testRefreshStockPrice() {
        when(stockRepository.findById("AAPL")).thenReturn(Optional.of(testStock));

        Stock refreshed = stockPriceService.refreshStockPrice("AAPL");

        assertNotNull(refreshed);
        assertEquals("AAPL", refreshed.getTicker());
    }

}
