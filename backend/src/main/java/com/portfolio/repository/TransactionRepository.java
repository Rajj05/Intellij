package com.portfolio.repository;

import com.portfolio.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Find all transactions for a user (ordered by date desc)
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);
    
    // Find transactions with pagination
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    
    // Find transactions for a specific stock
    List<Transaction> findByUserIdAndTickerOrderByTransactionDateDesc(Long userId, String ticker);
    
    // Find transactions within date range
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Get today's transactions
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND DATE(t.transactionDate) = CURRENT_DATE " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findTodayTransactions(@Param("userId") Long userId);
}
