package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.Order} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String orderNumber;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String orderType;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String orderSource;

    @Size(max = 50)
    private String status;

    @NotNull(message = "must not be null")
    private Instant orderDate;

    private Instant estimatedReadyTime;

    private Instant actualReadyTime;

    @Lob
    private String specialInstructions;

    private BigDecimal subtotal;

    private BigDecimal taxAmount;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private Boolean isPaid;

    private Instant cancelledAt;

    @Size(max = 255)
    private String cancelledBy;

    @Lob
    private String cancellationReason;

    private BranchDTO branch;

    private CustomerDTO customer;

    private RmsUserDTO user;

    private BranchTableDTO branchTable;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public Instant getEstimatedReadyTime() {
        return estimatedReadyTime;
    }

    public void setEstimatedReadyTime(Instant estimatedReadyTime) {
        this.estimatedReadyTime = estimatedReadyTime;
    }

    public Instant getActualReadyTime() {
        return actualReadyTime;
    }

    public void setActualReadyTime(Instant actualReadyTime) {
        this.actualReadyTime = actualReadyTime;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public BranchDTO getBranch() {
        return branch;
    }

    public void setBranch(BranchDTO branch) {
        this.branch = branch;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public RmsUserDTO getUser() {
        return user;
    }

    public void setUser(RmsUserDTO user) {
        this.user = user;
    }

    public BranchTableDTO getBranchTable() {
        return branchTable;
    }

    public void setBranchTable(BranchTableDTO branchTable) {
        this.branchTable = branchTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDTO)) {
            return false;
        }

        OrderDTO orderDTO = (OrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDTO{" +
            "id='" + getId() + "'" +
            ", orderNumber='" + getOrderNumber() + "'" +
            ", orderType='" + getOrderType() + "'" +
            ", orderSource='" + getOrderSource() + "'" +
            ", status='" + getStatus() + "'" +
            ", orderDate='" + getOrderDate() + "'" +
            ", estimatedReadyTime='" + getEstimatedReadyTime() + "'" +
            ", actualReadyTime='" + getActualReadyTime() + "'" +
            ", specialInstructions='" + getSpecialInstructions() + "'" +
            ", subtotal=" + getSubtotal() +
            ", taxAmount=" + getTaxAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", totalAmount=" + getTotalAmount() +
            ", isPaid='" + getIsPaid() + "'" +
            ", cancelledAt='" + getCancelledAt() + "'" +
            ", cancelledBy='" + getCancelledBy() + "'" +
            ", cancellationReason='" + getCancellationReason() + "'" +
            ", branch=" + getBranch() +
            ", customer=" + getCustomer() +
            ", user=" + getUser() +
            ", branchTable=" + getBranchTable() +
            "}";
    }
}
