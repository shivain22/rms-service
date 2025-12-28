package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class OrderStatusHistorySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("previous_status", table, columnPrefix + "_previous_status"));
        columns.add(Column.aliased("new_status", table, columnPrefix + "_new_status"));
        columns.add(Column.aliased("changed_at", table, columnPrefix + "_changed_at"));
        columns.add(Column.aliased("changed_by", table, columnPrefix + "_changed_by"));
        columns.add(Column.aliased("notes", table, columnPrefix + "_notes"));

        columns.add(Column.aliased("order_id", table, columnPrefix + "_order_id"));
        return columns;
    }
}
