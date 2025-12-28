package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MenuItemSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("item_type", table, columnPrefix + "_item_type"));
        columns.add(Column.aliased("cuisine_type", table, columnPrefix + "_cuisine_type"));
        columns.add(Column.aliased("is_vegetarian", table, columnPrefix + "_is_vegetarian"));
        columns.add(Column.aliased("is_vegan", table, columnPrefix + "_is_vegan"));
        columns.add(Column.aliased("is_alcoholic", table, columnPrefix + "_is_alcoholic"));
        columns.add(Column.aliased("spice_level", table, columnPrefix + "_spice_level"));
        columns.add(Column.aliased("preparation_time", table, columnPrefix + "_preparation_time"));
        columns.add(Column.aliased("base_price", table, columnPrefix + "_base_price"));
        columns.add(Column.aliased("image_url", table, columnPrefix + "_image_url"));
        columns.add(Column.aliased("is_available", table, columnPrefix + "_is_available"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));
        columns.add(Column.aliased("display_order", table, columnPrefix + "_display_order"));

        columns.add(Column.aliased("branch_id", table, columnPrefix + "_branch_id"));
        columns.add(Column.aliased("menu_category_id", table, columnPrefix + "_menu_category_id"));
        return columns;
    }
}
