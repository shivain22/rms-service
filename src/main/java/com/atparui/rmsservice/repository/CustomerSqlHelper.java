package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CustomerSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("customer_code", table, columnPrefix + "_customer_code"));
        columns.add(Column.aliased("phone", table, columnPrefix + "_phone"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));
        columns.add(Column.aliased("first_name", table, columnPrefix + "_first_name"));
        columns.add(Column.aliased("last_name", table, columnPrefix + "_last_name"));
        columns.add(Column.aliased("date_of_birth", table, columnPrefix + "_date_of_birth"));
        columns.add(Column.aliased("address_line_1", table, columnPrefix + "_address_line_1"));
        columns.add(Column.aliased("address_line_2", table, columnPrefix + "_address_line_2"));
        columns.add(Column.aliased("city", table, columnPrefix + "_city"));
        columns.add(Column.aliased("state", table, columnPrefix + "_state"));
        columns.add(Column.aliased("country", table, columnPrefix + "_country"));
        columns.add(Column.aliased("postal_code", table, columnPrefix + "_postal_code"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
