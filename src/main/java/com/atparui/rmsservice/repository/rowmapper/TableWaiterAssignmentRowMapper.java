package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.TableWaiterAssignment;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TableWaiterAssignment}, with proper type conversions.
 */
@Service
public class TableWaiterAssignmentRowMapper implements BiFunction<Row, String, TableWaiterAssignment> {

    private final ColumnConverter converter;

    public TableWaiterAssignmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TableWaiterAssignment} stored in the database.
     */
    @Override
    public TableWaiterAssignment apply(Row row, String prefix) {
        TableWaiterAssignment entity = new TableWaiterAssignment();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setAssignmentDate(converter.fromRow(row, prefix + "_assignment_date", LocalDate.class));
        entity.setStartTime(converter.fromRow(row, prefix + "_start_time", Instant.class));
        entity.setEndTime(converter.fromRow(row, prefix + "_end_time", Instant.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setTableAssignmentId(converter.fromRow(row, prefix + "_table_assignment_id", UUID.class));
        entity.setWaiterId(converter.fromRow(row, prefix + "_waiter_id", UUID.class));
        return entity;
    }
}
