package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class OrderSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("order_number", table, columnPrefix + "_order_number"));
        columns.add(Column.aliased("order_type", table, columnPrefix + "_order_type"));
        columns.add(Column.aliased("order_source", table, columnPrefix + "_order_source"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("order_date", table, columnPrefix + "_order_date"));
        columns.add(Column.aliased("estimated_ready_time", table, columnPrefix + "_estimated_ready_time"));
        columns.add(Column.aliased("actual_ready_time", table, columnPrefix + "_actual_ready_time"));
        columns.add(Column.aliased("special_instructions", table, columnPrefix + "_special_instructions"));
        columns.add(Column.aliased("subtotal", table, columnPrefix + "_subtotal"));
        columns.add(Column.aliased("tax_amount", table, columnPrefix + "_tax_amount"));
        columns.add(Column.aliased("discount_amount", table, columnPrefix + "_discount_amount"));
        columns.add(Column.aliased("total_amount", table, columnPrefix + "_total_amount"));
        columns.add(Column.aliased("is_paid", table, columnPrefix + "_is_paid"));
        columns.add(Column.aliased("cancelled_at", table, columnPrefix + "_cancelled_at"));
        columns.add(Column.aliased("cancelled_by", table, columnPrefix + "_cancelled_by"));
        columns.add(Column.aliased("cancellation_reason", table, columnPrefix + "_cancellation_reason"));

        columns.add(Column.aliased("branch_id", table, columnPrefix + "_branch_id"));
        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("branch_table_id", table, columnPrefix + "_branch_table_id"));
        return columns;
    }
}
