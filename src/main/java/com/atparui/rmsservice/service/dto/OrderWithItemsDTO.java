package com.atparui.rmsservice.service.dto;

import java.util.List;

/**
 * DTO for order with all items and customizations.
 */
public class OrderWithItemsDTO extends OrderDTO {

    private List<OrderItemWithDetailsDTO> items;

    public List<OrderItemWithDetailsDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemWithDetailsDTO> items) {
        this.items = items;
    }

    /**
     * Inner DTO for order item with details.
     */
    public static class OrderItemWithDetailsDTO extends OrderItemDTO {

        private MenuItemDTO menuItem;
        private MenuItemVariantDTO variant;
        private List<MenuItemAddonDTO> addons;
        private List<OrderItemCustomizationDTO> customizations;

        public MenuItemDTO getMenuItem() {
            return menuItem;
        }

        public void setMenuItem(MenuItemDTO menuItem) {
            this.menuItem = menuItem;
        }

        public MenuItemVariantDTO getVariant() {
            return variant;
        }

        public void setVariant(MenuItemVariantDTO variant) {
            this.variant = variant;
        }

        public List<MenuItemAddonDTO> getAddons() {
            return addons;
        }

        public void setAddons(List<MenuItemAddonDTO> addons) {
            this.addons = addons;
        }

        public List<OrderItemCustomizationDTO> getCustomizations() {
            return customizations;
        }

        public void setCustomizations(List<OrderItemCustomizationDTO> customizations) {
            this.customizations = customizations;
        }
    }
}
