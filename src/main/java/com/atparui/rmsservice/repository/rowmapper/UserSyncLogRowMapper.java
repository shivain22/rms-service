package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.UserSyncLog;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserSyncLog}, with proper type conversions.
 */
@Service
public class UserSyncLogRowMapper implements BiFunction<Row, String, UserSyncLog> {

    private final ColumnConverter converter;

    public UserSyncLogRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserSyncLog} stored in the database.
     */
    @Override
    public UserSyncLog apply(Row row, String prefix) {
        UserSyncLog entity = new UserSyncLog();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setSyncType(converter.fromRow(row, prefix + "_sync_type", String.class));
        entity.setSyncStatus(converter.fromRow(row, prefix + "_sync_status", String.class));
        entity.setExternalUserId(converter.fromRow(row, prefix + "_external_user_id", String.class));
        entity.setRequestPayload(converter.fromRow(row, prefix + "_request_payload", String.class));
        entity.setResponsePayload(converter.fromRow(row, prefix + "_response_payload", String.class));
        entity.setErrorMessage(converter.fromRow(row, prefix + "_error_message", String.class));
        entity.setSyncedAt(converter.fromRow(row, prefix + "_synced_at", Instant.class));
        entity.setSyncedBy(converter.fromRow(row, prefix + "_synced_by", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", UUID.class));
        return entity;
    }
}
