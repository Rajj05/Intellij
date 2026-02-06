package com.portfolio.service;

import com.portfolio.dto.AlertDTO;
import com.portfolio.model.Alert;
import com.portfolio.model.PortfolioHolding;
import com.portfolio.model.User;
import com.portfolio.repository.AlertRepository;
import com.portfolio.repository.PortfolioHoldingRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final PortfolioHoldingRepository holdingRepository;

    // Threshold for underperforming alert (e.g., -5%)
    //private static final BigDecimal UNDERPERFORMING_THRESHOLD = new BigDecimal("-5.00");

//Get all alerts for a user

    @Transactional(readOnly = true)
    public List<AlertDTO> getUserAlerts(Long userId) {
        return alertRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

//Create a new alert

    @Transactional
    public AlertDTO createAlert(Long userId, String ticker, Alert.AlertType alertType, BigDecimal threshold) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Alert alert = new Alert();
        alert.setUser(user);
        alert.setTicker(ticker);
        alert.setAlertType(alertType);
        alert.setThreshold(threshold);
        alert.setTitle(ticker + " " + alertType.name().replace("_", " ") + " Alert");

        Alert savedAlert = alertRepository.save(alert);
        log.info("Created alert for user {}: {} - {}", user.getUsername(), ticker, alertType);
        
        return convertToDTO(savedAlert);
    }

// Delete an alert
  
    @Transactional
    public void deleteAlert(Long alertId) {
        alertRepository.deleteById(alertId);
        log.info("Deleted alert: {}", alertId);
    }



    private AlertDTO convertToDTO(Alert alert) {
        return AlertDTO.builder()
                .id(alert.getId())
                .ticker(alert.getTicker())
                .alertType(alert.getAlertType())
                .threshold(alert.getThreshold())
                .build();
    }
}