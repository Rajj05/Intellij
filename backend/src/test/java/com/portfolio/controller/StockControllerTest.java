package com.portfolio.controller;

import com.portfolio.dto.ApiResponse;
import com.portfolio.dto.HistoricalDataDTO;
import com.portfolio.dto.StockDTO;
import com.portfolio.model.Stock;
import com.portfolio.service.HistoricalDataService;
import com.portfolio.service.StockPriceService;
import com.portfolio.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
@DisplayName("Stock Controller Tests")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @MockBean
    private StockPriceService stockPriceService;

    @MockBean
    private HistoricalDataService historicalDataService;

    @Test
    @DisplayName("Should get all available stocks")
    void testGetAllStocks() throws Exception {
        StockDTO stock1 = StockDTO.builder()
                .ticker("AAPL")
                .companyName("Apple Inc.")
                .currentPrice(new BigDecimal("185.50"))
                .dayChange(new BigDecimal("2.30"))
                .dayChangePercent(new BigDecimal("1.26"))
                .build();

        StockDTO stock2 = StockDTO.builder()
                .ticker("GOOGL")
                .companyName("Alphabet Inc.")
                .currentPrice(new BigDecimal("141.80"))
                .dayChange(new BigDecimal("1.30"))
                .dayChangePercent(new BigDecimal("0.93"))
                .build();

        when(stockService.getAllStocks()).thenReturn(Arrays.asList(stock1, stock2));

        mockMvc.perform(get("/api/stocks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].ticker").value("AAPL"))
                .andExpect(jsonPath("$.data[1].ticker").value("GOOGL"));

        verify(stockService, times(1)).getAllStocks();
    }

    @Test
    @DisplayName("Should get stock by ticker")
    void testGetStockByTicker() throws Exception {
        StockDTO stock = StockDTO.builder()
                .ticker("AAPL")
                .companyName("Apple Inc.")
                .currentPrice(new BigDecimal("185.50"))
                .dayChange(new BigDecimal("2.30"))
                .dayChangePercent(new BigDecimal("1.26"))
                .build();

        when(stockService.getStockByTicker("AAPL")).thenReturn(stock);

        mockMvc.perform(get("/api/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ticker").value("AAPL"))
                .andExpect(jsonPath("$.data.companyName").value("Apple Inc."));

        verify(stockService, times(1)).getStockByTicker("AAPL");
    }

    @Test
    @DisplayName("Should return error for non-existent ticker")
    void testGetStockByTickerNotFound() throws Exception {
        when(stockService.getStockByTicker("INVALID"))
                .thenThrow(new RuntimeException("Stock not found"));

        mockMvc.perform(get("/api/stocks/INVALID")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get historical data with default period")
    void testGetHistoricalDataDefaultPeriod() throws Exception {
        HistoricalDataDTO historicalData = HistoricalDataDTO.builder()
                .ticker("AAPL")
                .period("1M")
                .build();

        when(historicalDataService.getHistoricalData("AAPL", "1M")).thenReturn(historicalData);

        mockMvc.perform(get("/api/stocks/AAPL/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(historicalDataService, times(1)).getHistoricalData("AAPL", "1M");
    }
}
