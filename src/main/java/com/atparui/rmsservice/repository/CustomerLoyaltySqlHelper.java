package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CustomerLoyaltySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("loyalty_points", table, columnPrefix + "_loyalty_points"));
        columns.add(Column.aliased("tier", table, columnPrefix + "_tier"));
        columns.add(Column.aliased("enrolled_at", table, columnPrefix + "_enrolled_at"));
        columns.add(Column.aliased("last_points_earned_at", table, columnPrefix + "_last_points_earned_at"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        columns.add(Column.aliased("restaurant_id", table, columnPrefix + "_restaurant_id"));
        return columns;
    }
}
