package com.atparui.rmsservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BillTaxSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("tax_name", table, columnPrefix + "_tax_name"));
        columns.add(Column.aliased("tax_rate", table, columnPrefix + "_tax_rate"));
        columns.add(Column.aliased("taxable_amount", table, columnPrefix + "_taxable_amount"));
        columns.add(Column.aliased("tax_amount", table, columnPrefix + "_tax_amount"));

        columns.add(Column.aliased("bill_id", table, columnPrefix + "_bill_id"));
        columns.add(Column.aliased("tax_config_id", table, columnPrefix + "_tax_config_id"));
        return columns;
    }
}
