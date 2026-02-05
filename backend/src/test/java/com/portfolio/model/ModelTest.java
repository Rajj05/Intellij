package com.portfolio.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Model Entity Tests")
class ModelTest {

    @Test
    @DisplayName("Should create User with all properties")
    void testUserEntity() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setWalletBalance(new BigDecimal("10000.00"));

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(new BigDecimal("10000.00"), user.getWalletBalance());
    }

    @Test
    @DisplayName("Should create Stock with price change data")
    void testStockEntity() {
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
        assertTrue(stock.getDayChange().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should create Transaction with buy details")
    void testTransactionEntity() {
        User user = new User();
        user.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(user);
        transaction.setTicker("TSLA");
        transaction.setTransactionType(Transaction.TransactionType.BUY);
        transaction.setQuantity(new BigDecimal("5.000000"));
        transaction.setPricePerUnit(new BigDecimal("250.00"));
        transaction.setTotalAmount(new BigDecimal("1250.00"));

        assertEquals("TSLA", transaction.getTicker());
        assertEquals(Transaction.TransactionType.BUY, transaction.getTransactionType());
        assertEquals(new BigDecimal("1250.00"), transaction.getTotalAmount());
    }

    @Test
    @DisplayName("Should calculate PortfolioHolding profit/loss correctly")
    void testPortfolioHoldingEntity() {
        Stock stock = new Stock();
        stock.setTicker("MSFT");
        stock.setCurrentPrice(new BigDecimal("400.00"));

        PortfolioHolding holding = new PortfolioHolding();
        holding.setStock(stock);
        holding.setQuantity(new BigDecimal("10"));
        holding.setTotalInvested(new BigDecimal("3500.00"));

        // Current value = 10 * 400 = 4000
        assertEquals(new BigDecimal("4000.00"), holding.getCurrentValue());
        // Profit = 4000 - 3500 = 500
        assertEquals(new BigDecimal("500.00"), holding.getProfitLoss());
    }

    @Test
    @DisplayName("Should create Alert and Notification with types")
    void testAlertAndNotificationEntities() {
        User user = new User();
        user.setId(1L);

        Alert alert = new Alert();
        alert.setUser(user);
        alert.setTicker("GOOGL");
        alert.setAlertType(Alert.AlertType.PRICE_DROP);
        alert.setThreshold(new BigDecimal("5.0"));

        assertEquals("GOOGL", alert.getTicker());
        assertEquals(Alert.AlertType.PRICE_DROP, alert.getAlertType());

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTicker("GOOGL");
        notification.setNotificationType(Notification.NotificationType.PRICE_DROP);
        notification.setTitle("Price Alert");
        notification.setMessage("GOOGL dropped 5%");
        notification.setIsRead(false);

        assertEquals("Price Alert", notification.getTitle());
        assertEquals(Notification.NotificationType.PRICE_DROP, notification.getNotificationType());
        assertFalse(notification.getIsRead());
    }
}
