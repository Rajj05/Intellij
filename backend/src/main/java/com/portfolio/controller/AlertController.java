package com.portfolio.controller;

import com.portfolio.dto.AlertDTO;
import com.portfolio.dto.ApiResponse;
import com.portfolio.model.Alert;
import com.portfolio.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;


    //  Get all alerts for a user


    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<AlertDTO>>> getUserAlerts(@PathVariable Long userId) {
        try {
            List<AlertDTO> alerts = alertService.getUserAlerts(userId);
            return ResponseEntity.ok(ApiResponse.success(alerts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

//  Create a new alert

    @PostMapping
    public ResponseEntity<ApiResponse<AlertDTO>> createAlert(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            String ticker = (String) request.get("ticker");
            String alertTypeStr = (String) request.get("alertType");
            BigDecimal threshold = new BigDecimal(request.get("threshold").toString());
            
            Alert.AlertType alertType = Alert.AlertType.valueOf(alertTypeStr);
            AlertDTO alert = alertService.createAlert(userId, ticker, alertType, threshold);
            return ResponseEntity.ok(ApiResponse.success("Alert created successfully", alert));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Delete an alert

    @DeleteMapping("/{alertId}")
    public ResponseEntity<ApiResponse<String>> deleteAlert(@PathVariable Long alertId) {
        try {
            alertService.deleteAlert(alertId);
            return ResponseEntity.ok(ApiResponse.success("Alert deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
