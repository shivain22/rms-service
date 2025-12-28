package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.Shift} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShiftDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    private String shiftName;

    @NotNull(message = "must not be null")
    private LocalTime startTime;

    @NotNull(message = "must not be null")
    private LocalTime endTime;

    @NotNull(message = "must not be null")
    private LocalDate shiftDate;

    private Boolean isActive;

    private BranchDTO branch;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public BranchDTO getBranch() {
        return branch;
    }

    public void setBranch(BranchDTO branch) {
        this.branch = branch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShiftDTO)) {
            return false;
        }

        ShiftDTO shiftDTO = (ShiftDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shiftDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShiftDTO{" +
            "id='" + getId() + "'" +
            ", shiftName='" + getShiftName() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", shiftDate='" + getShiftDate() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", branch=" + getBranch() +
            "}";
    }
}
