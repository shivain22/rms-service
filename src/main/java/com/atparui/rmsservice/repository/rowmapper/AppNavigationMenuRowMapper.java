package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.AppNavigationMenu;
import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link AppNavigationMenu}, with proper type conversions.
 */
@Service
public class AppNavigationMenuRowMapper implements BiFunction<Row, String, AppNavigationMenu> {

    private final ColumnConverter converter;

    public AppNavigationMenuRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AppNavigationMenu} stored in the database.
     */
    @Override
    public AppNavigationMenu apply(Row row, String prefix) {
        AppNavigationMenu entity = new AppNavigationMenu();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setMenuCode(converter.fromRow(row, prefix + "_menu_code", String.class));
        entity.setMenuName(converter.fromRow(row, prefix + "_menu_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setMenuType(converter.fromRow(row, prefix + "_menu_type", String.class));
        entity.setIcon(converter.fromRow(row, prefix + "_icon", String.class));
        entity.setRoutePath(converter.fromRow(row, prefix + "_route_path", String.class));
        entity.setDisplayOrder(converter.fromRow(row, prefix + "_display_order", Integer.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        return entity;
    }
}
