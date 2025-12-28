package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.AppNavigationMenuItem;
import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link AppNavigationMenuItem}, with proper type conversions.
 */
@Service
public class AppNavigationMenuItemRowMapper implements BiFunction<Row, String, AppNavigationMenuItem> {

    private final ColumnConverter converter;

    public AppNavigationMenuItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AppNavigationMenuItem} stored in the database.
     */
    @Override
    public AppNavigationMenuItem apply(Row row, String prefix) {
        AppNavigationMenuItem entity = new AppNavigationMenuItem();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setItemCode(converter.fromRow(row, prefix + "_item_code", String.class));
        entity.setItemName(converter.fromRow(row, prefix + "_item_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setIcon(converter.fromRow(row, prefix + "_icon", String.class));
        entity.setRoutePath(converter.fromRow(row, prefix + "_route_path", String.class));
        entity.setComponentPath(converter.fromRow(row, prefix + "_component_path", String.class));
        entity.setDisplayOrder(converter.fromRow(row, prefix + "_display_order", Integer.class));
        entity.setBadgeText(converter.fromRow(row, prefix + "_badge_text", String.class));
        entity.setBadgeColor(converter.fromRow(row, prefix + "_badge_color", String.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setParentMenuId(converter.fromRow(row, prefix + "_parent_menu_id", UUID.class));
        return entity;
    }
}
