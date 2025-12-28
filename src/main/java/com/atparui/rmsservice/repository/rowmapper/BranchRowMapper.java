package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.Branch;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Branch}, with proper type conversions.
 */
@Service
public class BranchRowMapper implements BiFunction<Row, String, Branch> {

    private final ColumnConverter converter;

    public BranchRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Branch} stored in the database.
     */
    @Override
    public Branch apply(Row row, String prefix) {
        Branch entity = new Branch();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setContactEmail(converter.fromRow(row, prefix + "_contact_email", String.class));
        entity.setContactPhone(converter.fromRow(row, prefix + "_contact_phone", String.class));
        entity.setAddressLine1(converter.fromRow(row, prefix + "_address_line_1", String.class));
        entity.setAddressLine2(converter.fromRow(row, prefix + "_address_line_2", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setState(converter.fromRow(row, prefix + "_state", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setPostalCode(converter.fromRow(row, prefix + "_postal_code", String.class));
        entity.setLatitude(converter.fromRow(row, prefix + "_latitude", BigDecimal.class));
        entity.setLongitude(converter.fromRow(row, prefix + "_longitude", BigDecimal.class));
        entity.setOpeningTime(converter.fromRow(row, prefix + "_opening_time", LocalTime.class));
        entity.setClosingTime(converter.fromRow(row, prefix + "_closing_time", LocalTime.class));
        entity.setTimezone(converter.fromRow(row, prefix + "_timezone", String.class));
        entity.setMaxCapacity(converter.fromRow(row, prefix + "_max_capacity", Integer.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setRestaurantId(converter.fromRow(row, prefix + "_restaurant_id", UUID.class));
        return entity;
    }
}
