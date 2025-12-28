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
 * A Discount.
 */
@Table("discount")
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Discount implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private UUID id;

    @Size(max = 50)
    @Column("discount_code")
    private String discountCode;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("discount_name")
    private String discountName;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    @Column("discount_type")
    private String discountType;

    @NotNull(message = "must not be null")
    @Column("discount_value")
    private BigDecimal discountValue;

    @Column("min_order_amount")
    private BigDecimal minOrderAmount;

    @Column("max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Size(max = 50)
    @Column("applicable_to")
    private String applicableTo;

    @NotNull(message = "must not be null")
    @Column("valid_from")
    private Instant validFrom;

    @Column("valid_to")
    private Instant validTo;

    @Column("max_uses")
    private Integer maxUses;

    @Column("current_uses")
    private Integer currentUses;

    @Column("is_active")
    private Boolean isActive;

    @org.springframework.data.annotation.Transient
    private boolean isPersisted;

    @org.springframework.data.annotation.Transient
    private Restaurant restaurant;

    @Column("restaurant_id")
    private UUID restaurantId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Discount id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDiscountCode() {
        return this.discountCode;
    }

    public Discount discountCode(String discountCode) {
        this.setDiscountCode(discountCode);
        return this;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public String getDiscountName() {
        return this.discountName;
    }

    public Discount discountName(String discountName) {
        this.setDiscountName(discountName);
        return this;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountType() {
        return this.discountType;
    }

    public Discount discountType(String discountType) {
        this.setDiscountType(discountType);
        return this;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return this.discountValue;
    }

    public Discount discountValue(BigDecimal discountValue) {
        this.setDiscountValue(discountValue);
        return this;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue != null ? discountValue.stripTrailingZeros() : null;
    }

    public BigDecimal getMinOrderAmount() {
        return this.minOrderAmount;
    }

    public Discount minOrderAmount(BigDecimal minOrderAmount) {
        this.setMinOrderAmount(minOrderAmount);
        return this;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount != null ? minOrderAmount.stripTrailingZeros() : null;
    }

    public BigDecimal getMaxDiscountAmount() {
        return this.maxDiscountAmount;
    }

    public Discount maxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.setMaxDiscountAmount(maxDiscountAmount);
        return this;
    }

    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount != null ? maxDiscountAmount.stripTrailingZeros() : null;
    }

    public String getApplicableTo() {
        return this.applicableTo;
    }

    public Discount applicableTo(String applicableTo) {
        this.setApplicableTo(applicableTo);
        return this;
    }

    public void setApplicableTo(String applicableTo) {
        this.applicableTo = applicableTo;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public Discount validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidTo() {
        return this.validTo;
    }

    public Discount validTo(Instant validTo) {
        this.setValidTo(validTo);
        return this;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }

    public Integer getMaxUses() {
        return this.maxUses;
    }

    public Discount maxUses(Integer maxUses) {
        this.setMaxUses(maxUses);
        return this;
    }

    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }

    public Integer getCurrentUses() {
        return this.currentUses;
    }

    public Discount currentUses(Integer currentUses) {
        this.setCurrentUses(currentUses);
        return this;
    }

    public void setCurrentUses(Integer currentUses) {
        this.currentUses = currentUses;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Discount isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @org.springframework.data.annotation.Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Discount setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        this.restaurantId = restaurant != null ? restaurant.getId() : null;
    }

    public Discount restaurant(Restaurant restaurant) {
        this.setRestaurant(restaurant);
        return this;
    }

    public UUID getRestaurantId() {
        return this.restaurantId;
    }

    public void setRestaurantId(UUID restaurant) {
        this.restaurantId = restaurant;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Discount)) {
            return false;
        }
        return getId() != null && getId().equals(((Discount) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Discount{" +
            "id=" + getId() +
            ", discountCode='" + getDiscountCode() + "'" +
            ", discountName='" + getDiscountName() + "'" +
            ", discountType='" + getDiscountType() + "'" +
            ", discountValue=" + getDiscountValue() +
            ", minOrderAmount=" + getMinOrderAmount() +
            ", maxDiscountAmount=" + getMaxDiscountAmount() +
            ", applicableTo='" + getApplicableTo() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validTo='" + getValidTo() + "'" +
            ", maxUses=" + getMaxUses() +
            ", currentUses=" + getCurrentUses() +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
