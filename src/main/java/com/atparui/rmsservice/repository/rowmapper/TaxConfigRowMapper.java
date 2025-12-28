package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.TaxConfig;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TaxConfig}, with proper type conversions.
 */
@Service
public class TaxConfigRowMapper implements BiFunction<Row, String, TaxConfig> {

    private final ColumnConverter converter;

    public TaxConfigRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TaxConfig} stored in the database.
     */
    @Override
    public TaxConfig apply(Row row, String prefix) {
        TaxConfig entity = new TaxConfig();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setTaxName(converter.fromRow(row, prefix + "_tax_name", String.class));
        entity.setTaxCode(converter.fromRow(row, prefix + "_tax_code", String.class));
        entity.setTaxRate(converter.fromRow(row, prefix + "_tax_rate", BigDecimal.class));
        entity.setTaxType(converter.fromRow(row, prefix + "_tax_type", String.class));
        entity.setIsApplicableToFood(converter.fromRow(row, prefix + "_is_applicable_to_food", Boolean.class));
        entity.setIsApplicableToBeverage(converter.fromRow(row, prefix + "_is_applicable_to_beverage", Boolean.class));
        entity.setIsApplicableToAlcohol(converter.fromRow(row, prefix + "_is_applicable_to_alcohol", Boolean.class));
        entity.setEffectiveFrom(converter.fromRow(row, prefix + "_effective_from", LocalDate.class));
        entity.setEffectiveTo(converter.fromRow(row, prefix + "_effective_to", LocalDate.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setRestaurantId(converter.fromRow(row, prefix + "_restaurant_id", UUID.class));
        return entity;
    }
}
