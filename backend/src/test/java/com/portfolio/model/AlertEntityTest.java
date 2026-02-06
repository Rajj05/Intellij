package com.portfolio.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Alert Entity Tests")
class AlertEntityTest {

    private Alert alert;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        alert = new Alert();
        alert.setUser(testUser);
        alert.setTicker("AAPL");
        alert.setAlertType(Alert.AlertType.PRICE_RISE);
        alert.setThreshold(new BigDecimal("150.50"));
    }

    @Test
    @DisplayName("Should create alert with all properties")
    void testAlertCreation() {
        assertEquals("AAPL", alert.getTicker());
        assertEquals(Alert.AlertType.PRICE_RISE, alert.getAlertType());
        assertEquals(new BigDecimal("150.50"), alert.getThreshold());
        assertEquals(testUser, alert.getUser());
    }

    @Test
    @DisplayName("Should set and get alert ID")
    void testAlertId() {
        alert.setId(1L);
        assertEquals(1L, alert.getId());
    }

    @Test
    @DisplayName("Should handle PRICE_DROP alert type")
    void testAlertPriceDropType() {
        alert.setAlertType(Alert.AlertType.PRICE_DROP);
        assertEquals(Alert.AlertType.PRICE_DROP, alert.getAlertType());
    }

    @Test
    @DisplayName("Should handle DAILY_GAIN alert type")
    void testAlertDailyGainType() {
        alert.setAlertType(Alert.AlertType.DAILY_GAIN);
        assertEquals(Alert.AlertType.DAILY_GAIN, alert.getAlertType());
    }

    @Test
    @DisplayName("Should handle DAILY_LOSS alert type")
    void testAlertDailyLossType() {
        alert.setAlertType(Alert.AlertType.DAILY_LOSS);
        assertEquals(Alert.AlertType.DAILY_LOSS, alert.getAlertType());
    }

    @Test
    @DisplayName("Should handle SYSTEM alert type")
    void testAlertSystemType() {
        alert.setAlertType(Alert.AlertType.SYSTEM);
        assertEquals(Alert.AlertType.SYSTEM, alert.getAlertType());
    }

    @Test
    @DisplayName("Should handle UNDERPERFORMING alert type")
    void testAlertUnderperformingType() {
        alert.setAlertType(Alert.AlertType.UNDERPERFORMING);
        assertEquals(Alert.AlertType.UNDERPERFORMING, alert.getAlertType());
    }

    @Test
    @DisplayName("Should update threshold value")
    void testAlertThresholdUpdate() {
        BigDecimal newThreshold = new BigDecimal("160.75");
        alert.setThreshold(newThreshold);
        assertEquals(newThreshold, alert.getThreshold());
    }

    @Test
    @DisplayName("Should update ticker symbol")
    void testAlertTickerUpdate() {
        alert.setTicker("GOOGL");
        assertEquals("GOOGL", alert.getTicker());
    }

    @Test
    @DisplayName("Should associate alert with user")
    void testAlertUserAssociation() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("anotheruser");
        newUser.setEmail("another@example.com");

        alert.setUser(newUser);
        assertEquals(newUser, alert.getUser());
        assertEquals(2L, alert.getUser().getId());
    }

    @Test
    @DisplayName("Should create alert with no-argument constructor")
    void testAlertNoArgConstructor() {
        Alert newAlert = new Alert();
        assertNotNull(newAlert);
        assertNull(newAlert.getId());
        assertNull(newAlert.getTicker());
        assertNull(newAlert.getAlertType());
        assertNull(newAlert.getThreshold());
        assertNull(newAlert.getUser());
    }

    @Test
    @DisplayName("Should handle negative threshold values")
    void testAlertNegativeThreshold() {
        alert.setThreshold(new BigDecimal("-5.00"));
        assertEquals(new BigDecimal("-5.00"), alert.getThreshold());
    }

    @Test
    @DisplayName("Should handle decimal threshold values")
    void testAlertDecimalThreshold() {
        BigDecimal decimalThreshold = new BigDecimal("0.5");
        alert.setThreshold(decimalThreshold);
        assertEquals(decimalThreshold, alert.getThreshold());
    }

    @Test
    @DisplayName("Should allow null ticker for SYSTEM alerts")
    void testAlertNullTicker() {
        alert.setTicker(null);
        alert.setAlertType(Alert.AlertType.SYSTEM);
        assertNull(alert.getTicker());
        assertEquals(Alert.AlertType.SYSTEM, alert.getAlertType());
    }

    @Test
    @DisplayName("Should create alert with all-argument constructor")
    void testAlertAllArgConstructor() {
        Alert newAlert = new Alert(2L, testUser, "MSFT", Alert.AlertType.PRICE_DROP, new BigDecimal("300.00"), "Test Alert");
        assertEquals(2L, newAlert.getId());
        assertEquals(testUser, newAlert.getUser());
        assertEquals("MSFT", newAlert.getTicker());
        assertEquals(Alert.AlertType.PRICE_DROP, newAlert.getAlertType());
        assertEquals(new BigDecimal("300.00"), newAlert.getThreshold());
        assertEquals("Test Alert", newAlert.getTitle());
    }
}
