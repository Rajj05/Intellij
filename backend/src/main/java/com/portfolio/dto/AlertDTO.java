package com.portfolio.dto;

import com.portfolio.model.Alert.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private Long id;
    private String ticker;
    private AlertType alertType;
    private BigDecimal threshold;
}
