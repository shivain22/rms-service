package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.Inventory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryDTO implements Serializable {

    private UUID id;

    private BigDecimal currentStock;

    @Size(max = 50)
    private String unit;

    private BigDecimal minStockLevel;

    private BigDecimal maxStockLevel;

    @NotNull(message = "must not be null")
    private Instant lastUpdatedAt;

    @Size(max = 255)
    private String lastUpdatedBy;

    private BranchDTO branch;

    private MenuItemDTO menuItem;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(BigDecimal minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public BigDecimal getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(BigDecimal maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Instant lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public BranchDTO getBranch() {
        return branch;
    }

    public void setBranch(BranchDTO branch) {
        this.branch = branch;
    }

    public MenuItemDTO getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItemDTO menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryDTO)) {
            return false;
        }

        InventoryDTO inventoryDTO = (InventoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inventoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryDTO{" +
            "id='" + getId() + "'" +
            ", currentStock=" + getCurrentStock() +
            ", unit='" + getUnit() + "'" +
            ", minStockLevel=" + getMinStockLevel() +
            ", maxStockLevel=" + getMaxStockLevel() +
            ", lastUpdatedAt='" + getLastUpdatedAt() + "'" +
            ", lastUpdatedBy='" + getLastUpdatedBy() + "'" +
            ", branch=" + getBranch() +
            ", menuItem=" + getMenuItem() +
            "}";
    }
}
