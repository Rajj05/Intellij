package com.portfolio.dto;

import com.portfolio.model.Alert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AlertDTO Tests")
class AlertDTOTest {

    @Test
    @DisplayName("Should create AlertDTO with builder")
    void testAlertDTOBuilder() {
        AlertDTO alert = AlertDTO.builder()
                .id(1L)
                .ticker("AAPL")
                .alertType(Alert.AlertType.PRICE_RISE)
                .threshold(new BigDecimal("150.50"))
                .build();

        assertEquals(1L, alert.getId());
        assertEquals("AAPL", alert.getTicker());
        assertEquals(Alert.AlertType.PRICE_RISE, alert.getAlertType());
        assertEquals(new BigDecimal("150.50"), alert.getThreshold());
    }

    @Test
    @DisplayName("Should set and get all AlertDTO properties")
    void testAlertDTOProperties() {
        AlertDTO alert = new AlertDTO();
        alert.setId(2L);
        alert.setTicker("GOOGL");
        alert.setAlertType(Alert.AlertType.PRICE_DROP);
        alert.setThreshold(new BigDecimal("130.00"));

        assertEquals(2L, alert.getId());
        assertEquals("GOOGL", alert.getTicker());
        assertEquals(Alert.AlertType.PRICE_DROP, alert.getAlertType());
        assertEquals(new BigDecimal("130.00"), alert.getThreshold());
    }

    @Test
    @DisplayName("Should handle null ticker")
    void testAlertDTONullTicker() {
        AlertDTO alert = new AlertDTO();
        alert.setTicker(null);

        assertNull(alert.getTicker());
    }

    @Test
    @DisplayName("Should handle DAILY_GAIN alert type")
    void testAlertDTODailyGainType() {
        AlertDTO alert = AlertDTO.builder()
                .id(3L)
                .ticker("MSFT")
                .alertType(Alert.AlertType.DAILY_GAIN)
                .threshold(new BigDecimal("5.0"))
                .build();

        assertEquals(Alert.AlertType.DAILY_GAIN, alert.getAlertType());
    }

}
