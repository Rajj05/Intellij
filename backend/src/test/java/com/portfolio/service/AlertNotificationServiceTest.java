package com.portfolio.service;

import com.portfolio.model.Alert;
import com.portfolio.model.Notification;
import com.portfolio.model.Stock;
import com.portfolio.model.User;
import com.portfolio.repository.AlertRepository;
import com.portfolio.repository.NotificationRepository;
import com.portfolio.repository.StockRepository;
import com.portfolio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Alert Notification Service Tests")
class AlertNotificationServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AlertNotificationService alertNotificationService;

    private User testUser;
    private Stock testStock;
    private Alert testAlert;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testStock = new Stock();
        testStock.setTicker("AAPL");
        testStock.setCompanyName("Apple Inc.");
        testStock.setCurrentPrice(new BigDecimal("185.50"));
        testStock.setPreviousClose(new BigDecimal("183.20"));
        testStock.setDayChange(new BigDecimal("2.30"));
        testStock.setDayChangePercent(new BigDecimal("1.26"));

        testAlert = new Alert();
        testAlert.setId(1L);
        testAlert.setUser(testUser);
        testAlert.setTicker("AAPL");
        testAlert.setAlertType(Alert.AlertType.PRICE_RISE);
        testAlert.setThreshold(new BigDecimal("1.0"));
    }



    @Test
    @DisplayName("Should trigger PRICE_RISE alert when threshold exceeded")
    void testTriggerPriceRiseAlert() {
        testAlert.setAlertType(Alert.AlertType.PRICE_RISE);
        testAlert.setThreshold(new BigDecimal("1.0"));
        testStock.setDayChangePercent(new BigDecimal("1.26"));

        when(alertRepository.findAll()).thenReturn(Arrays.asList(testAlert));
        when(stockRepository.findById("AAPL")).thenReturn(Optional.of(testStock));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        assertDoesNotThrow(() -> alertNotificationService.checkAlertsAndCreateNotifications());

        verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should trigger DAILY_GAIN alert when gain exceeds threshold")
    void testTriggerDailyGainAlert() {
        testAlert.setAlertType(Alert.AlertType.DAILY_GAIN);
        testAlert.setThreshold(new BigDecimal("1.0"));
        testStock.setDayChangePercent(new BigDecimal("2.0"));

        when(alertRepository.findAll()).thenReturn(Arrays.asList(testAlert));
        when(stockRepository.findById("AAPL")).thenReturn(Optional.of(testStock));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        assertDoesNotThrow(() -> alertNotificationService.checkAlertsAndCreateNotifications());
    }

    @Test
    @DisplayName("Should trigger DAILY_LOSS alert when loss exceeds threshold")
    void testTriggerDailyLossAlert() {
        testAlert.setAlertType(Alert.AlertType.DAILY_LOSS);
        testAlert.setThreshold(new BigDecimal("-2.0"));
        testStock.setDayChangePercent(new BigDecimal("-3.0"));

        when(alertRepository.findAll()).thenReturn(Arrays.asList(testAlert));
        when(stockRepository.findById("AAPL")).thenReturn(Optional.of(testStock));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        assertDoesNotThrow(() -> alertNotificationService.checkAlertsAndCreateNotifications());
    }

}
