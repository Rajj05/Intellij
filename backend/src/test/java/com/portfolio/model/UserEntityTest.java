package com.portfolio.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserEntityTest {

    @Test
    @DisplayName("Should create user with default wallet balance")
    void testUserDefaultWalletBalance() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        assertEquals(new BigDecimal("50000.00"), user.getWalletBalance());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should set and get all user properties")
    void testUserProperties() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setEmail("john@portfolio.com");
        user.setWalletBalance(new BigDecimal("75000.00"));
        
        assertEquals(1L, user.getId());
        assertEquals("john_doe", user.getUsername());
        assertEquals("john@portfolio.com", user.getEmail());
        assertEquals(new BigDecimal("75000.00"), user.getWalletBalance());
    }
}
