package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TableAssignmentSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("assignment_date", table, columnPrefix + "_assignment_date"));
        columns.add(Column.aliased("start_time", table, columnPrefix + "_start_time"));
        columns.add(Column.aliased("end_time", table, columnPrefix + "_end_time"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("branch_table_id", table, columnPrefix + "_branch_table_id"));
        columns.add(Column.aliased("shift_id", table, columnPrefix + "_shift_id"));
        columns.add(Column.aliased("supervisor_id", table, columnPrefix + "_supervisor_id"));
        return columns;
    }
}
