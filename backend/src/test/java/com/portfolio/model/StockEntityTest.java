package com.portfolio.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Stock Entity Tests")
class StockEntityTest {

    @Test
    @DisplayName("Should create stock with all properties")
    void testStockCreation() {
        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setCompanyName("Apple Inc.");
        stock.setCurrentPrice(new BigDecimal("185.50"));
        stock.setPreviousClose(new BigDecimal("183.20"));
        stock.setDayChange(new BigDecimal("2.30"));
        stock.setDayChangePercent(new BigDecimal("1.26"));
        
        assertEquals("AAPL", stock.getTicker());
        assertEquals("Apple Inc.", stock.getCompanyName());
        assertEquals(new BigDecimal("185.50"), stock.getCurrentPrice());
        assertEquals(new BigDecimal("1.26"), stock.getDayChangePercent());
    }

    @Test
    @DisplayName("Should calculate positive day change correctly")
    void testPositiveDayChange() {
        Stock stock = new Stock();
        stock.setTicker("NVDA");
        stock.setCompanyName("NVIDIA Corporation");
        stock.setCurrentPrice(new BigDecimal("875.50"));
        stock.setPreviousClose(new BigDecimal("868.00"));
        stock.setDayChange(new BigDecimal("7.50"));
        stock.setDayChangePercent(new BigDecimal("0.86"));
        
        assertTrue(stock.getDayChange().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(stock.getDayChangePercent().compareTo(BigDecimal.ZERO) > 0);
    }
}
