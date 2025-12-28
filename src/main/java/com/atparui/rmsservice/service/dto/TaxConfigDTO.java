package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.TaxConfig} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaxConfigDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    private String taxName;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String taxCode;

    @NotNull(message = "must not be null")
    private BigDecimal taxRate;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String taxType;

    private Boolean isApplicableToFood;

    private Boolean isApplicableToBeverage;

    private Boolean isApplicableToAlcohol;

    @NotNull(message = "must not be null")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private Boolean isActive;

    private RestaurantDTO restaurant;

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

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public Boolean getIsApplicableToFood() {
        return isApplicableToFood;
    }

    public void setIsApplicableToFood(Boolean isApplicableToFood) {
        this.isApplicableToFood = isApplicableToFood;
    }

    public Boolean getIsApplicableToBeverage() {
        return isApplicableToBeverage;
    }

    public void setIsApplicableToBeverage(Boolean isApplicableToBeverage) {
        this.isApplicableToBeverage = isApplicableToBeverage;
    }

    public Boolean getIsApplicableToAlcohol() {
        return isApplicableToAlcohol;
    }

    public void setIsApplicableToAlcohol(Boolean isApplicableToAlcohol) {
        this.isApplicableToAlcohol = isApplicableToAlcohol;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public RestaurantDTO getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxConfigDTO)) {
            return false;
        }

        TaxConfigDTO taxConfigDTO = (TaxConfigDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taxConfigDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaxConfigDTO{" +
            "id='" + getId() + "'" +
            ", taxName='" + getTaxName() + "'" +
            ", taxCode='" + getTaxCode() + "'" +
            ", taxRate=" + getTaxRate() +
            ", taxType='" + getTaxType() + "'" +
            ", isApplicableToFood='" + getIsApplicableToFood() + "'" +
            ", isApplicableToBeverage='" + getIsApplicableToBeverage() + "'" +
            ", isApplicableToAlcohol='" + getIsApplicableToAlcohol() + "'" +
            ", effectiveFrom='" + getEffectiveFrom() + "'" +
            ", effectiveTo='" + getEffectiveTo() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", restaurant=" + getRestaurant() +
            "}";
    }
}
