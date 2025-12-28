package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.AppNavigationMenu} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppNavigationMenuDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String menuCode;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String menuName;

    @Lob
    private String description;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String menuType;

    @Size(max = 100)
    private String icon;

    @Size(max = 500)
    private String routePath;

    private Integer displayOrder;

    private Boolean isActive;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppNavigationMenuDTO)) {
            return false;
        }

        AppNavigationMenuDTO appNavigationMenuDTO = (AppNavigationMenuDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appNavigationMenuDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppNavigationMenuDTO{" +
            "id='" + getId() + "'" +
            ", menuCode='" + getMenuCode() + "'" +
            ", menuName='" + getMenuName() + "'" +
            ", description='" + getDescription() + "'" +
            ", menuType='" + getMenuType() + "'" +
            ", icon='" + getIcon() + "'" +
            ", routePath='" + getRoutePath() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
