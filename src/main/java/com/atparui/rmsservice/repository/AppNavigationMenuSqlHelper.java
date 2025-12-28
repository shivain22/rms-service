package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AppNavigationMenuSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("menu_code", table, columnPrefix + "_menu_code"));
        columns.add(Column.aliased("menu_name", table, columnPrefix + "_menu_name"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("menu_type", table, columnPrefix + "_menu_type"));
        columns.add(Column.aliased("icon", table, columnPrefix + "_icon"));
        columns.add(Column.aliased("route_path", table, columnPrefix + "_route_path"));
        columns.add(Column.aliased("display_order", table, columnPrefix + "_display_order"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        return columns;
    }
}
