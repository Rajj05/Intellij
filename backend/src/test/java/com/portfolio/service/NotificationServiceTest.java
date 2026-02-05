package com.portfolio.service;

import com.portfolio.model.Notification;
import com.portfolio.model.User;
import com.portfolio.repository.NotificationRepository;
import com.portfolio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Service Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(testUser);
        testNotification.setTitle("Price Alert");
        testNotification.setMessage("AAPL price rose to $150");
        testNotification.setTicker("AAPL");
        testNotification.setIsRead(false);
    }

    @Test
    @DisplayName("Should get unread notifications for valid user")
    void testGetUnreadNotifications() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationRepository.findUnreadByUserId(1L)).thenReturn(Arrays.asList(testNotification));

        List<Notification> notifications = notificationService.getUnreadNotifications(1L);

        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        assertEquals("Price Alert", notifications.get(0).getTitle());
        assertFalse(notifications.get(0).getIsRead());
        verify(userRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).findUnreadByUserId(1L);
    }

    @Test
    @DisplayName("Should return empty list when user has no unread notifications")
    void testGetUnreadNotificationsEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationRepository.findUnreadByUserId(1L)).thenReturn(Arrays.asList());

        List<Notification> notifications = notificationService.getUnreadNotifications(1L);

        assertNotNull(notifications);
        assertEquals(0, notifications.size());
    }

    @Test
    @DisplayName("Should get all notifications for valid user")
    void testGetAllNotifications() {
        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setUser(testUser);
        notification2.setTitle("System Alert");
        notification2.setMessage("Portfolio updated");
        notification2.setIsRead(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationRepository.findAllByUserId(1L)).thenReturn(Arrays.asList(testNotification, notification2));

        List<Notification> notifications = notificationService.getAllNotifications(1L);

        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertEquals("Price Alert", notifications.get(0).getTitle());
        assertEquals("System Alert", notifications.get(1).getTitle());
        verify(userRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).findAllByUserId(1L);
    }

}
