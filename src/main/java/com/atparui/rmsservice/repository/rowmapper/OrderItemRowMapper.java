package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.OrderItem;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OrderItem}, with proper type conversions.
 */
@Service
public class OrderItemRowMapper implements BiFunction<Row, String, OrderItem> {

    private final ColumnConverter converter;

    public OrderItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OrderItem} stored in the database.
     */
    @Override
    public OrderItem apply(Row row, String prefix) {
        OrderItem entity = new OrderItem();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setUnitPrice(converter.fromRow(row, prefix + "_unit_price", BigDecimal.class));
        entity.setItemTotal(converter.fromRow(row, prefix + "_item_total", BigDecimal.class));
        entity.setSpecialInstructions(converter.fromRow(row, prefix + "_special_instructions", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", UUID.class));
        entity.setMenuItemId(converter.fromRow(row, prefix + "_menu_item_id", UUID.class));
        entity.setMenuItemVariantId(converter.fromRow(row, prefix + "_menu_item_variant_id", UUID.class));
        return entity;
    }
}
