package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.AppNavigationMenuRole;
import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link AppNavigationMenuRole}, with proper type conversions.
 */
@Service
public class AppNavigationMenuRoleRowMapper implements BiFunction<Row, String, AppNavigationMenuRole> {

    private final ColumnConverter converter;

    public AppNavigationMenuRoleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AppNavigationMenuRole} stored in the database.
     */
    @Override
    public AppNavigationMenuRole apply(Row row, String prefix) {
        AppNavigationMenuRole entity = new AppNavigationMenuRole();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setRole(converter.fromRow(row, prefix + "_role", String.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setAppNavigationMenuId(converter.fromRow(row, prefix + "_app_navigation_menu_id", UUID.class));
        entity.setAppNavigationMenuItemId(converter.fromRow(row, prefix + "_app_navigation_menu_item_id", UUID.class));
        return entity;
    }
}
