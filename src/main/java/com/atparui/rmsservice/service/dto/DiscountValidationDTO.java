package com.atparui.rmsservice.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for discount validation result.
 */
public class DiscountValidationDTO implements Serializable {

    private Boolean isValid;
    private UUID discountId;
    private String discountCode;
    private String discountType; // PERCENTAGE, FIXED_AMOUNT
    private BigDecimal discountValue;
    private BigDecimal discountAmount;
    private String message;

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public UUID getDiscountId() {
        return discountId;
    }

    public void setDiscountId(UUID discountId) {
        this.discountId = discountId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
