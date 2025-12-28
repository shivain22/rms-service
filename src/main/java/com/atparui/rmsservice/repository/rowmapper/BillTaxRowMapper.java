package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.BillTax;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link BillTax}, with proper type conversions.
 */
@Service
public class BillTaxRowMapper implements BiFunction<Row, String, BillTax> {

    private final ColumnConverter converter;

    public BillTaxRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BillTax} stored in the database.
     */
    @Override
    public BillTax apply(Row row, String prefix) {
        BillTax entity = new BillTax();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setTaxName(converter.fromRow(row, prefix + "_tax_name", String.class));
        entity.setTaxRate(converter.fromRow(row, prefix + "_tax_rate", BigDecimal.class));
        entity.setTaxableAmount(converter.fromRow(row, prefix + "_taxable_amount", BigDecimal.class));
        entity.setTaxAmount(converter.fromRow(row, prefix + "_tax_amount", BigDecimal.class));
        entity.setBillId(converter.fromRow(row, prefix + "_bill_id", UUID.class));
        entity.setTaxConfigId(converter.fromRow(row, prefix + "_tax_config_id", UUID.class));
        return entity;
    }
}
