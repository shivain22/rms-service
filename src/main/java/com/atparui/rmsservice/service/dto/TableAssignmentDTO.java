package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.TableAssignment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TableAssignmentDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    private LocalDate assignmentDate;

    private Instant startTime;

    private Instant endTime;

    private Boolean isActive;

    private BranchTableDTO branchTable;

    private ShiftDTO shift;

    private RmsUserDTO supervisor;

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

    public BranchTableDTO getBranchTable() {
        return branchTable;
    }

    public void setBranchTable(BranchTableDTO branchTable) {
        this.branchTable = branchTable;
    }

    public ShiftDTO getShift() {
        return shift;
    }

    public void setShift(ShiftDTO shift) {
        this.shift = shift;
    }

    public RmsUserDTO getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(RmsUserDTO supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TableAssignmentDTO)) {
            return false;
        }

        TableAssignmentDTO tableAssignmentDTO = (TableAssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tableAssignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TableAssignmentDTO{" +
            "id='" + getId() + "'" +
            ", assignmentDate='" + getAssignmentDate() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", branchTable=" + getBranchTable() +
            ", shift=" + getShift() +
            ", supervisor=" + getSupervisor() +
            "}";
    }
}
