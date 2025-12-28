package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.Bill;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Bill}, with proper type conversions.
 */
@Service
public class BillRowMapper implements BiFunction<Row, String, Bill> {

    private final ColumnConverter converter;

    public BillRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Bill} stored in the database.
     */
    @Override
    public Bill apply(Row row, String prefix) {
        Bill entity = new Bill();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setBillNumber(converter.fromRow(row, prefix + "_bill_number", String.class));
        entity.setBillDate(converter.fromRow(row, prefix + "_bill_date", Instant.class));
        entity.setSubtotal(converter.fromRow(row, prefix + "_subtotal", BigDecimal.class));
        entity.setTaxAmount(converter.fromRow(row, prefix + "_tax_amount", BigDecimal.class));
        entity.setDiscountAmount(converter.fromRow(row, prefix + "_discount_amount", BigDecimal.class));
        entity.setServiceCharge(converter.fromRow(row, prefix + "_service_charge", BigDecimal.class));
        entity.setTotalAmount(converter.fromRow(row, prefix + "_total_amount", BigDecimal.class));
        entity.setAmountPaid(converter.fromRow(row, prefix + "_amount_paid", BigDecimal.class));
        entity.setAmountDue(converter.fromRow(row, prefix + "_amount_due", BigDecimal.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setGeneratedBy(converter.fromRow(row, prefix + "_generated_by", String.class));
        entity.setNotes(converter.fromRow(row, prefix + "_notes", String.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", UUID.class));
        entity.setBranchId(converter.fromRow(row, prefix + "_branch_id", UUID.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", UUID.class));
        return entity;
    }
}
