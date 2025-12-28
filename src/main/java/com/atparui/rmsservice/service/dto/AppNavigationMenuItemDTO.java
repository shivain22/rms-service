package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.AppNavigationMenuItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppNavigationMenuItemDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String itemCode;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String itemName;

    @Lob
    private String description;

    @Size(max = 100)
    private String icon;

    @NotNull(message = "must not be null")
    @Size(max = 500)
    private String routePath;

    @Size(max = 500)
    private String componentPath;

    private Integer displayOrder;

    @Size(max = 50)
    private String badgeText;

    @Size(max = 50)
    private String badgeColor;

    private Boolean isActive;

    private AppNavigationMenuDTO parentMenu;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public String getComponentPath() {
        return componentPath;
    }

    public void setComponentPath(String componentPath) {
        this.componentPath = componentPath;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getBadgeText() {
        return badgeText;
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public void setBadgeColor(String badgeColor) {
        this.badgeColor = badgeColor;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public AppNavigationMenuDTO getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(AppNavigationMenuDTO parentMenu) {
        this.parentMenu = parentMenu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppNavigationMenuItemDTO)) {
            return false;
        }

        AppNavigationMenuItemDTO appNavigationMenuItemDTO = (AppNavigationMenuItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appNavigationMenuItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppNavigationMenuItemDTO{" +
            "id='" + getId() + "'" +
            ", itemCode='" + getItemCode() + "'" +
            ", itemName='" + getItemName() + "'" +
            ", description='" + getDescription() + "'" +
            ", icon='" + getIcon() + "'" +
            ", routePath='" + getRoutePath() + "'" +
            ", componentPath='" + getComponentPath() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", badgeText='" + getBadgeText() + "'" +
            ", badgeColor='" + getBadgeColor() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", parentMenu=" + getParentMenu() +
            "}";
    }
}
