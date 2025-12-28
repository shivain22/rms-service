package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.RmsUser} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RmsUserDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String externalUserId;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    private String username;

    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Size(max = 255)
    private String displayName;

    @Size(max = 500)
    private String profileImageUrl;

    private Boolean isActive;

    private Instant lastSyncAt;

    @Size(max = 50)
    private String syncStatus;

    @Lob
    private String syncError;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(Instant lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncError() {
        return syncError;
    }

    public void setSyncError(String syncError) {
        this.syncError = syncError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RmsUserDTO)) {
            return false;
        }

        RmsUserDTO rmsUserDTO = (RmsUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rmsUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RmsUserDTO{" +
            "id='" + getId() + "'" +
            ", externalUserId='" + getExternalUserId() + "'" +
            ", username='" + getUsername() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", displayName='" + getDisplayName() + "'" +
            ", profileImageUrl='" + getProfileImageUrl() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", lastSyncAt='" + getLastSyncAt() + "'" +
            ", syncStatus='" + getSyncStatus() + "'" +
            ", syncError='" + getSyncError() + "'" +
            "}";
    }
}
