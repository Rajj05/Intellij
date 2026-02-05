package com.portfolio.service;

import com.portfolio.dto.AlertDTO;
import com.portfolio.model.Alert;
import com.portfolio.model.User;
import com.portfolio.repository.AlertRepository;
import com.portfolio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Alert Service Tests")
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AlertService alertService;

    private User testUser;
    private Alert testAlert;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testAlert = new Alert();
        testAlert.setId(1L);
        testAlert.setUser(testUser);
        testAlert.setTicker("AAPL");
        testAlert.setAlertType(Alert.AlertType.PRICE_RISE);
        testAlert.setThreshold(new BigDecimal("150.50"));
    }

    @Test
    @DisplayName("Should get all alerts for a user")
    void testGetUserAlerts() {
        Alert alert2 = new Alert();
        alert2.setId(2L);
        alert2.setUser(testUser);
        alert2.setTicker("GOOGL");
        alert2.setAlertType(Alert.AlertType.PRICE_DROP);
        alert2.setThreshold(new BigDecimal("130.00"));

        when(alertRepository.findByUserId(1L)).thenReturn(Arrays.asList(testAlert, alert2));

        List<AlertDTO> alerts = alertService.getUserAlerts(1L);

        assertNotNull(alerts);
        assertEquals(2, alerts.size());
        assertEquals("AAPL", alerts.get(0).getTicker());
        assertEquals("GOOGL", alerts.get(1).getTicker());
        verify(alertRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should return empty list when user has no alerts")
    void testGetUserAlertsEmpty() {
        when(alertRepository.findByUserId(2L)).thenReturn(Arrays.asList());

        List<AlertDTO> alerts = alertService.getUserAlerts(2L);

        assertNotNull(alerts);
        assertEquals(0, alerts.size());
        verify(alertRepository, times(1)).findByUserId(2L);
    }

    @Test
    @DisplayName("Should create a new alert successfully")
    void testCreateAlert() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);

        AlertDTO createdAlert = alertService.createAlert(1L, "AAPL", Alert.AlertType.PRICE_RISE, new BigDecimal("150.50"));

        assertNotNull(createdAlert);
        assertEquals("AAPL", createdAlert.getTicker());
        assertEquals(Alert.AlertType.PRICE_RISE, createdAlert.getAlertType());
        assertEquals(new BigDecimal("150.50"), createdAlert.getThreshold());
        verify(userRepository, times(1)).findById(1L);
        verify(alertRepository, times(1)).save(any(Alert.class));
    }

}
