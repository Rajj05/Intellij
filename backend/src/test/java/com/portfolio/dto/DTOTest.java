package com.portfolio.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTO Tests")
class DTOTest {

    @Test
    @DisplayName("ApiResponse should create success and error responses correctly")
    void testApiResponseCreation() {
        // Test success response with data
        ApiResponse<String> successResponse = ApiResponse.success("Test data");
        assertTrue(successResponse.isSuccess());
        assertEquals("Success", successResponse.getMessage());
        assertEquals("Test data", successResponse.getData());

        // Test success response with custom message
        ApiResponse<Integer> customSuccess = ApiResponse.success("Custom message", 42);
        assertTrue(customSuccess.isSuccess());
        assertEquals("Custom message", customSuccess.getMessage());
        assertEquals(42, customSuccess.getData());

        // Test error response
        ApiResponse<Object> errorResponse = ApiResponse.error("Something went wrong");
        assertFalse(errorResponse.isSuccess());
        assertEquals("Something went wrong", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    @DisplayName("PortfolioSummaryDTO should hold portfolio data correctly")
    void testPortfolioSummaryDTO() {
        HoldingDTO holding = new HoldingDTO();
        holding.setTicker("AAPL");
        holding.setQuantity(new BigDecimal("10"));
        holding.setCurrentPrice(new BigDecimal("185.50"));

        PortfolioSummaryDTO summary = PortfolioSummaryDTO.builder()
                .userId(1L)
                .username("testuser")
                .walletBalance(new BigDecimal("5000.00"))
                .totalInvested(new BigDecimal("10000.00"))
                .totalCurrentValue(new BigDecimal("12000.00"))
                .totalBalance(new BigDecimal("17000.00"))
                .totalGainLoss(new BigDecimal("2000.00"))
                .totalGainLossPercent(new BigDecimal("20.00"))
                .dailyGainLoss(new BigDecimal("150.00"))
                .dailyGainLossPercent(new BigDecimal("1.25"))
                .totalAssets(5)
                .holdings(Arrays.asList(holding))
                .build();

        assertEquals(1L, summary.getUserId());
        assertEquals("testuser", summary.getUsername());
        assertEquals(new BigDecimal("5000.00"), summary.getWalletBalance());
        assertEquals(new BigDecimal("12000.00"), summary.getTotalCurrentValue());
        assertEquals(new BigDecimal("2000.00"), summary.getTotalGainLoss());
        assertEquals(5, summary.getTotalAssets());
        assertEquals(1, summary.getHoldings().size());
        assertEquals("AAPL", summary.getHoldings().get(0).getTicker());
    }

    @Test
    @DisplayName("BuyRequest should hold trade request data correctly")
    void testBuyRequestDTO() {
        BuyRequest buyRequest = new BuyRequest();
        buyRequest.setUserId(1L);
        buyRequest.setTicker("TSLA");
        buyRequest.setQuantity(new BigDecimal("5.5"));

        assertEquals(1L, buyRequest.getUserId());
        assertEquals("TSLA", buyRequest.getTicker());
        assertEquals(new BigDecimal("5.5"), buyRequest.getQuantity());

        BuyRequest buyRequest2 = new BuyRequest(2L, "NVDA", new BigDecimal("10"), false);
        assertEquals(2L, buyRequest2.getUserId());
        assertEquals("NVDA", buyRequest2.getTicker());
        assertEquals(new BigDecimal("10"), buyRequest2.getQuantity());
    }
}
