package com.portfolio.service;

import com.portfolio.model.Alert;
import com.portfolio.model.Notification;
import com.portfolio.model.Stock;
import com.portfolio.model.User;
import com.portfolio.repository.AlertRepository;
import com.portfolio.repository.NotificationRepository;
import com.portfolio.repository.StockRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertNotificationService {

    private final AlertRepository alertRepository;
    private final StockRepository stockRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


     // Check all alerts every 5 minutes and create notifications if conditions are met
     //Runs after stock prices are updated by StockPriceService

    //@Scheduled(fixedRate = 300000, initialDelay = 310000) // 5 minutes, starts after StockPriceService (310 seconds)
    @Transactional
    public void checkAlertsAndCreateNotifications() {
        log.info("Checking alerts and creating notifications...");

        try {
            List<Alert> allAlerts = alertRepository.findAll();

            for (Alert alert : allAlerts) {
                checkAndProcessAlert(alert);
            }

            log.info("Alert checking completed successfully!");
        } catch (Exception e) {
            log.error("Error during alert checking: {}", e.getMessage(), e);
        }
    }

    
     // Check a single alert and create notification if conditions are met
     
    @Transactional
    private void checkAndProcessAlert(Alert alert) {
        try {
            // Get the stock by ticker (ticker is the primary key)
            Stock stock = stockRepository.findById(alert.getTicker()).orElse(null);
            if (stock == null) {
                log.warn("Stock not found for alert {}: {}", alert.getId(), alert.getTicker());
                return;
            }

            // Get the user
            User user = userRepository.findById(alert.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if alert condition is met
            if (shouldTriggerAlert(alert, stock)) {
                createNotificationForAlert(alert, stock, user);
            }
        } catch (Exception e) {
            log.error("Error processing alert {}: {}", alert.getId(), e.getMessage());
        }
    }

    
    // Determine if an alert should trigger based on current stock data
     
    private boolean shouldTriggerAlert(Alert alert, Stock stock) {
        BigDecimal threshold = alert.getThreshold();
        BigDecimal dayChangePercent = stock.getDayChangePercent() != null ? stock.getDayChangePercent() : BigDecimal.ZERO;
        BigDecimal dayChange = stock.getDayChange() != null ? stock.getDayChange() : BigDecimal.ZERO;
        BigDecimal currentPrice = stock.getCurrentPrice();
        BigDecimal previousClose = stock.getPreviousClose();

        if (currentPrice == null || previousClose == null) {
            return false;
        }

        switch (alert.getAlertType()) {
            case DAILY_LOSS:
                // Trigger when daily loss percentage exceeds threshold (negative values)
                return dayChangePercent.compareTo(threshold.negate()) <= 0;

            case DAILY_GAIN:
                // Trigger when daily gain percentage exceeds threshold (positive values)
                return dayChangePercent.compareTo(threshold) >= 0;

            case PRICE_DROP:
                // Trigger when price drops by threshold percentage from previous close
                
                return dayChange.compareTo(threshold.negate()) <= 0;

            case PRICE_RISE:
                // Trigger when price rises by threshold percentage from previous close
                return dayChange.compareTo(threshold) >= 0;

            case UNDERPERFORMING:
                // Trigger when stock loses more than threshold percentage
                return dayChangePercent.compareTo(new BigDecimal("-3")) < 0;

            default:
                return false;
        }
    }

    
    // Create a notification for a triggered alert
     
    @Transactional
    private void createNotificationForAlert(Alert alert, Stock stock, User user) {
        try {
            // Check if a notification for this alert already exists (today)
            // This prevents duplicate notifications for the same alert on the same day
            if (hasNotificationToday(alert, user)) {
                log.debug("Notification already exists for alert {} today", alert.getId());
                return;
            }
            String stockName = stock.getCompanyName() != null ? stock.getCompanyName() : alert.getTicker();
        

            String title = stockName;
            String message = generateAlertMessage(alert, stock);
            Notification.NotificationType notificationType = mapAlertTypeToNotificationType(alert.getAlertType());

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setTicker(alert.getTicker());
            notification.setNotificationType(notificationType);
            notification.setIsRead(false);

            notificationRepository.save(notification);
            log.info("Created notification for user {} - Alert {} triggered for {}", user.getId(), alert.getId(), alert.getTicker());
        } catch (Exception e) {
            log.error("Error creating notification for alert {}: {}", alert.getId(), e.getMessage());
        }
    }

    
    // Check if a notification for this alert already exists today
     
    private boolean hasNotificationToday(Alert alert, User user) {
        // This is a simple check - you might want to add a query method to NotificationRepository
        // for more precise checking based on created date
        return false; // For now, allow multiple notifications per day
    }


     // Generate alert message
     
    private String generateAlertMessage(Alert alert, Stock stock) {
        BigDecimal dayChangePercent = stock.getDayChangePercent() != null ? stock.getDayChangePercent() : BigDecimal.ZERO;
        BigDecimal previousClose = stock.getPreviousClose() != null ? stock.getPreviousClose() : BigDecimal.ZERO;
        BigDecimal currentPrice = stock.getCurrentPrice();
        String stockName = stock.getCompanyName() != null ? stock.getCompanyName() : alert.getTicker();

        switch (alert.getAlertType()) {
            case DAILY_LOSS:
                return String.format("%s has lost %.2f%% today. Current price: $%.2f", 
                    stockName, dayChangePercent, currentPrice);
            case DAILY_GAIN:
                return String.format("%s has gained %.2f%% today. Current price: $%.2f", 
                    stockName, dayChangePercent, currentPrice);
            case PRICE_DROP:
                return String.format("%s has dropped from $%.2f to $%.2f (Threshold: %.2f%%)", 
                    stockName, previousClose, currentPrice, alert.getThreshold().negate());
            case PRICE_RISE:
                return String.format("%s has risen from $%.2f to $%.2f (Threshold: %.2f%%)", 
                    stockName, previousClose, currentPrice, alert.getThreshold());
            case UNDERPERFORMING:
                return String.format("%s is underperforming with a %.2f%% loss today. Current price: $%.2f", 
                    stockName, dayChangePercent, currentPrice);
            default:
                return String.format("%s price movement alert. Current price: $%.2f", stockName, currentPrice);
        }
    }

// Map Alert type to Notification type

    private Notification.NotificationType mapAlertTypeToNotificationType(Alert.AlertType alertType) {
        switch (alertType) {
            case DAILY_LOSS:
                return Notification.NotificationType.DAILY_LOSS;
            case DAILY_GAIN:
                return Notification.NotificationType.DAILY_GAIN;
            case PRICE_DROP:
                return Notification.NotificationType.PRICE_DROP;
            case PRICE_RISE:
                return Notification.NotificationType.PRICE_RISE;
            case UNDERPERFORMING:
                return Notification.NotificationType.UNDERPERFORMING;
            default:
                return Notification.NotificationType.SYSTEM;
        }
    }

    //  Manual trigger for testing - checks all alerts immediately
    // Can be called via controller for on-demand testing

    @Transactional
    public void manuallyTriggerAlertCheck() {
        log.info("Manually triggering alert check for testing...");
        checkAlertsAndCreateNotifications();
    }
}
