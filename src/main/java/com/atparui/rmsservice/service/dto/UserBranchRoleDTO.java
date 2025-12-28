package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.UserBranchRole} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserBranchRoleDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String role;

    private Boolean isActive;

    @NotNull(message = "must not be null")
    private Instant assignedAt;

    @Size(max = 255)
    private String assignedBy;

    private Instant revokedAt;

    @Size(max = 255)
    private String revokedBy;

    private RmsUserDTO user;

    private BranchDTO branch;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getRevokedBy() {
        return revokedBy;
    }

    public void setRevokedBy(String revokedBy) {
        this.revokedBy = revokedBy;
    }

    public RmsUserDTO getUser() {
        return user;
    }

    public void setUser(RmsUserDTO user) {
        this.user = user;
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
        if (!(o instanceof UserBranchRoleDTO)) {
            return false;
        }

        UserBranchRoleDTO userBranchRoleDTO = (UserBranchRoleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userBranchRoleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserBranchRoleDTO{" +
            "id='" + getId() + "'" +
            ", role='" + getRole() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", assignedAt='" + getAssignedAt() + "'" +
            ", assignedBy='" + getAssignedBy() + "'" +
            ", revokedAt='" + getRevokedAt() + "'" +
            ", revokedBy='" + getRevokedBy() + "'" +
            ", user=" + getUser() +
            ", branch=" + getBranch() +
            "}";
    }
}
