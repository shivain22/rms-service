package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.MenuCategory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuCategoryDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String name;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String code;

    @Lob
    private String description;

    private Integer displayOrder;

    @Size(max = 500)
    private String imageUrl;

    private Boolean isActive;

    private RestaurantDTO restaurant;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
        if (!(o instanceof MenuCategoryDTO)) {
            return false;
        }

        MenuCategoryDTO menuCategoryDTO = (MenuCategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, menuCategoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuCategoryDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", restaurant=" + getRestaurant() +
            "}";
    }
}
