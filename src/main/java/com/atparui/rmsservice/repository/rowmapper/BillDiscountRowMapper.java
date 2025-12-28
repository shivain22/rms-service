package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.BillDiscount;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link BillDiscount}, with proper type conversions.
 */
@Service
public class BillDiscountRowMapper implements BiFunction<Row, String, BillDiscount> {

    private final ColumnConverter converter;

    public BillDiscountRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BillDiscount} stored in the database.
     */
    @Override
    public BillDiscount apply(Row row, String prefix) {
        BillDiscount entity = new BillDiscount();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setDiscountCode(converter.fromRow(row, prefix + "_discount_code", String.class));
        entity.setDiscountType(converter.fromRow(row, prefix + "_discount_type", String.class));
        entity.setDiscountValue(converter.fromRow(row, prefix + "_discount_value", BigDecimal.class));
        entity.setDiscountAmount(converter.fromRow(row, prefix + "_discount_amount", BigDecimal.class));
        entity.setBillId(converter.fromRow(row, prefix + "_bill_id", UUID.class));
        entity.setDiscountId(converter.fromRow(row, prefix + "_discount_id", UUID.class));
        return entity;
    }
}
