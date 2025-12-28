package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for adding loyalty points to a customer.
 */
public class LoyaltyPointsRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private BigDecimal points;

    @NotNull(message = "must not be null")
    private String reason;

    public BigDecimal getPoints() {
        return points;
    }

    public void setPoints(BigDecimal points) {
        this.points = points;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
