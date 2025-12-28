package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.Bill} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BillDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String billNumber;

    @NotNull(message = "must not be null")
    private Instant billDate;

    @NotNull(message = "must not be null")
    private BigDecimal subtotal;

    private BigDecimal taxAmount;

    private BigDecimal discountAmount;

    private BigDecimal serviceCharge;

    @NotNull(message = "must not be null")
    private BigDecimal totalAmount;

    private BigDecimal amountPaid;

    @NotNull(message = "must not be null")
    private BigDecimal amountDue;

    @Size(max = 50)
    private String status;

    @Size(max = 255)
    private String generatedBy;

    @Lob
    private String notes;

    private OrderDTO order;

    private BranchDTO branch;

    private CustomerDTO customer;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Instant getBillDate() {
        return billDate;
    }

    public void setBillDate(Instant billDate) {
        this.billDate = billDate;
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

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BillDTO)) {
            return false;
        }

        BillDTO billDTO = (BillDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, billDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BillDTO{" +
            "id='" + getId() + "'" +
            ", billNumber='" + getBillNumber() + "'" +
            ", billDate='" + getBillDate() + "'" +
            ", subtotal=" + getSubtotal() +
            ", taxAmount=" + getTaxAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", serviceCharge=" + getServiceCharge() +
            ", totalAmount=" + getTotalAmount() +
            ", amountPaid=" + getAmountPaid() +
            ", amountDue=" + getAmountDue() +
            ", status='" + getStatus() + "'" +
            ", generatedBy='" + getGeneratedBy() + "'" +
            ", notes='" + getNotes() + "'" +
            ", order=" + getOrder() +
            ", branch=" + getBranch() +
            ", customer=" + getCustomer() +
            "}";
    }
}
