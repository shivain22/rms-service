package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class InventorySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("current_stock", table, columnPrefix + "_current_stock"));
        columns.add(Column.aliased("unit", table, columnPrefix + "_unit"));
        columns.add(Column.aliased("min_stock_level", table, columnPrefix + "_min_stock_level"));
        columns.add(Column.aliased("max_stock_level", table, columnPrefix + "_max_stock_level"));
        columns.add(Column.aliased("last_updated_at", table, columnPrefix + "_last_updated_at"));
        columns.add(Column.aliased("last_updated_by", table, columnPrefix + "_last_updated_by"));

        columns.add(Column.aliased("branch_id", table, columnPrefix + "_branch_id"));
        columns.add(Column.aliased("menu_item_id", table, columnPrefix + "_menu_item_id"));
        return columns;
    }
}
