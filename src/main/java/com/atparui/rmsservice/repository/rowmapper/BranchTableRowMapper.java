package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.BranchTable;
import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link BranchTable}, with proper type conversions.
 */
@Service
public class BranchTableRowMapper implements BiFunction<Row, String, BranchTable> {

    private final ColumnConverter converter;

    public BranchTableRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BranchTable} stored in the database.
     */
    @Override
    public BranchTable apply(Row row, String prefix) {
        BranchTable entity = new BranchTable();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setTableNumber(converter.fromRow(row, prefix + "_table_number", String.class));
        entity.setTableName(converter.fromRow(row, prefix + "_table_name", String.class));
        entity.setCapacity(converter.fromRow(row, prefix + "_capacity", Integer.class));
        entity.setFloor(converter.fromRow(row, prefix + "_floor", String.class));
        entity.setSection(converter.fromRow(row, prefix + "_section", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setQrCode(converter.fromRow(row, prefix + "_qr_code", String.class));
        entity.setQrCodeUrl(converter.fromRow(row, prefix + "_qr_code_url", String.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setBranchId(converter.fromRow(row, prefix + "_branch_id", UUID.class));
        return entity;
    }
}
