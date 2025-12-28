package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.BranchTable} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BranchTableDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String tableNumber;

    @Size(max = 255)
    private String tableName;

    @NotNull(message = "must not be null")
    private Integer capacity;

    @Size(max = 50)
    private String floor;

    @Size(max = 100)
    private String section;

    @Size(max = 50)
    private String status;

    @Size(max = 500)
    private String qrCode;

    @Size(max = 500)
    private String qrCodeUrl;

    private Boolean isActive;

    private BranchDTO branch;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
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
        if (!(o instanceof BranchTableDTO)) {
            return false;
        }

        BranchTableDTO branchTableDTO = (BranchTableDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, branchTableDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BranchTableDTO{" +
            "id='" + getId() + "'" +
            ", tableNumber='" + getTableNumber() + "'" +
            ", tableName='" + getTableName() + "'" +
            ", capacity=" + getCapacity() +
            ", floor='" + getFloor() + "'" +
            ", section='" + getSection() + "'" +
            ", status='" + getStatus() + "'" +
            ", qrCode='" + getQrCode() + "'" +
            ", qrCodeUrl='" + getQrCodeUrl() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", branch=" + getBranch() +
            "}";
    }
}
