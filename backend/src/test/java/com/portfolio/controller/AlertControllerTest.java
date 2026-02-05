package com.portfolio.controller;

import com.portfolio.dto.AlertDTO;
import com.portfolio.dto.ApiResponse;
import com.portfolio.model.Alert;
import com.portfolio.service.AlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
@DisplayName("Alert Controller Tests")
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    @Test
    @DisplayName("Should get all alerts for a valid user")
    void testGetUserAlerts() throws Exception {
        AlertDTO alert1 = AlertDTO.builder()
                .id(1L)
                .ticker("AAPL")
                .alertType(Alert.AlertType.PRICE_RISE)
                .threshold(new BigDecimal("150.50"))
                .build();

        AlertDTO alert2 = AlertDTO.builder()
                .id(2L)
                .ticker("GOOGL")
                .alertType(Alert.AlertType.PRICE_DROP)
                .threshold(new BigDecimal("130.00"))
                .build();

        when(alertService.getUserAlerts(1L)).thenReturn(Arrays.asList(alert1, alert2));

        mockMvc.perform(get("/api/alerts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].ticker").value("AAPL"))
                .andExpect(jsonPath("$.data[0].alertType").value("PRICE_RISE"))
                .andExpect(jsonPath("$.data[1].ticker").value("GOOGL"))
                .andExpect(jsonPath("$.data[1].alertType").value("PRICE_DROP"));

        verify(alertService, times(1)).getUserAlerts(1L);
    }

    @Test
    @DisplayName("Should return error when getting alerts for non-existent user")
    void testGetUserAlertsUserNotFound() throws Exception {
        when(alertService.getUserAlerts(999L))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/alerts/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @DisplayName("Should create a new alert successfully")
    void testCreateAlert() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", 1L);
        requestBody.put("ticker", "AAPL");
        requestBody.put("alertType", "PRICE_RISE");
        requestBody.put("threshold", "150.50");

        AlertDTO createdAlert = AlertDTO.builder()
                .id(1L)
                .ticker("AAPL")
                .alertType(Alert.AlertType.PRICE_RISE)
                .threshold(new BigDecimal("150.50"))
                .build();

        when(alertService.createAlert(1L, "AAPL", Alert.AlertType.PRICE_RISE, new BigDecimal("150.50")))
                .thenReturn(createdAlert);

        mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"ticker\":\"AAPL\",\"alertType\":\"PRICE_RISE\",\"threshold\":\"150.50\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Alert created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.ticker").value("AAPL"))
                .andExpect(jsonPath("$.data.alertType").value("PRICE_RISE"))
                .andExpect(jsonPath("$.data.threshold").value("150.5"));
    }


    @Test
    @DisplayName("Should create alert with DAILY_GAIN type")
    void testCreateDailyGainAlert() throws Exception {
        AlertDTO createdAlert = AlertDTO.builder()
                .id(2L)
                .ticker("MSFT")
                .alertType(Alert.AlertType.DAILY_GAIN)
                .threshold(new BigDecimal("5.0"))
                .build();

        when(alertService.createAlert(1L, "MSFT", Alert.AlertType.DAILY_GAIN, new BigDecimal("5.0")))
                .thenReturn(createdAlert);

        mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"ticker\":\"MSFT\",\"alertType\":\"DAILY_GAIN\",\"threshold\":\"5.0\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.alertType").value("DAILY_GAIN"));
    }


    @Test
    @DisplayName("Should return empty list when user has no alerts")
    void testGetUserAlertsEmpty() throws Exception {
        when(alertService.getUserAlerts(2L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/alerts/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    
}
