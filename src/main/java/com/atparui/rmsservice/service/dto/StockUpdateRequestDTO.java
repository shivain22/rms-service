package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for updating inventory stock level.
 */
public class StockUpdateRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private BigDecimal currentStock;

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }
}
