package com.portfolio.controller;

import com.portfolio.dto.HoldingDTO;
import com.portfolio.dto.PortfolioSummaryDTO;
import com.portfolio.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Arrays;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
@DisplayName("Portfolio Controller Tests")
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Test
    @DisplayName("Should return portfolio summary for valid user")
    void testGetPortfolioSummary() throws Exception {
        PortfolioSummaryDTO summary = PortfolioSummaryDTO.builder()
                .userId(1L)
                .username("testuser")
                .walletBalance(new BigDecimal("50000.00"))
                .totalInvested(new BigDecimal("1800.00"))
                .totalCurrentValue(new BigDecimal("1855.00"))
                .totalBalance(new BigDecimal("51855.00"))
                .totalGainLoss(new BigDecimal("55.00"))
                .totalGainLossPercent(new BigDecimal("3.06"))
                .dailyGainLoss(new BigDecimal("23.00"))
                .dailyGainLossPercent(new BigDecimal("1.26"))
                .totalAssets(1)
                .holdings(Arrays.asList())
                .build();

        when(portfolioService.getPortfolioSummary(1L)).thenReturn(summary);

        mockMvc.perform(get("/api/portfolio/1/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.totalAssets").value(1));

        verify(portfolioService, times(1)).getPortfolioSummary(1L);
    }

    @Test
    @DisplayName("Should return error for invalid user")
    void testGetPortfolioSummaryError() throws Exception {
        when(portfolioService.getPortfolioSummary(999L))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/portfolio/999/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}
