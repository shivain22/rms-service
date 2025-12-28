package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class UserSyncLogSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("sync_type", table, columnPrefix + "_sync_type"));
        columns.add(Column.aliased("sync_status", table, columnPrefix + "_sync_status"));
        columns.add(Column.aliased("external_user_id", table, columnPrefix + "_external_user_id"));
        columns.add(Column.aliased("request_payload", table, columnPrefix + "_request_payload"));
        columns.add(Column.aliased("response_payload", table, columnPrefix + "_response_payload"));
        columns.add(Column.aliased("error_message", table, columnPrefix + "_error_message"));
        columns.add(Column.aliased("synced_at", table, columnPrefix + "_synced_at"));
        columns.add(Column.aliased("synced_by", table, columnPrefix + "_synced_by"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
