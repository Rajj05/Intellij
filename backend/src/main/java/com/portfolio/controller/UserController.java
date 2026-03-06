package com.portfolio.controller;

import com.portfolio.dto.ApiResponse;
import com.portfolio.model.User;
import com.portfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

//Get user info

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("walletBalance", user.getWalletBalance());
            userData.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(ApiResponse.success(userData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Get user wallet balance

    @GetMapping("/{userId}/wallet")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getWalletBalance(@PathVariable Long userId) {
        try {
            BigDecimal balance = userService.getWalletBalance(userId);
            Map<String, BigDecimal> result = new HashMap<>();
            result.put("walletBalance", balance);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    //Reset user wallet to default balance

    @PostMapping("/{userId}/wallet/reset")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> resetWallet(@PathVariable Long userId) {
        try {
            BigDecimal newBalance = userService.resetWallet(userId);
            Map<String, BigDecimal> result = new HashMap<>();
            result.put("walletBalance", newBalance);
            
            return ResponseEntity.ok(ApiResponse.success("Wallet reset successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{userId}/wallet/{amount}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateWalletBalance(@PathVariable Long userId, @PathVariable BigDecimal amount) {
        try {
            // Get the user from the database
            User user = userService.getUserById(userId);
            
            // Update the wallet balance
            BigDecimal updatedWalletBalance = user.getWalletBalance().add(amount);
            user.setWalletBalance(updatedWalletBalance);
            
            // Save the updated user data
            userService.save(user);
            
            // Create a response map with the updated user data
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("walletBalance", updatedWalletBalance);  // return updated balance
            userData.put("createdAt", user.getCreatedAt());

            // Return the updated user data
            return ResponseEntity.ok(ApiResponse.success(userData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Create new user (register)

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerUser(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            
            if (username == null || email == null || password == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Username, email, and password are required"));
            }
            
            User user = userService.createUser(username, email, password);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("walletBalance", user.getWalletBalance());
            
            return ResponseEntity.ok(ApiResponse.success("User created successfully", userData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Login

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginUser(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Username and password are required"));
            }
            
            User user = userService.login(username, password);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("walletBalance", user.getWalletBalance());
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", userData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
