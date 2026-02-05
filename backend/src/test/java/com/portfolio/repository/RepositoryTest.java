package com.portfolio.repository;

import com.portfolio.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Repository Tests")
class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PortfolioHoldingRepository portfolioHoldingRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Test
    @DisplayName("Should find user by username and email")
    void testUserRepositoryFinders() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setWalletBalance(new BigDecimal("10000.00"));
        user = userRepository.save(user);

        // When
        Optional<User> foundByUsername = userRepository.findByUsername("testuser");
        Optional<User> foundByEmail = userRepository.findByEmail("test@example.com");
        boolean usernameExists = userRepository.existsByUsername("testuser");
        boolean emailExists = userRepository.existsByEmail("test@example.com");

        // Then
        assertTrue(foundByUsername.isPresent());
        assertEquals("testuser", foundByUsername.get().getUsername());
        assertTrue(foundByEmail.isPresent());
        assertEquals("test@example.com", foundByEmail.get().getEmail());
        assertTrue(usernameExists);
        assertTrue(emailExists);
    }

    @Test
    @DisplayName("Should find stocks by ticker and search criteria")
    void testStockRepositoryFinders() {
        // Given
        Stock stock1 = new Stock();
        stock1.setTicker("AAPL");
        stock1.setCompanyName("Apple Inc.");
        stock1.setCurrentPrice(new BigDecimal("185.50"));
        stock1.setDayChangePercent(new BigDecimal("2.5"));
        stockRepository.save(stock1);

        Stock stock2 = new Stock();
        stock2.setTicker("GOOGL");
        stock2.setCompanyName("Alphabet Inc.");
        stock2.setCurrentPrice(new BigDecimal("140.50"));
        stock2.setDayChangePercent(new BigDecimal("-1.2"));
        stockRepository.save(stock2);

        // When
        List<Stock> topGainers = stockRepository.findTopGainers();
        List<Stock> topLosers = stockRepository.findTopLosers();
        List<Stock> searchResults = stockRepository.searchStocks("apple");

        // Then
        assertFalse(topGainers.isEmpty());
        assertEquals("AAPL", topGainers.get(0).getTicker());
        assertFalse(topLosers.isEmpty());
        assertEquals("GOOGL", topLosers.get(0).getTicker());
        assertFalse(searchResults.isEmpty());
        assertEquals("AAPL", searchResults.get(0).getTicker());
    }

    @Test
    @DisplayName("Should find transactions by user and ticker")
    void testTransactionRepositoryFinders() {
        // Given
        User user = new User();
        user.setUsername("trader");
        user.setEmail("trader@example.com");
        user.setWalletBalance(new BigDecimal("5000.00"));
        user = userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTicker("TSLA");
        transaction.setTransactionType(Transaction.TransactionType.BUY);
        transaction.setQuantity(new BigDecimal("10"));
        transaction.setPricePerUnit(new BigDecimal("250.00"));
        transaction.setTotalAmount(new BigDecimal("2500.00"));
        transaction.setWalletBalanceAfter(new BigDecimal("7500.00"));
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        // When
        List<Transaction> userTransactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(user.getId());
        List<Transaction> tickerTransactions = transactionRepository.findByUserIdAndTickerOrderByTransactionDateDesc(user.getId(), "TSLA");

        // Then
        assertFalse(userTransactions.isEmpty());
        assertEquals("TSLA", userTransactions.get(0).getTicker());
        assertFalse(tickerTransactions.isEmpty());
        assertEquals(Transaction.TransactionType.BUY, tickerTransactions.get(0).getTransactionType());
    }

    @Test
    @DisplayName("Should find and manage portfolio holdings")
    void testPortfolioHoldingRepositoryOperations() {
        // Given
        User user = new User();
        user.setUsername("investor");
        user.setEmail("investor@example.com");
        user.setWalletBalance(new BigDecimal("10000.00"));
        user = userRepository.save(user);

        Stock stock = new Stock();
        stock.setTicker("MSFT");
        stock.setCompanyName("Microsoft Corporation");
        stock.setCurrentPrice(new BigDecimal("400.00"));
        stock = stockRepository.save(stock);

        PortfolioHolding holding = new PortfolioHolding();
        holding.setUser(user);
        holding.setStock(stock);
        holding.setQuantity(new BigDecimal("5"));
        holding.setAverageCost(new BigDecimal("380.00"));
        holding.setTotalInvested(new BigDecimal("1900.00"));
        portfolioHoldingRepository.save(holding);

        // When
        List<PortfolioHolding> holdings = portfolioHoldingRepository.findByUserIdWithStock(user.getId());
        Optional<PortfolioHolding> specificHolding = portfolioHoldingRepository.findByUserIdAndTicker(user.getId(), "MSFT");
        boolean exists = portfolioHoldingRepository.existsByUserIdAndStockTicker(user.getId(), "MSFT");
        long count = portfolioHoldingRepository.countByUserId(user.getId());

        // Then
        assertFalse(holdings.isEmpty());
        assertEquals("MSFT", holdings.get(0).getStock().getTicker());
        assertTrue(specificHolding.isPresent());
        assertEquals(new BigDecimal("5"), specificHolding.get().getQuantity());
        assertTrue(exists);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Should find alerts by user and ticker")
    void testAlertRepositoryFinders() {
        // Given
        User user = new User();
        user.setUsername("alertuser");
        user.setEmail("alert@example.com");
        user.setWalletBalance(new BigDecimal("5000.00"));
        user = userRepository.save(user);

        Alert alert = new Alert();
        alert.setUser(user);
        alert.setTicker("NVDA");
        alert.setAlertType(Alert.AlertType.PRICE_DROP);
        alert.setThreshold(new BigDecimal("5.0"));
        alertRepository.save(alert);

        // When
        List<Alert> userAlerts = alertRepository.findByUserId(user.getId());
        List<Alert> tickerAlerts = alertRepository.findByUserIdAndTicker(user.getId(), "NVDA");

        // Then
        assertFalse(userAlerts.isEmpty());
        assertEquals("NVDA", userAlerts.get(0).getTicker());
        assertFalse(tickerAlerts.isEmpty());
        assertEquals(Alert.AlertType.PRICE_DROP, tickerAlerts.get(0).getAlertType());
    }
}
