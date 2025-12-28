package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.RmsUser;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RmsUser}, with proper type conversions.
 */
@Service
public class RmsUserRowMapper implements BiFunction<Row, String, RmsUser> {

    private final ColumnConverter converter;

    public RmsUserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RmsUser} stored in the database.
     */
    @Override
    public RmsUser apply(Row row, String prefix) {
        RmsUser entity = new RmsUser();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setExternalUserId(converter.fromRow(row, prefix + "_external_user_id", String.class));
        entity.setUsername(converter.fromRow(row, prefix + "_username", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setDisplayName(converter.fromRow(row, prefix + "_display_name", String.class));
        entity.setProfileImageUrl(converter.fromRow(row, prefix + "_profile_image_url", String.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setLastSyncAt(converter.fromRow(row, prefix + "_last_sync_at", Instant.class));
        entity.setSyncStatus(converter.fromRow(row, prefix + "_sync_status", String.class));
        entity.setSyncError(converter.fromRow(row, prefix + "_sync_error", String.class));
        return entity;
    }
}
