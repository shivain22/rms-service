package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.UserBranchRole;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserBranchRole}, with proper type conversions.
 */
@Service
public class UserBranchRoleRowMapper implements BiFunction<Row, String, UserBranchRole> {

    private final ColumnConverter converter;

    public UserBranchRoleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserBranchRole} stored in the database.
     */
    @Override
    public UserBranchRole apply(Row row, String prefix) {
        UserBranchRole entity = new UserBranchRole();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setRole(converter.fromRow(row, prefix + "_role", String.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setAssignedAt(converter.fromRow(row, prefix + "_assigned_at", Instant.class));
        entity.setAssignedBy(converter.fromRow(row, prefix + "_assigned_by", String.class));
        entity.setRevokedAt(converter.fromRow(row, prefix + "_revoked_at", Instant.class));
        entity.setRevokedBy(converter.fromRow(row, prefix + "_revoked_by", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", UUID.class));
        entity.setBranchId(converter.fromRow(row, prefix + "_branch_id", UUID.class));
        return entity;
    }
}
