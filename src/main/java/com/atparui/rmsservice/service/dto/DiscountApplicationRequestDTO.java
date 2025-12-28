package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for applying a discount to a bill.
 */
public class DiscountApplicationRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private UUID discountId;

    public UUID getDiscountId() {
        return discountId;
    }

    public void setDiscountId(UUID discountId) {
        this.discountId = discountId;
    }
}
