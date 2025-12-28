package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.OrderItemCustomization} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItemCustomizationDTO implements Serializable {

    private UUID id;

    private Integer quantity;

    @NotNull(message = "must not be null")
    private BigDecimal unitPrice;

    @NotNull(message = "must not be null")
    private BigDecimal totalPrice;

    private OrderItemDTO orderItem;

    private MenuItemAddonDTO menuItemAddon;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderItemDTO getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItemDTO orderItem) {
        this.orderItem = orderItem;
    }

    public MenuItemAddonDTO getMenuItemAddon() {
        return menuItemAddon;
    }

    public void setMenuItemAddon(MenuItemAddonDTO menuItemAddon) {
        this.menuItemAddon = menuItemAddon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItemCustomizationDTO)) {
            return false;
        }

        OrderItemCustomizationDTO orderItemCustomizationDTO = (OrderItemCustomizationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderItemCustomizationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemCustomizationDTO{" +
            "id='" + getId() + "'" +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", totalPrice=" + getTotalPrice() +
            ", orderItem=" + getOrderItem() +
            ", menuItemAddon=" + getMenuItemAddon() +
            "}";
    }
}
