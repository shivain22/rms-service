package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.UserSyncLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserSyncLogDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String syncType;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String syncStatus;

    @Size(max = 255)
    private String externalUserId;

    @Lob
    private String requestPayload;

    @Lob
    private String responsePayload;

    @Lob
    private String errorMessage;

    @NotNull(message = "must not be null")
    private Instant syncedAt;

    @Size(max = 255)
    private String syncedBy;

    private RmsUserDTO user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(Instant syncedAt) {
        this.syncedAt = syncedAt;
    }

    public String getSyncedBy() {
        return syncedBy;
    }

    public void setSyncedBy(String syncedBy) {
        this.syncedBy = syncedBy;
    }

    public RmsUserDTO getUser() {
        return user;
    }

    public void setUser(RmsUserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserSyncLogDTO)) {
            return false;
        }

        UserSyncLogDTO userSyncLogDTO = (UserSyncLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userSyncLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserSyncLogDTO{" +
            "id='" + getId() + "'" +
            ", syncType='" + getSyncType() + "'" +
            ", syncStatus='" + getSyncStatus() + "'" +
            ", externalUserId='" + getExternalUserId() + "'" +
            ", requestPayload='" + getRequestPayload() + "'" +
            ", responsePayload='" + getResponsePayload() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", syncedAt='" + getSyncedAt() + "'" +
            ", syncedBy='" + getSyncedBy() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
