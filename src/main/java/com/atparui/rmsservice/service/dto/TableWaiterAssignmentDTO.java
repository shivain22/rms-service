package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.TableWaiterAssignment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TableWaiterAssignmentDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    private LocalDate assignmentDate;

    private Instant startTime;

    private Instant endTime;

    private Boolean isActive;

    private TableAssignmentDTO tableAssignment;

    private RmsUserDTO waiter;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public TableAssignmentDTO getTableAssignment() {
        return tableAssignment;
    }

    public void setTableAssignment(TableAssignmentDTO tableAssignment) {
        this.tableAssignment = tableAssignment;
    }

    public RmsUserDTO getWaiter() {
        return waiter;
    }

    public void setWaiter(RmsUserDTO waiter) {
        this.waiter = waiter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TableWaiterAssignmentDTO)) {
            return false;
        }

        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = (TableWaiterAssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tableWaiterAssignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TableWaiterAssignmentDTO{" +
            "id='" + getId() + "'" +
            ", assignmentDate='" + getAssignmentDate() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", tableAssignment=" + getTableAssignment() +
            ", waiter=" + getWaiter() +
            "}";
    }
}
