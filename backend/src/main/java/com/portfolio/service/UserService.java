package com.portfolio.service;

import com.portfolio.model.User;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

//Get user by ID

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

// Get user by username

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

// Get user's wallet balance

    @Transactional(readOnly = true)
    public BigDecimal getWalletBalance(Long userId) {
        User user = getUserById(userId);
        return user.getWalletBalance();
    }

//Update wallet balance

    @Transactional
    public BigDecimal updateWalletBalance(Long userId, BigDecimal amount) {
        User user = getUserById(userId);
        BigDecimal newBalance = user.getWalletBalance().add(amount);
        
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        user.setWalletBalance(newBalance);
        userRepository.save(user);
        
        log.info("User {} wallet updated. New balance: ${}", user.getUsername(), newBalance);
        return newBalance;
    }

//Create new user

    @Transactional
    public User createUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setWalletBalance(new BigDecimal("50000.00")); // Default starting balance
        
        User savedUser = userRepository.save(user);
        log.info("Created new user: {}", username);
        return savedUser;
    }

    public void save(User user){
        userRepository.save(user);
    }

//Reset user wallet to default balance

    @Transactional
    public BigDecimal resetWallet(Long userId) {
        User user = getUserById(userId);
        BigDecimal defaultBalance = new BigDecimal("50000.00");
        user.setWalletBalance(defaultBalance);
        userRepository.save(user);
        
        log.info("Reset wallet for user: {}", user.getUsername());
        return defaultBalance;
    }

// AI Trade Password methods

    @Transactional
    public void setAiTradePassword(Long userId, String password) {
        User user = getUserById(userId);
        user.setAiTradePassword(password);
        userRepository.save(user);
        log.info("AI trade password set for user: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public boolean hasAiTradePassword(Long userId) {
        User user = getUserById(userId);
        return user.getAiTradePassword() != null && !user.getAiTradePassword().isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean verifyAiTradePassword(Long userId, String password) {
        User user = getUserById(userId);
        if (user.getAiTradePassword() == null) {
            throw new RuntimeException("AI trade password not set. Please set one first.");
        }
        return user.getAiTradePassword().equals(password);
    }
}
