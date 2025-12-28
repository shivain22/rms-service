package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MenuItemVariantSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("variant_name", table, columnPrefix + "_variant_name"));
        columns.add(Column.aliased("variant_code", table, columnPrefix + "_variant_code"));
        columns.add(Column.aliased("price_modifier", table, columnPrefix + "_price_modifier"));
        columns.add(Column.aliased("is_default", table, columnPrefix + "_is_default"));
        columns.add(Column.aliased("display_order", table, columnPrefix + "_display_order"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("menu_item_id", table, columnPrefix + "_menu_item_id"));
        return columns;
    }
}
