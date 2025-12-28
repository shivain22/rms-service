package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for processing a payment for a bill.
 */
public class PaymentRequestDTO implements Serializable {

    @NotNull(message = "must not be null")
    private UUID billId;

    @NotNull(message = "must not be null")
    private UUID paymentMethodId;

    @NotNull(message = "must not be null")
    private BigDecimal amount;

    private String transactionId;

    private String notes;

    public UUID getBillId() {
        return billId;
    }

    public void setBillId(UUID billId) {
        this.billId = billId;
    }

    public UUID getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(UUID paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
