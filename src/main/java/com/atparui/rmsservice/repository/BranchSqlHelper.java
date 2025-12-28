package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BranchSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("contact_email", table, columnPrefix + "_contact_email"));
        columns.add(Column.aliased("contact_phone", table, columnPrefix + "_contact_phone"));
        columns.add(Column.aliased("address_line_1", table, columnPrefix + "_address_line_1"));
        columns.add(Column.aliased("address_line_2", table, columnPrefix + "_address_line_2"));
        columns.add(Column.aliased("city", table, columnPrefix + "_city"));
        columns.add(Column.aliased("state", table, columnPrefix + "_state"));
        columns.add(Column.aliased("country", table, columnPrefix + "_country"));
        columns.add(Column.aliased("postal_code", table, columnPrefix + "_postal_code"));
        columns.add(Column.aliased("latitude", table, columnPrefix + "_latitude"));
        columns.add(Column.aliased("longitude", table, columnPrefix + "_longitude"));
        columns.add(Column.aliased("opening_time", table, columnPrefix + "_opening_time"));
        columns.add(Column.aliased("closing_time", table, columnPrefix + "_closing_time"));
        columns.add(Column.aliased("timezone", table, columnPrefix + "_timezone"));
        columns.add(Column.aliased("max_capacity", table, columnPrefix + "_max_capacity"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("restaurant_id", table, columnPrefix + "_restaurant_id"));
        return columns;
    }
}
