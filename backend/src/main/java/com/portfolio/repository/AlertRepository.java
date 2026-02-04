package com.portfolio.repository;

import com.portfolio.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    // Find all alerts for a user
    List<Alert> findByUserId(Long userId);
    
    // Find alerts by ticker and user
    List<Alert> findByUserIdAndTicker(Long userId, String ticker);
}
