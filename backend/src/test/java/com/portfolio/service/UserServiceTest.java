package com.portfolio.service;

import com.portfolio.model.User;
import com.portfolio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setWalletBalance(new BigDecimal("50000.00"));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User user = userService.getUserById(1L);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should update wallet balance by adding amount")
    void testUpdateWalletBalanceAdd() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        BigDecimal newBalance = userService.updateWalletBalance(1L, new BigDecimal("5000.00"));

        assertEquals(new BigDecimal("55000.00"), newBalance);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    
    @Test
    @DisplayName("Should create new user successfully")
    void testCreateUser() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setWalletBalance(new BigDecimal("50000.00"));

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User created = userService.createUser("newuser", "new@example.com");

        assertNotNull(created);
        assertEquals("newuser", created.getUsername());
        assertEquals("new@example.com", created.getEmail());
        assertEquals(new BigDecimal("50000.00"), created.getWalletBalance());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreateUserDuplicateEmail() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            userService.createUser("newuser", "test@example.com")
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should reset wallet to default balance")
    void testResetWallet() {
        testUser.setWalletBalance(new BigDecimal("25000.00"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        BigDecimal resetBalance = userService.resetWallet(1L);

        assertEquals(new BigDecimal("50000.00"), resetBalance);
        verify(userRepository, times(1)).save(testUser);
    }

}
