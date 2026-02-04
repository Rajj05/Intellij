package com.portfolio.service;

import com.portfolio.model.Notification;
import com.portfolio.model.User;
import com.portfolio.repository.NotificationRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Query notifications by userId to avoid lazy loading issues
        return notificationRepository.findUnreadByUserId(userId);
    }

// Get all notifications for a user

    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications(Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Query notifications by userId to avoid lazy loading issues
        return notificationRepository.findAllByUserId(userId);
    }

// Mark notification as read

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
        log.info("Marked notification {} as read", notificationId);
    }

// Create a notification

    @Transactional
    public Notification createNotification(Long userId, String title, String message, String ticker, Notification.NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTicker(ticker);
        notification.setNotificationType(type);
        notification.setIsRead(false);
        
        Notification saved = notificationRepository.save(notification);
        log.info("Created notification for user {}: {}", user.getUsername(), title);
        return saved;
    }
}
