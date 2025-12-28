package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for processing a refund for a payment.
 */
public class RefundRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private BigDecimal refundAmount;

    @NotNull(message = "must not be null")
    private String refundReason;

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }
}
