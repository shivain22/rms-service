package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class UserBranchRoleSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("role", table, columnPrefix + "_role"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));
        columns.add(Column.aliased("assigned_at", table, columnPrefix + "_assigned_at"));
        columns.add(Column.aliased("assigned_by", table, columnPrefix + "_assigned_by"));
        columns.add(Column.aliased("revoked_at", table, columnPrefix + "_revoked_at"));
        columns.add(Column.aliased("revoked_by", table, columnPrefix + "_revoked_by"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("branch_id", table, columnPrefix + "_branch_id"));
        return columns;
    }
}
