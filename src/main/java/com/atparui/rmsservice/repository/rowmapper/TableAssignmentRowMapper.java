package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.TableAssignment;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TableAssignment}, with proper type conversions.
 */
@Service
public class TableAssignmentRowMapper implements BiFunction<Row, String, TableAssignment> {

    private final ColumnConverter converter;

    public TableAssignmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TableAssignment} stored in the database.
     */
    @Override
    public TableAssignment apply(Row row, String prefix) {
        TableAssignment entity = new TableAssignment();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setAssignmentDate(converter.fromRow(row, prefix + "_assignment_date", LocalDate.class));
        entity.setStartTime(converter.fromRow(row, prefix + "_start_time", Instant.class));
        entity.setEndTime(converter.fromRow(row, prefix + "_end_time", Instant.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setBranchTableId(converter.fromRow(row, prefix + "_branch_table_id", UUID.class));
        entity.setShiftId(converter.fromRow(row, prefix + "_shift_id", UUID.class));
        entity.setSupervisorId(converter.fromRow(row, prefix + "_supervisor_id", UUID.class));
        return entity;
    }
}
