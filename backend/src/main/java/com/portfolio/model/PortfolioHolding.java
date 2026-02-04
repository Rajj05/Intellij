package com.portfolio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_holdings", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ticker"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioHolding {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker", nullable = false)
    private Stock stock;
    
    @Column(nullable = false, precision = 15, scale = 6)
    private BigDecimal quantity;
    
    @Column(name = "average_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal averageCost;
    
    @Column(name = "total_invested", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInvested;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper method to get current value
    public BigDecimal getCurrentValue() {
        if (stock != null && stock.getCurrentPrice() != null) {
            return quantity.multiply(stock.getCurrentPrice());
        }
        return BigDecimal.ZERO;
    }
    
    // Helper method to get profit/loss
    public BigDecimal getProfitLoss() {
        return getCurrentValue().subtract(totalInvested);
    }
    
    // Helper method to get profit/loss percentage
    public BigDecimal getProfitLossPercent() {
        if (totalInvested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfitLoss()
                .divide(totalInvested, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}
