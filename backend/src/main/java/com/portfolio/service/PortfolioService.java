package com.portfolio.service;

import com.portfolio.dto.*;
import com.portfolio.model.*;
import com.portfolio.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final PortfolioHoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;
    private final AlertService alertService;

// Get complete portfolio summary for a user

    @Transactional(readOnly = true)
    public PortfolioSummaryDTO getPortfolioSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PortfolioHolding> holdings = holdingRepository.findByUserIdWithStock(userId);
        
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;
        
        List<HoldingDTO> holdingDTOs = holdings.stream().map(h -> {
            Stock stock = h.getStock();
            BigDecimal currentValue = h.getQuantity().multiply(stock.getCurrentPrice());
            BigDecimal profitLoss = currentValue.subtract(h.getTotalInvested());
            BigDecimal profitLossPercent = h.getTotalInvested().compareTo(BigDecimal.ZERO) != 0
                    ? profitLoss.divide(h.getTotalInvested(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                    : BigDecimal.ZERO;

            return HoldingDTO.builder()
                    .id(h.getId())
                    .ticker(stock.getTicker())
                    .companyName(stock.getCompanyName())
                    .quantity(h.getQuantity())
                    .averageCost(h.getAverageCost())
                    .totalInvested(h.getTotalInvested())
                    .currentPrice(stock.getCurrentPrice())
                    .currentValue(currentValue)
                    .profitLoss(profitLoss)
                    .profitLossPercent(profitLossPercent)
                    .dayChange(stock.getDayChange())
                    .dayChangePercent(stock.getDayChangePercent())
                    .build();
        }).collect(Collectors.toList());

        // Calculate totals
        for (HoldingDTO h : holdingDTOs) {
            totalInvested = totalInvested.add(h.getTotalInvested());
            totalCurrentValue = totalCurrentValue.add(h.getCurrentValue());
        }

        BigDecimal totalGainLoss = totalCurrentValue.subtract(totalInvested);
        BigDecimal totalGainLossPercent = totalInvested.compareTo(BigDecimal.ZERO) != 0
                ? totalGainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;

        // Calculate daily gain/loss based on day change of holdings
        BigDecimal dailyGainLoss = holdings.stream()
                .map(h -> h.getQuantity().multiply(h.getStock().getDayChange() != null ? h.getStock().getDayChange() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousValue = totalCurrentValue.subtract(dailyGainLoss);
        BigDecimal dailyGainLossPercent = previousValue.compareTo(BigDecimal.ZERO) != 0
                ? dailyGainLoss.divide(previousValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;

        return PortfolioSummaryDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .walletBalance(user.getWalletBalance())
                .totalInvested(totalInvested)
                .totalCurrentValue(totalCurrentValue)
                .totalBalance(user.getWalletBalance().add(totalCurrentValue))
                .totalGainLoss(totalGainLoss)
                .totalGainLossPercent(totalGainLossPercent)
                .dailyGainLoss(dailyGainLoss)
                .dailyGainLossPercent(dailyGainLossPercent)
                .totalAssets(holdings.size())
                .holdings(holdingDTOs)
                .build();
    }

// Get user's holdings

    @Transactional(readOnly = true)
    public List<HoldingDTO> getUserHoldings(Long userId) {
        return holdingRepository.findByUserIdWithStock(userId).stream()
                .map(this::convertToHoldingDTO)
                .collect(Collectors.toList());
    }

// Buy stock

    @Transactional
    public TransactionDTO buyStock(BuyRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stock stock = stockRepository.findById(request.getTicker())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + request.getTicker()));

        BigDecimal totalCost = stock.getCurrentPrice().multiply(request.getQuantity());

        // Check if user has enough balance
        if (user.getWalletBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient funds. Required: $" + totalCost + ", Available: $" + user.getWalletBalance());
        }

        // Deduct from wallet
        user.setWalletBalance(user.getWalletBalance().subtract(totalCost));
        userRepository.save(user);

        // Update or create holding
        PortfolioHolding holding = holdingRepository
                .findByUserIdAndTicker(request.getUserId(), request.getTicker())
                .orElse(null);

        if (holding == null) {
            // Create new holding
            holding = new PortfolioHolding();
            holding.setUser(user);
            holding.setStock(stock);
            holding.setQuantity(request.getQuantity());
            holding.setAverageCost(stock.getCurrentPrice());
            holding.setTotalInvested(totalCost);
        } else {
            // Update existing holding with weighted average cost
            BigDecimal totalQty = holding.getQuantity().add(request.getQuantity());
            BigDecimal totalInvested = holding.getTotalInvested().add(totalCost);
            BigDecimal avgCost = totalInvested.divide(totalQty, 2, RoundingMode.HALF_UP);

            holding.setQuantity(totalQty);
            holding.setAverageCost(avgCost);
            holding.setTotalInvested(totalInvested);
        }
        holdingRepository.save(holding);

        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTicker(request.getTicker());
        transaction.setTransactionType(Transaction.TransactionType.BUY);
        transaction.setQuantity(request.getQuantity());
        transaction.setPricePerUnit(stock.getCurrentPrice());
        transaction.setTotalAmount(totalCost);
        transaction.setWalletBalanceAfter(user.getWalletBalance());
        transactionRepository.save(transaction);

        log.info("User {} bought {} shares of {} at ${}", 
                user.getUsername(), request.getQuantity(), request.getTicker(), stock.getCurrentPrice());

        return convertToTransactionDTO(transaction, stock.getCompanyName());
    }

//Sell stock

    @Transactional
    public TransactionDTO sellStock(SellRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stock stock = stockRepository.findById(request.getTicker())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + request.getTicker()));

        PortfolioHolding holding = holdingRepository
                .findByUserIdAndTicker(request.getUserId(), request.getTicker())
                .orElseThrow(() -> new RuntimeException("You don't own any " + request.getTicker() + " shares"));

        // BigDecimal quantityToSell = request.isSellAll() ? holding.getQuantity() : request.getQuantity();
        BigDecimal quantityToSell = request.getQuantity();

        // Validate quantity
        if (quantityToSell.compareTo(holding.getQuantity()) > 0) {
            throw new RuntimeException("Insufficient shares. You own: " + holding.getQuantity() + " shares");
        }

        BigDecimal totalSaleAmount = stock.getCurrentPrice().multiply(quantityToSell);

        // Add to wallet
        user.setWalletBalance(user.getWalletBalance().add(totalSaleAmount));
        userRepository.save(user);

        // Update or delete holding
        BigDecimal remainingQty = holding.getQuantity().subtract(quantityToSell);
        if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) {
            holdingRepository.delete(holding);
        } else {
            BigDecimal soldPortion = quantityToSell.divide(holding.getQuantity(), 4, RoundingMode.HALF_UP);
            BigDecimal investedToRemove = holding.getTotalInvested().multiply(soldPortion);
            
            holding.setQuantity(remainingQty);
            holding.setTotalInvested(holding.getTotalInvested().subtract(investedToRemove));
            holdingRepository.save(holding);
        }

        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTicker(request.getTicker());
        transaction.setTransactionType(Transaction.TransactionType.SELL);
        transaction.setQuantity(quantityToSell);
        transaction.setPricePerUnit(stock.getCurrentPrice());
        transaction.setTotalAmount(totalSaleAmount);
        transaction.setWalletBalanceAfter(user.getWalletBalance());
        transactionRepository.save(transaction);

        log.info("User {} sold {} shares of {} at ${}", 
                user.getUsername(), quantityToSell, request.getTicker(), stock.getCurrentPrice());

        return convertToTransactionDTO(transaction, stock.getCompanyName());
    }

//Get user's transaction history

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionHistory(Long userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId).stream()
                .map(t -> {
                    Stock stock = stockRepository.findById(t.getTicker()).orElse(null);
                    String companyName = stock != null ? stock.getCompanyName() : t.getTicker();
                    return convertToTransactionDTO(t, companyName);
                })
                .collect(Collectors.toList());
    }

    // Helper methods
    private HoldingDTO convertToHoldingDTO(PortfolioHolding holding) {
        Stock stock = holding.getStock();
        BigDecimal currentValue = holding.getQuantity().multiply(stock.getCurrentPrice());
        BigDecimal profitLoss = currentValue.subtract(holding.getTotalInvested());
        BigDecimal profitLossPercent = holding.getTotalInvested().compareTo(BigDecimal.ZERO) != 0
                ? profitLoss.divide(holding.getTotalInvested(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;

        return HoldingDTO.builder()
                .id(holding.getId())
                .ticker(stock.getTicker())
                .companyName(stock.getCompanyName())
                .quantity(holding.getQuantity())
                .averageCost(holding.getAverageCost())
                .totalInvested(holding.getTotalInvested())
                .currentPrice(stock.getCurrentPrice())
                .currentValue(currentValue)
                .profitLoss(profitLoss)
                .profitLossPercent(profitLossPercent)
                .dayChange(stock.getDayChange())
                .dayChangePercent(stock.getDayChangePercent())
                .build();
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction, String companyName) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .ticker(transaction.getTicker())
                .companyName(companyName)
                .transactionType(transaction.getTransactionType())
                .quantity(transaction.getQuantity())
                .pricePerUnit(transaction.getPricePerUnit())
                .totalAmount(transaction.getTotalAmount())
                .walletBalanceAfter(transaction.getWalletBalanceAfter())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}
