package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.MenuItemVariant;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MenuItemVariant}, with proper type conversions.
 */
@Service
public class MenuItemVariantRowMapper implements BiFunction<Row, String, MenuItemVariant> {

    private final ColumnConverter converter;

    public MenuItemVariantRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MenuItemVariant} stored in the database.
     */
    @Override
    public MenuItemVariant apply(Row row, String prefix) {
        MenuItemVariant entity = new MenuItemVariant();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setVariantName(converter.fromRow(row, prefix + "_variant_name", String.class));
        entity.setVariantCode(converter.fromRow(row, prefix + "_variant_code", String.class));
        entity.setPriceModifier(converter.fromRow(row, prefix + "_price_modifier", BigDecimal.class));
        entity.setIsDefault(converter.fromRow(row, prefix + "_is_default", Boolean.class));
        entity.setDisplayOrder(converter.fromRow(row, prefix + "_display_order", Integer.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setMenuItemId(converter.fromRow(row, prefix + "_menu_item_id", UUID.class));
        return entity;
    }
}
