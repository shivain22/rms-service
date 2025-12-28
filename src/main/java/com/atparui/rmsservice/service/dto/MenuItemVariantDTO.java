package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.MenuItemVariant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemVariantDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    private String variantName;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String variantCode;

    private BigDecimal priceModifier;

    private Boolean isDefault;

    private Integer displayOrder;

    private Boolean isActive;

    private MenuItemDTO menuItem;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public BigDecimal getPriceModifier() {
        return priceModifier;
    }

    public void setPriceModifier(BigDecimal priceModifier) {
        this.priceModifier = priceModifier;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        if (!(o instanceof MenuItemVariantDTO)) {
            return false;
        }

        MenuItemVariantDTO menuItemVariantDTO = (MenuItemVariantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, menuItemVariantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemVariantDTO{" +
            "id='" + getId() + "'" +
            ", variantName='" + getVariantName() + "'" +
            ", variantCode='" + getVariantCode() + "'" +
            ", priceModifier=" + getPriceModifier() +
            ", isDefault='" + getIsDefault() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", isActive='" + getIsActive() + "'" +
            ", menuItem=" + getMenuItem() +
            "}";
    }
}
