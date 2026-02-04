package com.portfolio.controller;

import com.portfolio.dto.*;
import com.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PortfolioController {

    private final PortfolioService portfolioService;

// Get complete portfolio summary for a user

    @GetMapping("/{userId}/summary")
    public ResponseEntity<ApiResponse<PortfolioSummaryDTO>> getPortfolioSummary(@PathVariable Long userId) {
        try {
            PortfolioSummaryDTO summary = portfolioService.getPortfolioSummary(userId);
            return ResponseEntity.ok(ApiResponse.success(summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Get user's holdings

    @GetMapping("/{userId}/holdings")
    public ResponseEntity<ApiResponse<List<HoldingDTO>>> getUserHoldings(@PathVariable Long userId) {
        try {
            List<HoldingDTO> holdings = portfolioService.getUserHoldings(userId);
            return ResponseEntity.ok(ApiResponse.success(holdings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Get user's transaction history

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionHistory(@PathVariable Long userId) {
        try {
            List<TransactionDTO> transactions = portfolioService.getTransactionHistory(userId);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
