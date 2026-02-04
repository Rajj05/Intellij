package com.portfolio.repository;

import com.portfolio.model.PortfolioSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioSnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {
    
    // Find snapshots for a user within date range
    @Query("SELECT ps FROM PortfolioSnapshot ps WHERE ps.user.id = :userId " +
           "AND ps.snapshotDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ps.snapshotDate ASC")
    List<PortfolioSnapshot> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // Find latest snapshot for a user
    Optional<PortfolioSnapshot> findTopByUserIdOrderBySnapshotDateDesc(Long userId);
    
    // Find snapshot for specific date
    Optional<PortfolioSnapshot> findByUserIdAndSnapshotDate(Long userId, LocalDate snapshotDate);
    
    // Get snapshots for last N days
    @Query("SELECT ps FROM PortfolioSnapshot ps WHERE ps.user.id = :userId " +
           "ORDER BY ps.snapshotDate DESC LIMIT :days")
    List<PortfolioSnapshot> findRecentSnapshots(@Param("userId") Long userId, @Param("days") int days);
}
