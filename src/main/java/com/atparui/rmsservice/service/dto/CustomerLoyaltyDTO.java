package com.atparui.rmsservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.CustomerLoyalty} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerLoyaltyDTO implements Serializable {

    private UUID id;

    private BigDecimal loyaltyPoints;

    @Size(max = 50)
    private String tier;

    @NotNull(message = "must not be null")
    private Instant enrolledAt;

    private Instant lastPointsEarnedAt;

    private Boolean isActive;

    private CustomerDTO customer;

    private RestaurantDTO restaurant;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(BigDecimal loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Instant enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Instant getLastPointsEarnedAt() {
        return lastPointsEarnedAt;
    }

    public void setLastPointsEarnedAt(Instant lastPointsEarnedAt) {
        this.lastPointsEarnedAt = lastPointsEarnedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
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
        if (!(o instanceof CustomerLoyaltyDTO)) {
            return false;
        }

        CustomerLoyaltyDTO customerLoyaltyDTO = (CustomerLoyaltyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerLoyaltyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerLoyaltyDTO{" +
            "id='" + getId() + "'" +
            ", loyaltyPoints=" + getLoyaltyPoints() +
            ", tier='" + getTier() + "'" +
            ", enrolledAt='" + getEnrolledAt() + "'" +
            ", lastPointsEarnedAt='" + getLastPointsEarnedAt() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", customer=" + getCustomer() +
            ", restaurant=" + getRestaurant() +
            "}";
    }
}
