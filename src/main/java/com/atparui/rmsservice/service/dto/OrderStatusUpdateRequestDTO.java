package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * DTO for updating order status.
 */
public class OrderStatusUpdateRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String status;

    private String notes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
