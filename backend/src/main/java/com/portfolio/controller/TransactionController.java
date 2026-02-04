package com.portfolio.controller;

import com.portfolio.dto.*;
import com.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final PortfolioService portfolioService;

// Buy stock

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<TransactionDTO>> buyStock(@RequestBody BuyRequest request) {
        try {
            TransactionDTO transaction = portfolioService.buyStock(request);
            return ResponseEntity.ok(ApiResponse.success("Stock purchased successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Sell stock

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<TransactionDTO>> sellStock(@RequestBody SellRequest request) {
        try {
            TransactionDTO transaction = portfolioService.sellStock(request);
            return ResponseEntity.ok(ApiResponse.success("Stock sold successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
