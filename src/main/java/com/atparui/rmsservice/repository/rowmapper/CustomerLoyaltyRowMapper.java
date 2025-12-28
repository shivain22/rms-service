package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.CustomerLoyalty;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CustomerLoyalty}, with proper type conversions.
 */
@Service
public class CustomerLoyaltyRowMapper implements BiFunction<Row, String, CustomerLoyalty> {

    private final ColumnConverter converter;

    public CustomerLoyaltyRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CustomerLoyalty} stored in the database.
     */
    @Override
    public CustomerLoyalty apply(Row row, String prefix) {
        CustomerLoyalty entity = new CustomerLoyalty();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setLoyaltyPoints(converter.fromRow(row, prefix + "_loyalty_points", BigDecimal.class));
        entity.setTier(converter.fromRow(row, prefix + "_tier", String.class));
        entity.setEnrolledAt(converter.fromRow(row, prefix + "_enrolled_at", Instant.class));
        entity.setLastPointsEarnedAt(converter.fromRow(row, prefix + "_last_points_earned_at", Instant.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", UUID.class));
        entity.setRestaurantId(converter.fromRow(row, prefix + "_restaurant_id", UUID.class));
        return entity;
    }
}
