package com.portfolio.controller;

import com.portfolio.dto.ApiResponse;
import com.portfolio.model.Notification;
import com.portfolio.service.AlertNotificationService;
import com.portfolio.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final AlertNotificationService alertNotificationService;

// Get all unread notifications for a user

    @GetMapping("/{userId}/unread")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Get all notifications for a user

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getAllNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getAllNotifications(userId);
            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Mark notification as read

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Manually trigger alert check (for testing)

    @PostMapping("/test/trigger-alerts")
    public ResponseEntity<ApiResponse<String>> triggerAlertCheck() {
        try {
            alertNotificationService.manuallyTriggerAlertCheck();
            return ResponseEntity.ok(ApiResponse.success("Alert check triggered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
