package com.portfolio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    public enum TransactionType {
        BUY, SELL
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 10)
    private String ticker;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;
    
    @Column(nullable = false, precision = 15, scale = 6)
    private BigDecimal quantity;
    
    @Column(name = "price_per_unit", nullable = false, precision = 15, scale = 2)
    private BigDecimal pricePerUnit;
    
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "wallet_balance_after", nullable = false, precision = 15, scale = 2)
    private BigDecimal walletBalanceAfter;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }
}
