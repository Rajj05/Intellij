package com.portfolio.controller;

import com.portfolio.dto.ApiResponse;
import com.portfolio.model.User;
import com.portfolio.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Should get user info successfully")
    void testGetUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setWalletBalance(new BigDecimal("50000.00"));
        user.setCreatedAt(LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.walletBalance").value("50000.0"));

        verify(userService, times(1)).getUserById(1L);
    }

   
    @Test
    @DisplayName("Should get user wallet balance successfully")
    void testGetWalletBalance() throws Exception {
        when(userService.getWalletBalance(1L)).thenReturn(new BigDecimal("50000.00"));

        mockMvc.perform(get("/api/user/1/wallet")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.walletBalance").value("50000.0"));

        verify(userService, times(1)).getWalletBalance(1L);
    }

    @Test
    @DisplayName("Should return error when creating user with duplicate email")
    void testCreateUserDuplicateEmail() throws Exception {
        when(userService.createUser("newuser", "test@example.com"))
                .thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

}
