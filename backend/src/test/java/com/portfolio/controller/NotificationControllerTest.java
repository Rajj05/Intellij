package com.portfolio.controller;

import com.portfolio.dto.ApiResponse;
import com.portfolio.model.Notification;
import com.portfolio.service.AlertNotificationService;
import com.portfolio.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@DisplayName("Notification Controller Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private AlertNotificationService alertNotificationService;

    @Test
    @DisplayName("Should get all unread notifications for valid user")
    void testGetUnreadNotifications() throws Exception {
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setTitle("Price Alert");
        notification1.setMessage("AAPL price rose to $150");
        notification1.setIsRead(false);

        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setTitle("System Alert");
        notification2.setMessage("Portfolio updated");
        notification2.setIsRead(false);

        when(notificationService.getUnreadNotifications(1L))
                .thenReturn(Arrays.asList(notification1, notification2));

        mockMvc.perform(get("/api/notifications/1/unread")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("Price Alert"))
                .andExpect(jsonPath("$.data[1].title").value("System Alert"));

        verify(notificationService, times(1)).getUnreadNotifications(1L);
    }

   
    @Test
    @DisplayName("Should mark notification as read successfully")
    void testMarkAsRead() throws Exception {
        doNothing().when(notificationService).markAsRead(1L);

        mockMvc.perform(put("/api/notifications/1/read")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(notificationService, times(1)).markAsRead(1L);
    }

   
}
