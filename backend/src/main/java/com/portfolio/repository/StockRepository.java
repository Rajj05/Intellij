package com.portfolio.repository;

import com.portfolio.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    
    // Find top gainers (positive day change, ordered by percentage)
    @Query("SELECT s FROM Stock s WHERE s.dayChangePercent > 0 ORDER BY s.dayChangePercent DESC")
    List<Stock> findTopGainers();
    
    // Find top losers (negative day change, ordered by percentage)
    @Query("SELECT s FROM Stock s WHERE s.dayChangePercent < 0 ORDER BY s.dayChangePercent ASC")
    List<Stock> findTopLosers();
    
    // Search stocks by ticker or company name
    @Query("SELECT s FROM Stock s WHERE LOWER(s.ticker) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Stock> searchStocks(String search);
    
    // Find stocks by list of tickers
    List<Stock> findByTickerIn(List<String> tickers);
}
