package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.Payment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String paymentNumber;

    @NotNull(message = "must not be null")
    private BigDecimal amount;

    @NotNull(message = "must not be null")
    private Instant paymentDate;

    @Size(max = 255)
    private String transactionId;

    @Lob
    private String paymentGatewayResponse;

    @Size(max = 50)
    private String status;

    @Size(max = 255)
    private String processedBy;

    @Lob
    private String notes;

    private Instant refundedAt;

    @Size(max = 255)
    private String refundedBy;

    @Lob
    private String refundReason;

    private BillDTO bill;

    private PaymentMethodDTO paymentMethod;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentGatewayResponse() {
        return paymentGatewayResponse;
    }

    public void setPaymentGatewayResponse(String paymentGatewayResponse) {
        this.paymentGatewayResponse = paymentGatewayResponse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(Instant refundedAt) {
        this.refundedAt = refundedAt;
    }

    public String getRefundedBy() {
        return refundedBy;
    }

    public void setRefundedBy(String refundedBy) {
        this.refundedBy = refundedBy;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public BillDTO getBill() {
        return bill;
    }

    public void setBill(BillDTO bill) {
        this.bill = bill;
    }

    public PaymentMethodDTO getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodDTO paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentDTO)) {
            return false;
        }

        PaymentDTO paymentDTO = (PaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentDTO{" +
            "id='" + getId() + "'" +
            ", paymentNumber='" + getPaymentNumber() + "'" +
            ", amount=" + getAmount() +
            ", paymentDate='" + getPaymentDate() + "'" +
            ", transactionId='" + getTransactionId() + "'" +
            ", paymentGatewayResponse='" + getPaymentGatewayResponse() + "'" +
            ", status='" + getStatus() + "'" +
            ", processedBy='" + getProcessedBy() + "'" +
            ", notes='" + getNotes() + "'" +
            ", refundedAt='" + getRefundedAt() + "'" +
            ", refundedBy='" + getRefundedBy() + "'" +
            ", refundReason='" + getRefundReason() + "'" +
            ", bill=" + getBill() +
            ", paymentMethod=" + getPaymentMethod() +
            "}";
    }
}
