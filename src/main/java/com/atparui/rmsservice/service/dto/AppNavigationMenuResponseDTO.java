package com.atparui.rmsservice.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for application navigation menu response containing menus with their items
 * This is for the application's navigation menu (not restaurant menu)
 */
public class AppNavigationMenuResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AppNavigationMenuWithItemsDTO> menus;

    public AppNavigationMenuResponseDTO() {
        this.menus = new ArrayList<>();
    }

    public List<AppNavigationMenuWithItemsDTO> getMenus() {
        return menus;
    }

    public void setMenus(List<AppNavigationMenuWithItemsDTO> menus) {
        this.menus = menus;
    }

    @Override
    public String toString() {
        return "AppNavigationMenuResponseDTO{" + "menus=" + menus + '}';
    }

    /**
     * DTO for navigation menu with its items
     */
    public static class AppNavigationMenuWithItemsDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        private UUID id;
        private String menuCode;
        private String menuName;
        private String description;
        private String menuType;
        private String icon;
        private String routePath;
        private Integer displayOrder;
        private Boolean isActive;
        private List<AppNavigationMenuItemDTO> items;

        public AppNavigationMenuWithItemsDTO() {
            this.items = new ArrayList<>();
        }

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

        public List<AppNavigationMenuItemDTO> getItems() {
            return items;
        }

        public void setItems(List<AppNavigationMenuItemDTO> items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return (
                "AppNavigationMenuWithItemsDTO{" +
                "id=" +
                id +
                ", menuCode='" +
                menuCode +
                '\'' +
                ", menuName='" +
                menuName +
                '\'' +
                ", menuType='" +
                menuType +
                '\'' +
                ", items=" +
                items +
                '}'
            );
        }
    }
}
