package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.MenuItemAddon} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemAddonDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    private String addonName;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String addonCode;

    @NotNull(message = "must not be null")
    private BigDecimal price;

    private Boolean isActive;

    private MenuItemDTO menuItem;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAddonName() {
        return addonName;
    }

    public void setAddonName(String addonName) {
        this.addonName = addonName;
    }

    public String getAddonCode() {
        return addonCode;
    }

    public void setAddonCode(String addonCode) {
        this.addonCode = addonCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
        if (!(o instanceof MenuItemAddonDTO)) {
            return false;
        }

        MenuItemAddonDTO menuItemAddonDTO = (MenuItemAddonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, menuItemAddonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemAddonDTO{" +
            "id='" + getId() + "'" +
            ", addonName='" + getAddonName() + "'" +
            ", addonCode='" + getAddonCode() + "'" +
            ", price=" + getPrice() +
            ", isActive='" + getIsActive() + "'" +
            ", menuItem=" + getMenuItem() +
            "}";
    }
}
