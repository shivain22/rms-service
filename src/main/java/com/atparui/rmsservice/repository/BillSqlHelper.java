package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BillSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("bill_number", table, columnPrefix + "_bill_number"));
        columns.add(Column.aliased("bill_date", table, columnPrefix + "_bill_date"));
        columns.add(Column.aliased("subtotal", table, columnPrefix + "_subtotal"));
        columns.add(Column.aliased("tax_amount", table, columnPrefix + "_tax_amount"));
        columns.add(Column.aliased("discount_amount", table, columnPrefix + "_discount_amount"));
        columns.add(Column.aliased("service_charge", table, columnPrefix + "_service_charge"));
        columns.add(Column.aliased("total_amount", table, columnPrefix + "_total_amount"));
        columns.add(Column.aliased("amount_paid", table, columnPrefix + "_amount_paid"));
        columns.add(Column.aliased("amount_due", table, columnPrefix + "_amount_due"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("generated_by", table, columnPrefix + "_generated_by"));
        columns.add(Column.aliased("notes", table, columnPrefix + "_notes"));

        columns.add(Column.aliased("order_id", table, columnPrefix + "_order_id"));
        columns.add(Column.aliased("branch_id", table, columnPrefix + "_branch_id"));
        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        return columns;
    }
}
