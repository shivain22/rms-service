package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for adjusting inventory stock (add or remove quantity).
 */
public class StockAdjustmentRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private BigDecimal adjustmentQuantity;

    @NotNull(message = "must not be null")
    private String adjustmentType; // ADD, REMOVE

    private String reason;

    public BigDecimal getAdjustmentQuantity() {
        return adjustmentQuantity;
    }

    public void setAdjustmentQuantity(BigDecimal adjustmentQuantity) {
        this.adjustmentQuantity = adjustmentQuantity;
    }

    public String getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
