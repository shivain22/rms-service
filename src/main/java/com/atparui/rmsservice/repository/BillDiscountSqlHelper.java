package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BillDiscountSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("discount_code", table, columnPrefix + "_discount_code"));
        columns.add(Column.aliased("discount_type", table, columnPrefix + "_discount_type"));
        columns.add(Column.aliased("discount_value", table, columnPrefix + "_discount_value"));
        columns.add(Column.aliased("discount_amount", table, columnPrefix + "_discount_amount"));

        columns.add(Column.aliased("bill_id", table, columnPrefix + "_bill_id"));
        columns.add(Column.aliased("discount_id", table, columnPrefix + "_discount_id"));
        return columns;
    }
}
