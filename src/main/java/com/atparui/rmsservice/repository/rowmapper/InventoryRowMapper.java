package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.Inventory;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Inventory}, with proper type conversions.
 */
@Service
public class InventoryRowMapper implements BiFunction<Row, String, Inventory> {

    private final ColumnConverter converter;

    public InventoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Inventory} stored in the database.
     */
    @Override
    public Inventory apply(Row row, String prefix) {
        Inventory entity = new Inventory();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setCurrentStock(converter.fromRow(row, prefix + "_current_stock", BigDecimal.class));
        entity.setUnit(converter.fromRow(row, prefix + "_unit", String.class));
        entity.setMinStockLevel(converter.fromRow(row, prefix + "_min_stock_level", BigDecimal.class));
        entity.setMaxStockLevel(converter.fromRow(row, prefix + "_max_stock_level", BigDecimal.class));
        entity.setLastUpdatedAt(converter.fromRow(row, prefix + "_last_updated_at", Instant.class));
        entity.setLastUpdatedBy(converter.fromRow(row, prefix + "_last_updated_by", String.class));
        entity.setBranchId(converter.fromRow(row, prefix + "_branch_id", UUID.class));
        entity.setMenuItemId(converter.fromRow(row, prefix + "_menu_item_id", UUID.class));
        return entity;
    }
}
