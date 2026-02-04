package com.portfolio.repository;

import com.portfolio.model.PortfolioHolding;
import com.portfolio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding, Long> {
    
    // Find all holdings for a user
    @Query("SELECT ph FROM PortfolioHolding ph JOIN FETCH ph.stock WHERE ph.user.id = :userId")
    List<PortfolioHolding> findByUserIdWithStock(@Param("userId") Long userId);
    
    // Find specific holding by user and ticker
    @Query("SELECT ph FROM PortfolioHolding ph JOIN FETCH ph.stock WHERE ph.user.id = :userId AND ph.stock.ticker = :ticker")
    Optional<PortfolioHolding> findByUserIdAndTicker(@Param("userId") Long userId, @Param("ticker") String ticker);
    
    // Check if user holds a specific stock
    boolean existsByUserIdAndStockTicker(Long userId, String ticker);
    
    // Delete holding by user and ticker
    void deleteByUserIdAndStockTicker(Long userId, String ticker);
    
    // Count holdings for a user
    long countByUserId(Long userId);
}
