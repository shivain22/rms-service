package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.MenuCategory;
import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MenuCategory}, with proper type conversions.
 */
@Service
public class MenuCategoryRowMapper implements BiFunction<Row, String, MenuCategory> {

    private final ColumnConverter converter;

    public MenuCategoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MenuCategory} stored in the database.
     */
    @Override
    public MenuCategory apply(Row row, String prefix) {
        MenuCategory entity = new MenuCategory();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setDisplayOrder(converter.fromRow(row, prefix + "_display_order", Integer.class));
        entity.setImageUrl(converter.fromRow(row, prefix + "_image_url", String.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setRestaurantId(converter.fromRow(row, prefix + "_restaurant_id", UUID.class));
        return entity;
    }
}
