package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.BillItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BillItemDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String itemName;

    @NotNull(message = "must not be null")
    private Integer quantity;

    @NotNull(message = "must not be null")
    private BigDecimal unitPrice;

    @NotNull(message = "must not be null")
    private BigDecimal itemTotal;

    private BillDTO bill;

    private OrderItemDTO orderItem;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public BigDecimal getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }

    public BillDTO getBill() {
        return bill;
    }

    public void setBill(BillDTO bill) {
        this.bill = bill;
    }

    public OrderItemDTO getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItemDTO orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BillItemDTO)) {
            return false;
        }

        BillItemDTO billItemDTO = (BillItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, billItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BillItemDTO{" +
            "id='" + getId() + "'" +
            ", itemName='" + getItemName() + "'" +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", itemTotal=" + getItemTotal() +
            ", bill=" + getBill() +
            ", orderItem=" + getOrderItem() +
            "}";
    }
}
