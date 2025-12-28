package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for cancelling an order.
 */
public class OrderCancellationRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private String cancellationReason;

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}
