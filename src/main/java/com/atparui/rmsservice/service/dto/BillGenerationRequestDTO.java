package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for generating a bill from an order.
 */
public class BillGenerationRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private UUID orderId;

    private UUID discountId;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getDiscountId() {
        return discountId;
    }

    public void setDiscountId(UUID discountId) {
        this.discountId = discountId;
    }
}
