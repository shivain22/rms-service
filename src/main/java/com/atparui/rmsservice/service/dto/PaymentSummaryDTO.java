package com.atparui.rmsservice.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO for payment summary grouped by payment method.
 */
public class PaymentSummaryDTO implements Serializable {

    private UUID branchId;
    private List<PaymentMethodSummaryDTO> methodSummaries;
    private BigDecimal totalAmount;
    private Integer totalTransactions;

    public UUID getBranchId() {
        return branchId;
    }

    public void setBranchId(UUID branchId) {
        this.branchId = branchId;
    }

    public List<PaymentMethodSummaryDTO> getMethodSummaries() {
        return methodSummaries;
    }

    public void setMethodSummaries(List<PaymentMethodSummaryDTO> methodSummaries) {
        this.methodSummaries = methodSummaries;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Integer totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    /**
     * Inner DTO for payment method summary.
     */
    public static class PaymentMethodSummaryDTO implements Serializable {

        private UUID paymentMethodId;
        private String paymentMethodName;
        private BigDecimal totalAmount;
        private Integer transactionCount;

        public UUID getPaymentMethodId() {
            return paymentMethodId;
        }

        public void setPaymentMethodId(UUID paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
        }

        public String getPaymentMethodName() {
            return paymentMethodName;
        }

        public void setPaymentMethodName(String paymentMethodName) {
            this.paymentMethodName = paymentMethodName;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Integer getTransactionCount() {
            return transactionCount;
        }

        public void setTransactionCount(Integer transactionCount) {
            this.transactionCount = transactionCount;
        }
    }
}
