package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating daily table assignments.
 */
public class DailyTableAssignmentRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private UUID branchId;

    @NotNull(message = "must not be null")
    private LocalDate assignmentDate;

    @NotNull(message = "must not be null")
    @Size(min = 1)
    private List<TableAssignmentItemDTO> assignments;

    public UUID getBranchId() {
        return branchId;
    }

    public void setBranchId(UUID branchId) {
        this.branchId = branchId;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public List<TableAssignmentItemDTO> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<TableAssignmentItemDTO> assignments) {
        this.assignments = assignments;
    }

    /**
     * Inner DTO for table assignment item.
     */
    public static class TableAssignmentItemDTO implements Serializable {

        @NotNull(message = "must not be null")
        private UUID branchTableId;

        @NotNull(message = "must not be null")
        private UUID shiftId;

        public UUID getBranchTableId() {
            return branchTableId;
        }

        public void setBranchTableId(UUID branchTableId) {
            this.branchTableId = branchTableId;
        }

        public UUID getShiftId() {
            return shiftId;
        }

        public void setShiftId(UUID shiftId) {
            this.shiftId = shiftId;
        }
    }
}
