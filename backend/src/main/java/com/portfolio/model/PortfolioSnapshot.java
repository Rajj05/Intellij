package com.portfolio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_snapshots",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "snapshot_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSnapshot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;
    
    @Column(name = "total_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalValue;
    
    @Column(name = "total_invested", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInvested;
    
    @Column(name = "cash_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal cashBalance;
    
    @Column(name = "daily_gain_loss", precision = 15, scale = 2)
    private BigDecimal dailyGainLoss = BigDecimal.ZERO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
