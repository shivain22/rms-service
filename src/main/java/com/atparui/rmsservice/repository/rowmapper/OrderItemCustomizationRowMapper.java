package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.OrderItemCustomization;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OrderItemCustomization}, with proper type conversions.
 */
@Service
public class OrderItemCustomizationRowMapper implements BiFunction<Row, String, OrderItemCustomization> {

    private final ColumnConverter converter;

    public OrderItemCustomizationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OrderItemCustomization} stored in the database.
     */
    @Override
    public OrderItemCustomization apply(Row row, String prefix) {
        OrderItemCustomization entity = new OrderItemCustomization();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setUnitPrice(converter.fromRow(row, prefix + "_unit_price", BigDecimal.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", BigDecimal.class));
        entity.setOrderItemId(converter.fromRow(row, prefix + "_order_item_id", UUID.class));
        entity.setMenuItemAddonId(converter.fromRow(row, prefix + "_menu_item_addon_id", UUID.class));
        return entity;
    }
}
