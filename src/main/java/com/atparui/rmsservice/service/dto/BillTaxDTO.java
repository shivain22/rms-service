package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.BillTax} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BillTaxDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    private String taxName;

    @NotNull(message = "must not be null")
    private BigDecimal taxRate;

    @NotNull(message = "must not be null")
    private BigDecimal taxableAmount;

    @NotNull(message = "must not be null")
    private BigDecimal taxAmount;

    private BillDTO bill;

    private TaxConfigDTO taxConfig;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BillDTO getBill() {
        return bill;
    }

    public void setBill(BillDTO bill) {
        this.bill = bill;
    }

    public TaxConfigDTO getTaxConfig() {
        return taxConfig;
    }

    public void setTaxConfig(TaxConfigDTO taxConfig) {
        this.taxConfig = taxConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BillTaxDTO)) {
            return false;
        }

        BillTaxDTO billTaxDTO = (BillTaxDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, billTaxDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BillTaxDTO{" +
            "id='" + getId() + "'" +
            ", taxName='" + getTaxName() + "'" +
            ", taxRate=" + getTaxRate() +
            ", taxableAmount=" + getTaxableAmount() +
            ", taxAmount=" + getTaxAmount() +
            ", bill=" + getBill() +
            ", taxConfig=" + getTaxConfig() +
            "}";
    }
}
