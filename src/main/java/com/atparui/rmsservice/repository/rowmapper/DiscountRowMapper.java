package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.Discount;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Discount}, with proper type conversions.
 */
@Service
public class DiscountRowMapper implements BiFunction<Row, String, Discount> {

    private final ColumnConverter converter;

    public DiscountRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Discount} stored in the database.
     */
    @Override
    public Discount apply(Row row, String prefix) {
        Discount entity = new Discount();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setDiscountCode(converter.fromRow(row, prefix + "_discount_code", String.class));
        entity.setDiscountName(converter.fromRow(row, prefix + "_discount_name", String.class));
        entity.setDiscountType(converter.fromRow(row, prefix + "_discount_type", String.class));
        entity.setDiscountValue(converter.fromRow(row, prefix + "_discount_value", BigDecimal.class));
        entity.setMinOrderAmount(converter.fromRow(row, prefix + "_min_order_amount", BigDecimal.class));
        entity.setMaxDiscountAmount(converter.fromRow(row, prefix + "_max_discount_amount", BigDecimal.class));
        entity.setApplicableTo(converter.fromRow(row, prefix + "_applicable_to", String.class));
        entity.setValidFrom(converter.fromRow(row, prefix + "_valid_from", Instant.class));
        entity.setValidTo(converter.fromRow(row, prefix + "_valid_to", Instant.class));
        entity.setMaxUses(converter.fromRow(row, prefix + "_max_uses", Integer.class));
        entity.setCurrentUses(converter.fromRow(row, prefix + "_current_uses", Integer.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setRestaurantId(converter.fromRow(row, prefix + "_restaurant_id", UUID.class));
        return entity;
    }
}
