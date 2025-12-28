package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DiscountSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("discount_code", table, columnPrefix + "_discount_code"));
        columns.add(Column.aliased("discount_name", table, columnPrefix + "_discount_name"));
        columns.add(Column.aliased("discount_type", table, columnPrefix + "_discount_type"));
        columns.add(Column.aliased("discount_value", table, columnPrefix + "_discount_value"));
        columns.add(Column.aliased("min_order_amount", table, columnPrefix + "_min_order_amount"));
        columns.add(Column.aliased("max_discount_amount", table, columnPrefix + "_max_discount_amount"));
        columns.add(Column.aliased("applicable_to", table, columnPrefix + "_applicable_to"));
        columns.add(Column.aliased("valid_from", table, columnPrefix + "_valid_from"));
        columns.add(Column.aliased("valid_to", table, columnPrefix + "_valid_to"));
        columns.add(Column.aliased("max_uses", table, columnPrefix + "_max_uses"));
        columns.add(Column.aliased("current_uses", table, columnPrefix + "_current_uses"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("restaurant_id", table, columnPrefix + "_restaurant_id"));
        return columns;
    }
}
