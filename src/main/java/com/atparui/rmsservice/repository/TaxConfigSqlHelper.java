package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TaxConfigSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("tax_name", table, columnPrefix + "_tax_name"));
        columns.add(Column.aliased("tax_code", table, columnPrefix + "_tax_code"));
        columns.add(Column.aliased("tax_rate", table, columnPrefix + "_tax_rate"));
        columns.add(Column.aliased("tax_type", table, columnPrefix + "_tax_type"));
        columns.add(Column.aliased("is_applicable_to_food", table, columnPrefix + "_is_applicable_to_food"));
        columns.add(Column.aliased("is_applicable_to_beverage", table, columnPrefix + "_is_applicable_to_beverage"));
        columns.add(Column.aliased("is_applicable_to_alcohol", table, columnPrefix + "_is_applicable_to_alcohol"));
        columns.add(Column.aliased("effective_from", table, columnPrefix + "_effective_from"));
        columns.add(Column.aliased("effective_to", table, columnPrefix + "_effective_to"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("restaurant_id", table, columnPrefix + "_restaurant_id"));
        return columns;
    }
}
