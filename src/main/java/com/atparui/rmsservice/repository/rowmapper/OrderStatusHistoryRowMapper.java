package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.OrderStatusHistory;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OrderStatusHistory}, with proper type conversions.
 */
@Service
public class OrderStatusHistoryRowMapper implements BiFunction<Row, String, OrderStatusHistory> {

    private final ColumnConverter converter;

    public OrderStatusHistoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OrderStatusHistory} stored in the database.
     */
    @Override
    public OrderStatusHistory apply(Row row, String prefix) {
        OrderStatusHistory entity = new OrderStatusHistory();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setPreviousStatus(converter.fromRow(row, prefix + "_previous_status", String.class));
        entity.setNewStatus(converter.fromRow(row, prefix + "_new_status", String.class));
        entity.setChangedAt(converter.fromRow(row, prefix + "_changed_at", Instant.class));
        entity.setChangedBy(converter.fromRow(row, prefix + "_changed_by", String.class));
        entity.setNotes(converter.fromRow(row, prefix + "_notes", String.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", UUID.class));
        return entity;
    }
}
