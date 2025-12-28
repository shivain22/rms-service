package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class OrderItemCustomizationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("quantity", table, columnPrefix + "_quantity"));
        columns.add(Column.aliased("unit_price", table, columnPrefix + "_unit_price"));
        columns.add(Column.aliased("total_price", table, columnPrefix + "_total_price"));

        columns.add(Column.aliased("order_item_id", table, columnPrefix + "_order_item_id"));
        columns.add(Column.aliased("menu_item_addon_id", table, columnPrefix + "_menu_item_addon_id"));
        return columns;
    }
}
