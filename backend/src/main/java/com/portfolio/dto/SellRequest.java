package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellRequest {
    private Long userId;
    private String ticker;
    private BigDecimal quantity;  // null or 0 means sell all
    private boolean sellAll = false;
}
