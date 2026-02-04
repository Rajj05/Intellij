package com.portfolio.controller;

import com.portfolio.dto.ApiResponse;
import com.portfolio.dto.HistoricalDataDTO;
import com.portfolio.dto.StockDTO;
import com.portfolio.model.Stock;
import com.portfolio.service.HistoricalDataService;
import com.portfolio.service.StockPriceService;
import com.portfolio.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;
    private final StockPriceService stockPriceService;
    private final HistoricalDataService historicalDataService;

//Get all available stocks

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockDTO>>> getAllStocks() {
        try {
            List<StockDTO> stocks = stockService.getAllStocks();
            return ResponseEntity.ok(ApiResponse.success(stocks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Get stock by ticker

    @GetMapping("/{ticker}")
    public ResponseEntity<ApiResponse<StockDTO>> getStockByTicker(@PathVariable String ticker) {
        try {
            StockDTO stock = stockService.getStockByTicker(ticker);
            return ResponseEntity.ok(ApiResponse.success(stock));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

//Get top gainers (best performing stocks)

    @GetMapping("/top-gainers")
    public ResponseEntity<ApiResponse<List<StockDTO>>> getTopGainers(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<StockDTO> gainers = stockService.getTopGainers(limit);
            return ResponseEntity.ok(ApiResponse.success(gainers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Get top losers (worst performing stocks)

    @GetMapping("/top-losers")
    public ResponseEntity<ApiResponse<List<StockDTO>>> getTopLosers(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<StockDTO> losers = stockService.getTopLosers(limit);
            return ResponseEntity.ok(ApiResponse.success(losers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Search stocks by ticker or company name

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StockDTO>>> searchStocks(@RequestParam String q) {
        try {
            List<StockDTO> stocks = stockService.searchStocks(q);
            return ResponseEntity.ok(ApiResponse.success(stocks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Refresh stock price from live API

    @PostMapping("/{ticker}/refresh")
    public ResponseEntity<ApiResponse<StockDTO>> refreshStockPrice(@PathVariable String ticker) {
        try {
            Stock stock = stockPriceService.refreshStockPrice(ticker.toUpperCase());
            if (stock != null) {
                return ResponseEntity.ok(ApiResponse.success(stockService.getStockByTicker(ticker)));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Stock not found or API disabled"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Get real-time quote from API (without saving)

    @GetMapping("/{ticker}/quote")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRealTimeQuote(@PathVariable String ticker) {
        try {
            Map<String, Object> quote = stockPriceService.getRealTimeQuote(ticker.toUpperCase());
            if (quote != null) {
                return ResponseEntity.ok(ApiResponse.success(quote));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("API disabled or invalid ticker"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

//Trigger manual update of all stock prices

    @PostMapping("/refresh-all")
    public ResponseEntity<ApiResponse<String>> refreshAllPrices() {
        try {
            stockPriceService.updateAllStockPrices();
            return ResponseEntity.ok(ApiResponse.success("Price update triggered"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

// Get historical data for a stock

    @GetMapping("/{ticker}/history")
    public ResponseEntity<ApiResponse<HistoricalDataDTO>> getHistoricalData(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "1M") String period) {
        try {
            HistoricalDataDTO data = historicalDataService.getHistoricalData(ticker.toUpperCase(), period);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
