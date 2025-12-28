package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class RmsUserSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("external_user_id", table, columnPrefix + "_external_user_id"));
        columns.add(Column.aliased("username", table, columnPrefix + "_username"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));
        columns.add(Column.aliased("phone", table, columnPrefix + "_phone"));
        columns.add(Column.aliased("first_name", table, columnPrefix + "_first_name"));
        columns.add(Column.aliased("last_name", table, columnPrefix + "_last_name"));
        columns.add(Column.aliased("display_name", table, columnPrefix + "_display_name"));
        columns.add(Column.aliased("profile_image_url", table, columnPrefix + "_profile_image_url"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));
        columns.add(Column.aliased("last_sync_at", table, columnPrefix + "_last_sync_at"));
        columns.add(Column.aliased("sync_status", table, columnPrefix + "_sync_status"));
        columns.add(Column.aliased("sync_error", table, columnPrefix + "_sync_error"));

        return columns;
    }
}
