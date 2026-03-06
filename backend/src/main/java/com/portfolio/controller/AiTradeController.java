package com.portfolio.controller;

import com.portfolio.dto.*;
import com.portfolio.model.User;
import com.portfolio.service.PortfolioService;
import com.portfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiTradeController {

    private final UserService userService;
    private final PortfolioService portfolioService;

    // Set or update AI trade password
    @PostMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Map<String, String>>> setAiPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Password cannot be empty"));
            }
            userService.setAiTradePassword(userId, password);
            return ResponseEntity.ok(ApiResponse.success("AI trade password set successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Check if user has AI password set
    @GetMapping("/{userId}/password/status")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> hasAiPassword(@PathVariable Long userId) {
        try {
            boolean hasPassword = userService.hasAiTradePassword(userId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("hasPassword", hasPassword);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Verify AI trade password
    @PostMapping("/{userId}/password/verify")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> verifyAiPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            boolean valid = userService.verifyAiTradePassword(userId, password);
            Map<String, Boolean> result = new HashMap<>();
            result.put("valid", valid);
            if (!valid) {
                return ResponseEntity.ok(ApiResponse.error("Invalid AI trade password"));
            }
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Execute AI trade (buy)
    @PostMapping("/trade/buy")
    public ResponseEntity<ApiResponse<TransactionDTO>> aiBuyStock(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String password = (String) request.get("password");
            String ticker = ((String) request.get("ticker")).toUpperCase();
            BigDecimal quantity = new BigDecimal(request.get("quantity").toString());

            if (!userService.verifyAiTradePassword(userId, password)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid AI trade password"));
            }

            BuyRequest buyRequest = new BuyRequest();
            buyRequest.setUserId(userId);
            buyRequest.setTicker(ticker);
            buyRequest.setQuantity(quantity);
            buyRequest.setAiInitiated(true);

            TransactionDTO transaction = portfolioService.buyStock(buyRequest);
            return ResponseEntity.ok(ApiResponse.success("AI trade executed: Bought " + quantity + " shares of " + ticker, transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Execute AI trade (sell)
    @PostMapping("/trade/sell")
    public ResponseEntity<ApiResponse<TransactionDTO>> aiSellStock(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String password = (String) request.get("password");
            String ticker = ((String) request.get("ticker")).toUpperCase();
            BigDecimal quantity = new BigDecimal(request.get("quantity").toString());

            if (!userService.verifyAiTradePassword(userId, password)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid AI trade password"));
            }

            SellRequest sellRequest = new SellRequest();
            sellRequest.setUserId(userId);
            sellRequest.setTicker(ticker);
            sellRequest.setQuantity(quantity);
            sellRequest.setAiInitiated(true);

            TransactionDTO transaction = portfolioService.sellStock(sellRequest);
            return ResponseEntity.ok(ApiResponse.success("AI trade executed: Sold " + quantity + " shares of " + ticker, transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
