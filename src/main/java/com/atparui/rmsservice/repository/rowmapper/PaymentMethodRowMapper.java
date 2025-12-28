package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.PaymentMethod;
import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PaymentMethod}, with proper type conversions.
 */
@Service
public class PaymentMethodRowMapper implements BiFunction<Row, String, PaymentMethod> {

    private final ColumnConverter converter;

    public PaymentMethodRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PaymentMethod} stored in the database.
     */
    @Override
    public PaymentMethod apply(Row row, String prefix) {
        PaymentMethod entity = new PaymentMethod();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setMethodCode(converter.fromRow(row, prefix + "_method_code", String.class));
        entity.setMethodName(converter.fromRow(row, prefix + "_method_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        return entity;
    }
}
