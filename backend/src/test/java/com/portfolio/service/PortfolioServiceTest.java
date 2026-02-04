package com.portfolio.service;

import com.portfolio.dto.PortfolioSummaryDTO;
import com.portfolio.model.*;
import com.portfolio.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Portfolio Service Tests")
class PortfolioServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private StockRepository stockRepository;
    
    @Mock
    private PortfolioHoldingRepository holdingRepository;
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private AlertService alertService;

    @InjectMocks
    private PortfolioService portfolioService;

    private User testUser;
    private Stock testStock;
    private PortfolioHolding testHolding;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setWalletBalance(new BigDecimal("50000.00"));

        testStock = new Stock();
        testStock.setTicker("AAPL");
        testStock.setCompanyName("Apple Inc.");
        testStock.setCurrentPrice(new BigDecimal("185.50"));
        testStock.setDayChange(new BigDecimal("2.30"));
        testStock.setDayChangePercent(new BigDecimal("1.26"));

        testHolding = new PortfolioHolding();
        testHolding.setId(1L);
        testHolding.setUser(testUser);
        testHolding.setStock(testStock);
        testHolding.setQuantity(new BigDecimal("10"));
        testHolding.setAverageCost(new BigDecimal("180.00"));
        testHolding.setTotalInvested(new BigDecimal("1800.00"));
    }

    @Test
    @DisplayName("Should get portfolio summary for user")
    void testGetPortfolioSummary() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(holdingRepository.findByUserIdWithStock(1L)).thenReturn(Arrays.asList(testHolding));

        PortfolioSummaryDTO summary = portfolioService.getPortfolioSummary(1L);

        assertNotNull(summary);
        assertEquals(1L, summary.getUserId());
        assertEquals("testuser", summary.getUsername());
        assertEquals(new BigDecimal("50000.00"), summary.getWalletBalance());
        assertEquals(1, summary.getTotalAssets());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testGetPortfolioSummaryUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> portfolioService.getPortfolioSummary(999L));
        
        assertEquals("User not found", exception.getMessage());
    }
}
