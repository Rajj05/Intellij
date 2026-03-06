package com.portfolio.integration;

import com.portfolio.dto.*;
import com.portfolio.model.*;
import com.portfolio.repository.*;
import com.portfolio.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that execute real service code against H2 database.
 * These tests contribute to code coverage because they run actual implementations.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private StockService stockService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PortfolioHoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private User testUser;
    private Stock testStock;

    @BeforeEach
    void setUp() {
        // Clean up
        notificationRepository.deleteAll();
        alertRepository.deleteAll();
        transactionRepository.deleteAll();
        holdingRepository.deleteAll();
        stockRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("integrationuser");
        testUser.setEmail("integration@test.com");
        testUser.setPassword("testpassword");
        testUser.setWalletBalance(new BigDecimal("50000.00"));
        testUser = userRepository.save(testUser);

        // Create test stock
        testStock = new Stock();
        testStock.setTicker("AAPL");
        testStock.setCompanyName("Apple Inc.");
        testStock.setCurrentPrice(new BigDecimal("185.50"));
        testStock.setPreviousClose(new BigDecimal("183.20"));
        testStock.setDayChange(new BigDecimal("2.30"));
        testStock.setDayChangePercent(new BigDecimal("1.26"));
        testStock.setDayHigh(new BigDecimal("186.00"));
        testStock.setDayLow(new BigDecimal("182.00"));
        testStock = stockRepository.save(testStock);
    }

    // ==================== USER SERVICE TESTS ====================

    @Test
    @Order(1)
    @DisplayName("UserService - Get user by ID")
    void testGetUserById() {
        User user = userService.getUserById(testUser.getId());
        
        assertNotNull(user);
        assertEquals("integrationuser", user.getUsername());
        assertEquals("integration@test.com", user.getEmail());
        assertEquals(new BigDecimal("50000.00"), user.getWalletBalance());
    }

    @Test
    @Order(2)
    @DisplayName("UserService - Get user by username")
    void testGetUserByUsername() {
        User user = userService.getUserByUsername("integrationuser");
        
        assertNotNull(user);
        assertEquals(testUser.getId(), user.getId());
    }

    @Test
    @Order(3)
    @DisplayName("UserService - Get wallet balance")
    void testGetWalletBalance() {
        BigDecimal balance = userService.getWalletBalance(testUser.getId());
        
        assertEquals(new BigDecimal("50000.00"), balance);
    }

    @Test
    @Order(4)
    @DisplayName("UserService - Update wallet balance (add funds)")
    void testUpdateWalletBalanceAdd() {
        BigDecimal newBalance = userService.updateWalletBalance(testUser.getId(), new BigDecimal("5000.00"));
        
        assertEquals(new BigDecimal("55000.00"), newBalance);
    }

    @Test
    @Order(5)
    @DisplayName("UserService - Update wallet balance (withdraw)")
    void testUpdateWalletBalanceWithdraw() {
        BigDecimal newBalance = userService.updateWalletBalance(testUser.getId(), new BigDecimal("-1000.00"));
        
        assertEquals(new BigDecimal("49000.00"), newBalance);
    }

    @Test
    @Order(6)
    @DisplayName("UserService - Insufficient funds throws exception")
    void testInsufficientFunds() {
        assertThrows(RuntimeException.class, () -> {
            userService.updateWalletBalance(testUser.getId(), new BigDecimal("-100000.00"));
        });
    }

    @Test
    @Order(7)
    @DisplayName("UserService - Create new user")
    void testCreateUser() {
        User newUser = userService.createUser("newuser", "newuser@test.com", "password123");
        
        assertNotNull(newUser);
        assertNotNull(newUser.getId());
        assertEquals("newuser", newUser.getUsername());
        assertEquals(new BigDecimal("50000.00"), newUser.getWalletBalance());
    }

    @Test
    @Order(8)
    @DisplayName("UserService - Duplicate username throws exception")
    void testDuplicateUsername() {
        assertThrows(RuntimeException.class, () -> {
            userService.createUser("integrationuser", "another@test.com", "password123");
        });
    }

    @Test
    @Order(9)
    @DisplayName("UserService - Reset wallet")
    void testResetWallet() {
        userService.updateWalletBalance(testUser.getId(), new BigDecimal("-30000.00"));
        BigDecimal resetBalance = userService.resetWallet(testUser.getId());
        
        assertEquals(new BigDecimal("50000.00"), resetBalance);
    }

    // ==================== STOCK SERVICE TESTS ====================

    @Test
    @Order(10)
    @DisplayName("StockService - Get all stocks")
    void testGetAllStocks() {
        // Add more stocks
        Stock stock2 = new Stock();
        stock2.setTicker("GOOGL");
        stock2.setCompanyName("Alphabet Inc.");
        stock2.setCurrentPrice(new BigDecimal("141.80"));
        stock2.setPreviousClose(new BigDecimal("140.50"));
        stock2.setDayChange(new BigDecimal("1.30"));
        stock2.setDayChangePercent(new BigDecimal("0.93"));
        stockRepository.save(stock2);

        List<StockDTO> stocks = stockService.getAllStocks();
        
        assertNotNull(stocks);
        assertEquals(2, stocks.size());
    }

    @Test
    @Order(11)
    @DisplayName("StockService - Get stock by ticker")
    void testGetStockByTicker() {
        StockDTO stock = stockService.getStockByTicker("AAPL");
        
        assertNotNull(stock);
        assertEquals("AAPL", stock.getTicker());
        assertEquals("Apple Inc.", stock.getCompanyName());
        assertEquals(new BigDecimal("185.50"), stock.getCurrentPrice());
    }

    @Test
    @Order(12)
    @DisplayName("StockService - Stock not found throws exception")
    void testStockNotFound() {
        assertThrows(RuntimeException.class, () -> {
            stockService.getStockByTicker("INVALID");
        });
    }

    @Test
    @Order(13)
    @DisplayName("StockService - Get top gainers")
    void testGetTopGainers() {
        // Add more stocks with different performance
        Stock gainer = new Stock();
        gainer.setTicker("NVDA");
        gainer.setCompanyName("NVIDIA Corporation");
        gainer.setCurrentPrice(new BigDecimal("875.50"));
        gainer.setPreviousClose(new BigDecimal("850.00"));
        gainer.setDayChange(new BigDecimal("25.50"));
        gainer.setDayChangePercent(new BigDecimal("3.00"));
        stockRepository.save(gainer);

        List<StockDTO> topGainers = stockService.getTopGainers(5);
        
        assertNotNull(topGainers);
        assertFalse(topGainers.isEmpty());
    }

    @Test
    @Order(14)
    @DisplayName("StockService - Get top losers")
    void testGetTopLosers() {
        Stock loser = new Stock();
        loser.setTicker("TSLA");
        loser.setCompanyName("Tesla Inc.");
        loser.setCurrentPrice(new BigDecimal("248.75"));
        loser.setPreviousClose(new BigDecimal("260.00"));
        loser.setDayChange(new BigDecimal("-11.25"));
        loser.setDayChangePercent(new BigDecimal("-4.33"));
        stockRepository.save(loser);

        List<StockDTO> topLosers = stockService.getTopLosers(5);
        
        assertNotNull(topLosers);
    }

    @Test
    @Order(15)
    @DisplayName("StockService - Search stocks")
    void testSearchStocks() {
        List<StockDTO> results = stockService.searchStocks("Apple");
        
        assertNotNull(results);
        assertTrue(results.stream().anyMatch(s -> s.getTicker().equals("AAPL")));
    }

    @Test
    @Order(16)
    @DisplayName("StockService - Update stock price")
    void testUpdateStockPrice() {
        stockService.updateStockPrice("AAPL", new BigDecimal("190.00"));
        
        StockDTO updated = stockService.getStockByTicker("AAPL");
        assertEquals(new BigDecimal("190.00"), updated.getCurrentPrice());
    }

    // ==================== PORTFOLIO SERVICE TESTS ====================

    @Test
    @Order(20)
    @DisplayName("PortfolioService - Get portfolio summary (empty)")
    void testGetPortfolioSummaryEmpty() {
        PortfolioSummaryDTO summary = portfolioService.getPortfolioSummary(testUser.getId());
        
        assertNotNull(summary);
        assertEquals(testUser.getId(), summary.getUserId());
        assertEquals("integrationuser", summary.getUsername());
        assertEquals(0, summary.getTotalAssets());
    }

    @Test
    @Order(21)
    @DisplayName("PortfolioService - Buy stock")
    void testBuyStock() {
        BuyRequest request = new BuyRequest();
        request.setUserId(testUser.getId());
        request.setTicker("AAPL");
        request.setQuantity(new BigDecimal("10"));

        TransactionDTO transaction = portfolioService.buyStock(request);
        
        assertNotNull(transaction);
        assertEquals(Transaction.TransactionType.BUY, transaction.getTransactionType());
        assertEquals("AAPL", transaction.getTicker());
        assertEquals(new BigDecimal("10"), transaction.getQuantity());
        
        // Verify wallet was deducted
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        BigDecimal expectedBalance = new BigDecimal("50000.00").subtract(
            new BigDecimal("185.50").multiply(new BigDecimal("10"))
        );
        assertEquals(expectedBalance.setScale(2), updatedUser.getWalletBalance().setScale(2));
    }

    @Test
    @Order(22)
    @DisplayName("PortfolioService - Buy stock insufficient funds")
    void testBuyStockInsufficientFunds() {
        BuyRequest request = new BuyRequest();
        request.setUserId(testUser.getId());
        request.setTicker("AAPL");
        request.setQuantity(new BigDecimal("1000")); // Too many shares

        assertThrows(RuntimeException.class, () -> {
            portfolioService.buyStock(request);
        });
    }

    @Test
    @Order(23)
    @DisplayName("PortfolioService - Get portfolio with holdings")
    void testGetPortfolioWithHoldings() {
        // First buy some stock
        BuyRequest buyRequest = new BuyRequest();
        buyRequest.setUserId(testUser.getId());
        buyRequest.setTicker("AAPL");
        buyRequest.setQuantity(new BigDecimal("5"));
        portfolioService.buyStock(buyRequest);

        // Get portfolio summary
        PortfolioSummaryDTO summary = portfolioService.getPortfolioSummary(testUser.getId());
        
        assertNotNull(summary);
        assertEquals(1, summary.getTotalAssets());
        assertFalse(summary.getHoldings().isEmpty());
        
        HoldingDTO holding = summary.getHoldings().get(0);
        assertEquals("AAPL", holding.getTicker());
        assertEquals(new BigDecimal("5"), holding.getQuantity());
    }

    @Test
    @Order(24)
    @DisplayName("PortfolioService - Sell stock")
    void testSellStock() {
        // First buy stock
        BuyRequest buyRequest = new BuyRequest();
        buyRequest.setUserId(testUser.getId());
        buyRequest.setTicker("AAPL");
        buyRequest.setQuantity(new BigDecimal("10"));
        portfolioService.buyStock(buyRequest);

        // Then sell
        SellRequest sellRequest = new SellRequest();
        sellRequest.setUserId(testUser.getId());
        sellRequest.setTicker("AAPL");
        sellRequest.setQuantity(new BigDecimal("5"));
        
        TransactionDTO sellTransaction = portfolioService.sellStock(sellRequest);
        
        assertNotNull(sellTransaction);
        assertEquals(Transaction.TransactionType.SELL, sellTransaction.getTransactionType());
        assertEquals(new BigDecimal("5"), sellTransaction.getQuantity());
    }

    @Test
    @Order(25)
    @DisplayName("PortfolioService - Sell more than owned throws exception")
    void testSellMoreThanOwned() {
        // Buy 5 shares
        BuyRequest buyRequest = new BuyRequest();
        buyRequest.setUserId(testUser.getId());
        buyRequest.setTicker("AAPL");
        buyRequest.setQuantity(new BigDecimal("5"));
        portfolioService.buyStock(buyRequest);

        // Try to sell 10
        SellRequest sellRequest = new SellRequest();
        sellRequest.setUserId(testUser.getId());
        sellRequest.setTicker("AAPL");
        sellRequest.setQuantity(new BigDecimal("10"));

        assertThrows(RuntimeException.class, () -> {
            portfolioService.sellStock(sellRequest);
        });
    }

    @Test
    @Order(26)
    @DisplayName("PortfolioService - Get user holdings")
    void testGetUserHoldings() {
        // Buy stock
        BuyRequest buyRequest = new BuyRequest();
        buyRequest.setUserId(testUser.getId());
        buyRequest.setTicker("AAPL");
        buyRequest.setQuantity(new BigDecimal("10"));
        portfolioService.buyStock(buyRequest);

        List<HoldingDTO> holdings = portfolioService.getUserHoldings(testUser.getId());
        
        assertNotNull(holdings);
        assertEquals(1, holdings.size());
        assertEquals("AAPL", holdings.get(0).getTicker());
    }

    @Test
    @Order(27)
    @DisplayName("PortfolioService - Get user transactions")
    void testGetUserTransactions() {
        // Buy stock
        BuyRequest buyRequest = new BuyRequest();
        buyRequest.setUserId(testUser.getId());
        buyRequest.setTicker("AAPL");
        buyRequest.setQuantity(new BigDecimal("10"));
        portfolioService.buyStock(buyRequest);

        List<TransactionDTO> transactions = portfolioService.getTransactionHistory(testUser.getId());
        
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }

    // ==================== ALERT SERVICE TESTS ====================

    @Test
    @Order(30)
    @DisplayName("AlertService - Create alert")
    void testCreateAlert() {
        AlertDTO alert = alertService.createAlert(
            testUser.getId(),
            "AAPL",
            Alert.AlertType.PRICE_DROP,
            new BigDecimal("5.0")
        );
        
        assertNotNull(alert);
        assertNotNull(alert.getId());
        assertEquals("AAPL", alert.getTicker());
        assertEquals(Alert.AlertType.PRICE_DROP, alert.getAlertType());
    }

    @Test
    @Order(31)
    @DisplayName("AlertService - Get user alerts")
    void testGetUserAlerts() {
        // Create alerts
        alertService.createAlert(testUser.getId(), "AAPL", Alert.AlertType.PRICE_DROP, new BigDecimal("5.0"));
        alertService.createAlert(testUser.getId(), "AAPL", Alert.AlertType.DAILY_GAIN, new BigDecimal("3.0"));

        List<AlertDTO> alerts = alertService.getUserAlerts(testUser.getId());
        
        assertNotNull(alerts);
        assertEquals(2, alerts.size());
    }

    @Test
    @Order(32)
    @DisplayName("AlertService - Delete alert")
    void testDeleteAlert() {
        AlertDTO alert = alertService.createAlert(
            testUser.getId(),
            "AAPL",
            Alert.AlertType.PRICE_DROP,
            new BigDecimal("5.0")
        );

        alertService.deleteAlert(alert.getId());

        List<AlertDTO> alerts = alertService.getUserAlerts(testUser.getId());
        assertTrue(alerts.isEmpty());
    }

    // ==================== NOTIFICATION SERVICE TESTS ====================

    @Test
    @Order(40)
    @DisplayName("NotificationService - Create notification")
    void testCreateNotification() {
        Notification notification = notificationService.createNotification(
            testUser.getId(),
            "Price Alert",
            "AAPL dropped 5%",
            "AAPL",
            Notification.NotificationType.PRICE_DROP
        );
        
        assertNotNull(notification);
        assertNotNull(notification.getId());
        assertEquals("Price Alert", notification.getTitle());
    }

    @Test
    @Order(41)
    @DisplayName("NotificationService - Get unread notifications")
    void testGetUnreadNotifications() {
        notificationService.createNotification(
            testUser.getId(),
            "Alert 1",
            "Message 1",
            "AAPL",
            Notification.NotificationType.PRICE_DROP
        );
        notificationService.createNotification(
            testUser.getId(),
            "Alert 2",
            "Message 2",
            "GOOGL",
            Notification.NotificationType.DAILY_GAIN
        );

        List<Notification> unread = notificationService.getUnreadNotifications(testUser.getId());
        
        assertNotNull(unread);
        assertEquals(2, unread.size());
    }

    @Test
    @Order(42)
    @DisplayName("NotificationService - Mark as read")
    void testMarkNotificationAsRead() {
        Notification notification = notificationService.createNotification(
            testUser.getId(),
            "Test Alert",
            "Test message",
            "AAPL",
            Notification.NotificationType.SYSTEM
        );

        notificationService.markAsRead(notification.getId());

        List<Notification> unread = notificationService.getUnreadNotifications(testUser.getId());
        assertTrue(unread.isEmpty());
    }

    @Test
    @Order(43)
    @DisplayName("NotificationService - Get all notifications")
    void testGetAllNotifications() {
        Notification n1 = notificationService.createNotification(
            testUser.getId(),
            "Alert 1",
            "Message 1",
            "AAPL",
            Notification.NotificationType.PRICE_DROP
        );
        notificationService.markAsRead(n1.getId());
        
        notificationService.createNotification(
            testUser.getId(),
            "Alert 2",
            "Message 2",
            "GOOGL",
            Notification.NotificationType.DAILY_GAIN
        );

        List<Notification> all = notificationService.getAllNotifications(testUser.getId());
        
        assertNotNull(all);
        assertEquals(2, all.size());
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    @Order(50)
    @DisplayName("User not found throws exception")
    void testUserNotFound() {
        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(99999L);
        });
    }

    @Test
    @Order(51)
    @DisplayName("Portfolio for non-existent user throws exception")
    void testPortfolioUserNotFound() {
        assertThrows(RuntimeException.class, () -> {
            portfolioService.getPortfolioSummary(99999L);
        });
    }

    @Test
    @Order(52)
    @DisplayName("Buy non-existent stock throws exception")
    void testBuyNonExistentStock() {
        BuyRequest request = new BuyRequest();
        request.setUserId(testUser.getId());
        request.setTicker("INVALID");
        request.setQuantity(new BigDecimal("10"));

        assertThrows(RuntimeException.class, () -> {
            portfolioService.buyStock(request);
        });
    }

    @Test
    @Order(53)
    @DisplayName("Sell stock not owned throws exception")
    void testSellStockNotOwned() {
        SellRequest request = new SellRequest();
        request.setUserId(testUser.getId());
        request.setTicker("AAPL");
        request.setQuantity(new BigDecimal("10"));

        assertThrows(RuntimeException.class, () -> {
            portfolioService.sellStock(request);
        });
    }
}
