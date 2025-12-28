package com.atparui.rmsservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Bill.
 */
@Table("bill")
@JsonIgnoreProperties(value = { "new" })
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bill")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Bill implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    @Column("bill_number")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String billNumber;

    @NotNull(message = "must not be null")
    @Column("bill_date")
    private Instant billDate;

    @NotNull(message = "must not be null")
    @Column("subtotal")
    private BigDecimal subtotal;

    @Column("tax_amount")
    private BigDecimal taxAmount;

    @Column("discount_amount")
    private BigDecimal discountAmount;

    @Column("service_charge")
    private BigDecimal serviceCharge;

    @NotNull(message = "must not be null")
    @Column("total_amount")
    private BigDecimal totalAmount;

    @Column("amount_paid")
    private BigDecimal amountPaid;

    @NotNull(message = "must not be null")
    @Column("amount_due")
    private BigDecimal amountDue;

    @Size(max = 50)
    @Column("status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String status;

    @Size(max = 255)
    @Column("generated_by")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String generatedBy;

    @Column("notes")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String notes;

    @org.springframework.data.annotation.Transient
    private boolean isPersisted;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "branch", "customer", "user", "branchTable" }, allowSetters = true)
    private Order order;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "restaurant" }, allowSetters = true)
    private Branch branch;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Customer customer;

    @Column("order_id")
    private UUID orderId;

    @Column("branch_id")
    private UUID branchId;

    @Column("customer_id")
    private UUID customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Bill id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBillNumber() {
        return this.billNumber;
    }

    public Bill billNumber(String billNumber) {
        this.setBillNumber(billNumber);
        return this;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Instant getBillDate() {
        return this.billDate;
    }

    public Bill billDate(Instant billDate) {
        this.setBillDate(billDate);
        return this;
    }

    public void setBillDate(Instant billDate) {
        this.billDate = billDate;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public Bill subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal != null ? subtotal.stripTrailingZeros() : null;
    }

    public BigDecimal getTaxAmount() {
        return this.taxAmount;
    }

    public Bill taxAmount(BigDecimal taxAmount) {
        this.setTaxAmount(taxAmount);
        return this;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount != null ? taxAmount.stripTrailingZeros() : null;
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public Bill discountAmount(BigDecimal discountAmount) {
        this.setDiscountAmount(discountAmount);
        return this;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount != null ? discountAmount.stripTrailingZeros() : null;
    }

    public BigDecimal getServiceCharge() {
        return this.serviceCharge;
    }

    public Bill serviceCharge(BigDecimal serviceCharge) {
        this.setServiceCharge(serviceCharge);
        return this;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge != null ? serviceCharge.stripTrailingZeros() : null;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Bill totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount.stripTrailingZeros() : null;
    }

    public BigDecimal getAmountPaid() {
        return this.amountPaid;
    }

    public Bill amountPaid(BigDecimal amountPaid) {
        this.setAmountPaid(amountPaid);
        return this;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid != null ? amountPaid.stripTrailingZeros() : null;
    }

    public BigDecimal getAmountDue() {
        return this.amountDue;
    }

    public Bill amountDue(BigDecimal amountDue) {
        this.setAmountDue(amountDue);
        return this;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue != null ? amountDue.stripTrailingZeros() : null;
    }

    public String getStatus() {
        return this.status;
    }

    public Bill status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGeneratedBy() {
        return this.generatedBy;
    }

    public Bill generatedBy(String generatedBy) {
        this.setGeneratedBy(generatedBy);
        return this;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public String getNotes() {
        return this.notes;
    }

    public Bill notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @org.springframework.data.annotation.Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Bill setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }

    public Bill order(Order order) {
        this.setOrder(order);
        return this;
    }

    public Branch getBranch() {
        return this.branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
        this.branchId = branch != null ? branch.getId() : null;
    }

    public Bill branch(Branch branch) {
        this.setBranch(branch);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public Bill customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public UUID getOrderId() {
        return this.orderId;
    }

    public void setOrderId(UUID order) {
        this.orderId = order;
    }

    public UUID getBranchId() {
        return this.branchId;
    }

    public void setBranchId(UUID branch) {
        this.branchId = branch;
    }

    public UUID getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(UUID customer) {
        this.customerId = customer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bill)) {
            return false;
        }
        return getId() != null && getId().equals(((Bill) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bill{" +
            "id=" + getId() +
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
            "}";
    }
}
