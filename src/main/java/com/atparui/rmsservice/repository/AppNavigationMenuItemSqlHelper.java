package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AppNavigationMenuItemSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("item_code", table, columnPrefix + "_item_code"));
        columns.add(Column.aliased("item_name", table, columnPrefix + "_item_name"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("icon", table, columnPrefix + "_icon"));
        columns.add(Column.aliased("route_path", table, columnPrefix + "_route_path"));
        columns.add(Column.aliased("component_path", table, columnPrefix + "_component_path"));
        columns.add(Column.aliased("display_order", table, columnPrefix + "_display_order"));
        columns.add(Column.aliased("badge_text", table, columnPrefix + "_badge_text"));
        columns.add(Column.aliased("badge_color", table, columnPrefix + "_badge_color"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("parent_menu_id", table, columnPrefix + "_parent_menu_id"));
        return columns;
    }
}
