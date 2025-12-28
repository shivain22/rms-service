package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.BillItem;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link BillItem}, with proper type conversions.
 */
@Service
public class BillItemRowMapper implements BiFunction<Row, String, BillItem> {

    private final ColumnConverter converter;

    public BillItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BillItem} stored in the database.
     */
    @Override
    public BillItem apply(Row row, String prefix) {
        BillItem entity = new BillItem();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setItemName(converter.fromRow(row, prefix + "_item_name", String.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setUnitPrice(converter.fromRow(row, prefix + "_unit_price", BigDecimal.class));
        entity.setItemTotal(converter.fromRow(row, prefix + "_item_total", BigDecimal.class));
        entity.setBillId(converter.fromRow(row, prefix + "_bill_id", UUID.class));
        entity.setOrderItemId(converter.fromRow(row, prefix + "_order_item_id", UUID.class));
        return entity;
    }
}
