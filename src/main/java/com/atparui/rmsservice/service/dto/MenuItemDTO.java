package com.atparui.rmsservice.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.atparui.rmsservice.domain.MenuItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemDTO implements Serializable {

    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String name;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String code;

    @Lob
    private String description;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String itemType;

    @Size(max = 100)
    private String cuisineType;

    private Boolean isVegetarian;

    private Boolean isVegan;

    private Boolean isAlcoholic;

    private Integer spiceLevel;

    private Integer preparationTime;

    @NotNull(message = "must not be null")
    private BigDecimal basePrice;

    @Size(max = 500)
    private String imageUrl;

    private Boolean isAvailable;

    private Boolean isActive;

    private Integer displayOrder;

    private BranchDTO branch;

    private MenuCategoryDTO menuCategory;

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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public Boolean getIsVegan() {
        return isVegan;
    }

    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }

    public Boolean getIsAlcoholic() {
        return isAlcoholic;
    }

    public void setIsAlcoholic(Boolean isAlcoholic) {
        this.isAlcoholic = isAlcoholic;
    }

    public Integer getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(Integer spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public BranchDTO getBranch() {
        return branch;
    }

    public void setBranch(BranchDTO branch) {
        this.branch = branch;
    }

    public MenuCategoryDTO getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategoryDTO menuCategory) {
        this.menuCategory = menuCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItemDTO)) {
            return false;
        }

        MenuItemDTO menuItemDTO = (MenuItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, menuItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", itemType='" + getItemType() + "'" +
            ", cuisineType='" + getCuisineType() + "'" +
            ", isVegetarian='" + getIsVegetarian() + "'" +
            ", isVegan='" + getIsVegan() + "'" +
            ", isAlcoholic='" + getIsAlcoholic() + "'" +
            ", spiceLevel=" + getSpiceLevel() +
            ", preparationTime=" + getPreparationTime() +
            ", basePrice=" + getBasePrice() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", isAvailable='" + getIsAvailable() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", branch=" + getBranch() +
            ", menuCategory=" + getMenuCategory() +
            "}";
    }
}
