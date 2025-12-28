package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BranchTableSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("table_number", table, columnPrefix + "_table_number"));
        columns.add(Column.aliased("table_name", table, columnPrefix + "_table_name"));
        columns.add(Column.aliased("capacity", table, columnPrefix + "_capacity"));
        columns.add(Column.aliased("floor", table, columnPrefix + "_floor"));
        columns.add(Column.aliased("section", table, columnPrefix + "_section"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("qr_code", table, columnPrefix + "_qr_code"));
        columns.add(Column.aliased("qr_code_url", table, columnPrefix + "_qr_code_url"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("branch_id", table, columnPrefix + "_branch_id"));
        return columns;
    }
}
