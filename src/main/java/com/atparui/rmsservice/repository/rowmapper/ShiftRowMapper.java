package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.Shift;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Shift}, with proper type conversions.
 */
@Service
public class ShiftRowMapper implements BiFunction<Row, String, Shift> {

    private final ColumnConverter converter;

    public ShiftRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Shift} stored in the database.
     */
    @Override
    public Shift apply(Row row, String prefix) {
        Shift entity = new Shift();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setShiftName(converter.fromRow(row, prefix + "_shift_name", String.class));
        entity.setStartTime(converter.fromRow(row, prefix + "_start_time", LocalTime.class));
        entity.setEndTime(converter.fromRow(row, prefix + "_end_time", LocalTime.class));
        entity.setShiftDate(converter.fromRow(row, prefix + "_shift_date", LocalDate.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setBranchId(converter.fromRow(row, prefix + "_branch_id", UUID.class));
        return entity;
    }
}
