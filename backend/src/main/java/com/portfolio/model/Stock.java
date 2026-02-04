package com.portfolio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    
    @Id
    @Column(length = 10)
    private String ticker;
    
    @Column(name = "company_name", length = 200)
    private String companyName;
    
    @Column(name = "current_price", precision = 15, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(name = "previous_close", precision = 15, scale = 2)
    private BigDecimal previousClose;
    
    @Column(name = "day_change", precision = 10, scale = 2)
    private BigDecimal dayChange;
    
    @Column(name = "day_change_percent", precision = 5, scale = 2)
    private BigDecimal dayChangePercent;
    
    @Column(name = "day_high", precision = 15, scale = 2)
    private BigDecimal dayHigh;
    
    @Column(name = "day_low", precision = 15, scale = 2)
    private BigDecimal dayLow;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
