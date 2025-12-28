package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BillItemSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("item_name", table, columnPrefix + "_item_name"));
        columns.add(Column.aliased("quantity", table, columnPrefix + "_quantity"));
        columns.add(Column.aliased("unit_price", table, columnPrefix + "_unit_price"));
        columns.add(Column.aliased("item_total", table, columnPrefix + "_item_total"));

        columns.add(Column.aliased("bill_id", table, columnPrefix + "_bill_id"));
        columns.add(Column.aliased("order_item_id", table, columnPrefix + "_order_item_id"));
        return columns;
    }
}
