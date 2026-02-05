
package com.portfolio.controller;

import com.portfolio.dto.*;
import com.portfolio.model.Transaction.TransactionType;
import com.portfolio.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@DisplayName("Transaction Controller Tests")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Test
    @DisplayName("Should buy stock successfully")
    void testBuyStock() throws Exception {
        TransactionDTO transaction = TransactionDTO.builder()
                .id(1L)
                .ticker("AAPL")
                .quantity(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("185.50"))
                .totalAmount(new BigDecimal("1855.00"))
                .transactionType(TransactionType.BUY)
                .build();

        when(portfolioService.buyStock(any(BuyRequest.class))).thenReturn(transaction);

        mockMvc.perform(post("/api/transaction/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"ticker\":\"AAPL\",\"quantity\":10,\"pricePerUnit\":185.50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock purchased successfully"))
                .andExpect(jsonPath("$.data.ticker").value("AAPL"))
                .andExpect(jsonPath("$.data.quantity").value("10"))
                .andExpect(jsonPath("$.data.transactionType").value("BUY"));

        verify(portfolioService, times(1)).buyStock(any(BuyRequest.class));
    }

    @Test
    @DisplayName("Should return error when buying with insufficient funds")
    void testBuyStockInsufficientFunds() throws Exception {
        when(portfolioService.buyStock(any(BuyRequest.class)))
                .thenThrow(new RuntimeException("Insufficient funds"));

        mockMvc.perform(post("/api/transaction/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"ticker\":\"AAPL\",\"quantity\":1000,\"pricePerUnit\":185.50}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

}
