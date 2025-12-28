package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating a new order.
 */
public class OrderCreationRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private UUID branchId;

    private UUID customerId;

    private UUID userId;

    private UUID branchTableId;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String orderType; // ONLINE, OFFLINE, TAKEAWAY, DELIVERY

    @NotNull(message = "must not be null")
    @Size(min = 1)
    private List<OrderItemRequestDTO> items;

    private String specialInstructions;

    public UUID getBranchId() {
        return branchId;
    }

    public void setBranchId(UUID branchId) {
        this.branchId = branchId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getBranchTableId() {
        return branchTableId;
    }

    public void setBranchTableId(UUID branchTableId) {
        this.branchTableId = branchTableId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    /**
     * Inner DTO for order item request.
     */
    public static class OrderItemRequestDTO implements Serializable {

        @NotNull(message = "must not be null")
        private UUID menuItemId;

        @NotNull(message = "must not be null")
        private Integer quantity;

        private UUID menuItemVariantId;

        private List<UUID> addonIds;

        private String specialInstructions;

        public UUID getMenuItemId() {
            return menuItemId;
        }

        public void setMenuItemId(UUID menuItemId) {
            this.menuItemId = menuItemId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public UUID getMenuItemVariantId() {
            return menuItemVariantId;
        }

        public void setMenuItemVariantId(UUID menuItemVariantId) {
            this.menuItemVariantId = menuItemVariantId;
        }

        public List<UUID> getAddonIds() {
            return addonIds;
        }

        public void setAddonIds(List<UUID> addonIds) {
            this.addonIds = addonIds;
        }

        public String getSpecialInstructions() {
            return specialInstructions;
        }

        public void setSpecialInstructions(String specialInstructions) {
            this.specialInstructions = specialInstructions;
        }
    }
}
