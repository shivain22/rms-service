package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PaymentSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("payment_number", table, columnPrefix + "_payment_number"));
        columns.add(Column.aliased("amount", table, columnPrefix + "_amount"));
        columns.add(Column.aliased("payment_date", table, columnPrefix + "_payment_date"));
        columns.add(Column.aliased("transaction_id", table, columnPrefix + "_transaction_id"));
        columns.add(Column.aliased("payment_gateway_response", table, columnPrefix + "_payment_gateway_response"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("processed_by", table, columnPrefix + "_processed_by"));
        columns.add(Column.aliased("notes", table, columnPrefix + "_notes"));
        columns.add(Column.aliased("refunded_at", table, columnPrefix + "_refunded_at"));
        columns.add(Column.aliased("refunded_by", table, columnPrefix + "_refunded_by"));
        columns.add(Column.aliased("refund_reason", table, columnPrefix + "_refund_reason"));

        columns.add(Column.aliased("bill_id", table, columnPrefix + "_bill_id"));
        columns.add(Column.aliased("payment_method_id", table, columnPrefix + "_payment_method_id"));
        return columns;
    }
}
