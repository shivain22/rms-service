package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.MenuItemAddon;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MenuItemAddon}, with proper type conversions.
 */
@Service
public class MenuItemAddonRowMapper implements BiFunction<Row, String, MenuItemAddon> {

    private final ColumnConverter converter;

    public MenuItemAddonRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MenuItemAddon} stored in the database.
     */
    @Override
    public MenuItemAddon apply(Row row, String prefix) {
        MenuItemAddon entity = new MenuItemAddon();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setAddonName(converter.fromRow(row, prefix + "_addon_name", String.class));
        entity.setAddonCode(converter.fromRow(row, prefix + "_addon_code", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setMenuItemId(converter.fromRow(row, prefix + "_menu_item_id", UUID.class));
        return entity;
    }
}
