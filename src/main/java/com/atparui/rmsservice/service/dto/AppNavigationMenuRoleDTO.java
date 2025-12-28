package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.AppNavigationMenuRole} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppNavigationMenuRoleDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String role;

    private Boolean isActive;

    private AppNavigationMenuDTO appNavigationMenu;

    private AppNavigationMenuItemDTO appNavigationMenuItem;

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

    public AppNavigationMenuDTO getAppNavigationMenu() {
        return appNavigationMenu;
    }

    public void setAppNavigationMenu(AppNavigationMenuDTO appNavigationMenu) {
        this.appNavigationMenu = appNavigationMenu;
    }

    public AppNavigationMenuItemDTO getAppNavigationMenuItem() {
        return appNavigationMenuItem;
    }

    public void setAppNavigationMenuItem(AppNavigationMenuItemDTO appNavigationMenuItem) {
        this.appNavigationMenuItem = appNavigationMenuItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppNavigationMenuRoleDTO)) {
            return false;
        }

        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = (AppNavigationMenuRoleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appNavigationMenuRoleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppNavigationMenuRoleDTO{" +
            "id='" + getId() + "'" +
            ", role='" + getRole() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", appNavigationMenu=" + getAppNavigationMenu() +
            ", appNavigationMenuItem=" + getAppNavigationMenuItem() +
            "}";
    }
}
