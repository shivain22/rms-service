package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.MenuItem;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MenuItem}, with proper type conversions.
 */
@Service
public class MenuItemRowMapper implements BiFunction<Row, String, MenuItem> {

    private final ColumnConverter converter;

    public MenuItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MenuItem} stored in the database.
     */
    @Override
    public MenuItem apply(Row row, String prefix) {
        MenuItem entity = new MenuItem();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setItemType(converter.fromRow(row, prefix + "_item_type", String.class));
        entity.setCuisineType(converter.fromRow(row, prefix + "_cuisine_type", String.class));
        entity.setIsVegetarian(converter.fromRow(row, prefix + "_is_vegetarian", Boolean.class));
        entity.setIsVegan(converter.fromRow(row, prefix + "_is_vegan", Boolean.class));
        entity.setIsAlcoholic(converter.fromRow(row, prefix + "_is_alcoholic", Boolean.class));
        entity.setSpiceLevel(converter.fromRow(row, prefix + "_spice_level", Integer.class));
        entity.setPreparationTime(converter.fromRow(row, prefix + "_preparation_time", Integer.class));
        entity.setBasePrice(converter.fromRow(row, prefix + "_base_price", BigDecimal.class));
        entity.setImageUrl(converter.fromRow(row, prefix + "_image_url", String.class));
        entity.setIsAvailable(converter.fromRow(row, prefix + "_is_available", Boolean.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setDisplayOrder(converter.fromRow(row, prefix + "_display_order", Integer.class));
        entity.setBranchId(converter.fromRow(row, prefix + "_branch_id", UUID.class));
        entity.setMenuCategoryId(converter.fromRow(row, prefix + "_menu_category_id", UUID.class));
        return entity;
    }
}
