package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.Order;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Order}, with proper type conversions.
 */
@Service
public class OrderRowMapper implements BiFunction<Row, String, Order> {

    private final ColumnConverter converter;

    public OrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Order} stored in the database.
     */
    @Override
    public Order apply(Row row, String prefix) {
        Order entity = new Order();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setOrderNumber(converter.fromRow(row, prefix + "_order_number", String.class));
        entity.setOrderType(converter.fromRow(row, prefix + "_order_type", String.class));
        entity.setOrderSource(converter.fromRow(row, prefix + "_order_source", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setOrderDate(converter.fromRow(row, prefix + "_order_date", Instant.class));
        entity.setEstimatedReadyTime(converter.fromRow(row, prefix + "_estimated_ready_time", Instant.class));
        entity.setActualReadyTime(converter.fromRow(row, prefix + "_actual_ready_time", Instant.class));
        entity.setSpecialInstructions(converter.fromRow(row, prefix + "_special_instructions", String.class));
        entity.setSubtotal(converter.fromRow(row, prefix + "_subtotal", BigDecimal.class));
        entity.setTaxAmount(converter.fromRow(row, prefix + "_tax_amount", BigDecimal.class));
        entity.setDiscountAmount(converter.fromRow(row, prefix + "_discount_amount", BigDecimal.class));
        entity.setTotalAmount(converter.fromRow(row, prefix + "_total_amount", BigDecimal.class));
        entity.setIsPaid(converter.fromRow(row, prefix + "_is_paid", Boolean.class));
        entity.setCancelledAt(converter.fromRow(row, prefix + "_cancelled_at", Instant.class));
        entity.setCancelledBy(converter.fromRow(row, prefix + "_cancelled_by", String.class));
        entity.setCancellationReason(converter.fromRow(row, prefix + "_cancellation_reason", String.class));
        entity.setBranchId(converter.fromRow(row, prefix + "_branch_id", UUID.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", UUID.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", UUID.class));
        entity.setBranchTableId(converter.fromRow(row, prefix + "_branch_table_id", UUID.class));
        return entity;
    }
}
