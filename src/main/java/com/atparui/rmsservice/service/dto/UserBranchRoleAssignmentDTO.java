package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for assigning a role to a user for a branch.
 */
public class UserBranchRoleAssignmentDTO implements Serializable {

    @NotNull(message = "must not be null")
    private UUID userId;

    @NotNull(message = "must not be null")
    private UUID branchId;

    @NotNull(message = "must not be null")
    private String role;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getBranchId() {
        return branchId;
    }

    public void setBranchId(UUID branchId) {
        this.branchId = branchId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
