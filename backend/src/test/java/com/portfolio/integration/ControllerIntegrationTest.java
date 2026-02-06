package com.portfolio.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.model.*;
import com.portfolio.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller Integration tests using MockMvc.
 * Tests actual HTTP endpoints and their handlers.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PortfolioHoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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
        testUser.setUsername("controlleruser");
        testUser.setEmail("controller@test.com");
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

    // ==================== USER CONTROLLER TESTS ====================
    // Note: endpoint is /api/user (singular)

    @Test
    @Order(1)
    @DisplayName("GET /api/user/{id} - Get user by ID")
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("controlleruser"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/user/{id} - User not found")
    void testGetUserNotFound() throws Exception {
        mockMvc.perform(get("/api/user/99999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/user/{id}/wallet - Get wallet balance")
    void testGetWalletBalance() throws Exception {
        mockMvc.perform(get("/api/user/" + testUser.getId() + "/wallet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.walletBalance").value(50000.00));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/user/{id}/wallet/reset - Reset wallet")
    void testResetWallet() throws Exception {
        mockMvc.perform(post("/api/user/" + testUser.getId() + "/wallet/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.walletBalance").value(50000.00));
    }

    // ==================== STOCK CONTROLLER TESTS ====================

    @Test
    @Order(10)
    @DisplayName("GET /api/stocks - Get all stocks")
    void testGetAllStocks() throws Exception {
        mockMvc.perform(get("/api/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(11)
    @DisplayName("GET /api/stocks/{ticker} - Get stock by ticker")
    void testGetStockByTicker() throws Exception {
        mockMvc.perform(get("/api/stocks/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ticker").value("AAPL"))
                .andExpect(jsonPath("$.data.companyName").value("Apple Inc."));
    }

    @Test
    @Order(12)
    @DisplayName("GET /api/stocks/{ticker} - Stock not found")
    void testGetStockNotFound() throws Exception {
        mockMvc.perform(get("/api/stocks/INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(13)
    @DisplayName("GET /api/stocks/top-gainers - Get top gainers")
    void testGetTopGainers() throws Exception {
        mockMvc.perform(get("/api/stocks/top-gainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(14)
    @DisplayName("GET /api/stocks/top-losers - Get top losers")
    void testGetTopLosers() throws Exception {
        mockMvc.perform(get("/api/stocks/top-losers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(15)
    @DisplayName("GET /api/stocks/search - Search stocks")
    void testSearchStocks() throws Exception {
        mockMvc.perform(get("/api/stocks/search").param("q", "Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ==================== PORTFOLIO CONTROLLER TESTS ====================

    @Test
    @Order(20)
    @DisplayName("GET /api/portfolio/{userId}/summary - Get portfolio summary")
    void testGetPortfolioSummary() throws Exception {
        mockMvc.perform(get("/api/portfolio/" + testUser.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("controlleruser"))
                .andExpect(jsonPath("$.data.walletBalance").value(50000.00));
    }

    @Test
    @Order(21)
    @DisplayName("GET /api/portfolio/{userId}/holdings - Get holdings")
    void testGetHoldings() throws Exception {
        mockMvc.perform(get("/api/portfolio/" + testUser.getId() + "/holdings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    // ==================== TRANSACTION CONTROLLER TESTS ====================
    // Note: endpoint is /api/transaction (singular)

    @Test
    @Order(30)
    @DisplayName("POST /api/transaction/buy - Buy stock")
    void testBuyStock() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", testUser.getId());
        request.put("ticker", "AAPL");
        request.put("quantity", 10);

        mockMvc.perform(post("/api/transaction/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transactionType").value("BUY"))
                .andExpect(jsonPath("$.data.ticker").value("AAPL"));
    }

    @Test
    @Order(31)
    @DisplayName("POST /api/transaction/buy - Insufficient funds")
    void testBuyStockInsufficientFunds() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", testUser.getId());
        request.put("ticker", "AAPL");
        request.put("quantity", 10000); // Too many

        mockMvc.perform(post("/api/transaction/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(32)
    @DisplayName("POST /api/transaction/sell - Sell stock")
    void testSellStock() throws Exception {
        // First buy
        Map<String, Object> buyRequest = new HashMap<>();
        buyRequest.put("userId", testUser.getId());
        buyRequest.put("ticker", "AAPL");
        buyRequest.put("quantity", 10);
        mockMvc.perform(post("/api/transaction/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buyRequest)));

        // Then sell
        Map<String, Object> sellRequest = new HashMap<>();
        sellRequest.put("userId", testUser.getId());
        sellRequest.put("ticker", "AAPL");
        sellRequest.put("quantity", 5);

        mockMvc.perform(post("/api/transaction/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sellRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transactionType").value("SELL"));
    }

    // ==================== ALERT CONTROLLER TESTS ====================

    @Test
    @Order(40)
    @DisplayName("GET /api/alerts/{userId} - Get user alerts")
    void testGetUserAlerts() throws Exception {
        mockMvc.perform(get("/api/alerts/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(41)
    @DisplayName("POST /api/alerts - Create alert")
    void testCreateAlert() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", testUser.getId());
        request.put("ticker", "AAPL");
        request.put("alertType", "PRICE_DROP");
        request.put("threshold", 5.0);

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ticker").value("AAPL"));
    }

    @Test
    @Order(42)
    @DisplayName("DELETE /api/alerts/{alertId} - Delete alert")
    void testDeleteAlert() throws Exception {
        // First create an alert
        Alert alert = new Alert();
        alert.setUser(testUser);
        alert.setTicker("AAPL");
        alert.setAlertType(Alert.AlertType.PRICE_DROP);
        alert.setThreshold(new BigDecimal("5.0"));
        alert.setTitle("Test Alert");
        alert = alertRepository.save(alert);

        mockMvc.perform(delete("/api/alerts/" + alert.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ==================== NOTIFICATION CONTROLLER TESTS ====================

    @Test
    @Order(50)
    @DisplayName("GET /api/notifications/{userId}/unread - Get unread notifications")
    void testGetUnreadNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications/" + testUser.getId() + "/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(51)
    @DisplayName("GET /api/notifications/{userId} - Get all notifications")
    void testGetAllNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(52)
    @DisplayName("PUT /api/notifications/{id}/read - Mark as read")
    void testMarkNotificationAsRead() throws Exception {
        // Create notification
        Notification notification = new Notification();
        notification.setUser(testUser);
        notification.setTitle("Test");
        notification.setMessage("Test message");
        notification.setTicker("AAPL");
        notification.setNotificationType(Notification.NotificationType.SYSTEM);
        notification.setIsRead(false);
        notification = notificationRepository.save(notification);

        mockMvc.perform(put("/api/notifications/" + notification.getId() + "/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
