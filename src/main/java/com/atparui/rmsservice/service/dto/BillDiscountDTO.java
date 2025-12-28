package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.BillDiscount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BillDiscountDTO implements Serializable {

    private UUID id;

    @Size(max = 50)
    private String discountCode;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String discountType;

    @NotNull(message = "must not be null")
    private BigDecimal discountValue;

    @NotNull(message = "must not be null")
    private BigDecimal discountAmount;

    private BillDTO bill;

    private DiscountDTO discount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public BillDTO getBill() {
        return bill;
    }

    public void setBill(BillDTO bill) {
        this.bill = bill;
    }

    public DiscountDTO getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountDTO discount) {
        this.discount = discount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BillDiscountDTO)) {
            return false;
        }

        BillDiscountDTO billDiscountDTO = (BillDiscountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, billDiscountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BillDiscountDTO{" +
            "id='" + getId() + "'" +
            ", discountCode='" + getDiscountCode() + "'" +
            ", discountType='" + getDiscountType() + "'" +
            ", discountValue=" + getDiscountValue() +
            ", discountAmount=" + getDiscountAmount() +
            ", bill=" + getBill() +
            ", discount=" + getDiscount() +
            "}";
    }
}
