package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.OrderStatusHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderStatusHistoryDTO implements Serializable {

    private UUID id;

    @Size(max = 50)
    private String previousStatus;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String newStatus;

    @NotNull(message = "must not be null")
    private Instant changedAt;

    @Size(max = 255)
    private String changedBy;

    @Lob
    private String notes;

    private OrderDTO order;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderStatusHistoryDTO)) {
            return false;
        }

        OrderStatusHistoryDTO orderStatusHistoryDTO = (OrderStatusHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderStatusHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderStatusHistoryDTO{" +
            "id='" + getId() + "'" +
            ", previousStatus='" + getPreviousStatus() + "'" +
            ", newStatus='" + getNewStatus() + "'" +
            ", changedAt='" + getChangedAt() + "'" +
            ", changedBy='" + getChangedBy() + "'" +
            ", notes='" + getNotes() + "'" +
            ", order=" + getOrder() +
            "}";
    }
}
