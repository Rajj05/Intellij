package com.portfolio.service;

import com.portfolio.dto.StockDTO;
import com.portfolio.model.Stock;
import com.portfolio.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stock Service Tests")
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    private Stock testStock;

    @BeforeEach
    void setUp() {
        testStock = new Stock();
        testStock.setTicker("AAPL");
        testStock.setCompanyName("Apple Inc.");
        testStock.setCurrentPrice(new BigDecimal("185.50"));
        testStock.setPreviousClose(new BigDecimal("183.20"));
        testStock.setDayChange(new BigDecimal("2.30"));
        testStock.setDayChangePercent(new BigDecimal("1.26"));
        testStock.setDayHigh(new BigDecimal("186.50"));
        testStock.setDayLow(new BigDecimal("183.00"));
    }

    @Test
    @DisplayName("Should get all available stocks")
    void testGetAllStocks() {
        Stock stock2 = new Stock();
        stock2.setTicker("GOOGL");
        stock2.setCompanyName("Alphabet Inc.");
        stock2.setCurrentPrice(new BigDecimal("141.80"));

        when(stockRepository.findAll()).thenReturn(Arrays.asList(testStock, stock2));

        List<StockDTO> stocks = stockService.getAllStocks();

        assertNotNull(stocks);
        assertEquals(2, stocks.size());
        assertEquals("AAPL", stocks.get(0).getTicker());
        assertEquals("GOOGL", stocks.get(1).getTicker());
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get stock by ticker successfully")
    void testGetStockByTicker() {
        when(stockRepository.findById("AAPL")).thenReturn(Optional.of(testStock));

        StockDTO stock = stockService.getStockByTicker("AAPL");

        assertNotNull(stock);
        assertEquals("AAPL", stock.getTicker());
        assertEquals("Apple Inc.", stock.getCompanyName());
        verify(stockRepository, times(1)).findById("AAPL");
    }

    @Test
    @DisplayName("Should throw exception when stock not found by ticker")
    void testGetStockByTickerNotFound() {
        when(stockRepository.findById("INVALID")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> stockService.getStockByTicker("INVALID"));
    }

    @Test
    @DisplayName("Should get top gainers")
    void testGetTopGainers() {
        Stock gainer1 = new Stock();
        gainer1.setTicker("TSLA");
        gainer1.setDayChangePercent(new BigDecimal("5.50"));

        Stock gainer2 = new Stock();
        gainer2.setTicker("NVDA");
        gainer2.setDayChangePercent(new BigDecimal("4.25"));

        when(stockRepository.findTopGainers()).thenReturn(Arrays.asList(gainer1, gainer2));

        List<StockDTO> gainers = stockService.getTopGainers(5);

        assertNotNull(gainers);
        assertEquals(2, gainers.size());
        verify(stockRepository, times(1)).findTopGainers();
    }

    @Test
    @DisplayName("Should get top losers")
    void testGetTopLosers() {
        Stock loser1 = new Stock();
        loser1.setTicker("DIS");
        loser1.setDayChangePercent(new BigDecimal("-2.50"));

        Stock loser2 = new Stock();
        loser2.setTicker("AMD");
        loser2.setDayChangePercent(new BigDecimal("-1.75"));

        when(stockRepository.findTopLosers()).thenReturn(Arrays.asList(loser1, loser2));

        List<StockDTO> losers = stockService.getTopLosers(5);

        assertNotNull(losers);
        assertEquals(2, losers.size());
        verify(stockRepository, times(1)).findTopLosers();
    }

    
}
