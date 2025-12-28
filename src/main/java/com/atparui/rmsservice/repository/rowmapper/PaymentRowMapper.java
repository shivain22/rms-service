package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.Payment;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Payment}, with proper type conversions.
 */
@Service
public class PaymentRowMapper implements BiFunction<Row, String, Payment> {

    private final ColumnConverter converter;

    public PaymentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Payment} stored in the database.
     */
    @Override
    public Payment apply(Row row, String prefix) {
        Payment entity = new Payment();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setPaymentNumber(converter.fromRow(row, prefix + "_payment_number", String.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", BigDecimal.class));
        entity.setPaymentDate(converter.fromRow(row, prefix + "_payment_date", Instant.class));
        entity.setTransactionId(converter.fromRow(row, prefix + "_transaction_id", String.class));
        entity.setPaymentGatewayResponse(converter.fromRow(row, prefix + "_payment_gateway_response", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setProcessedBy(converter.fromRow(row, prefix + "_processed_by", String.class));
        entity.setNotes(converter.fromRow(row, prefix + "_notes", String.class));
        entity.setRefundedAt(converter.fromRow(row, prefix + "_refunded_at", Instant.class));
        entity.setRefundedBy(converter.fromRow(row, prefix + "_refunded_by", String.class));
        entity.setRefundReason(converter.fromRow(row, prefix + "_refund_reason", String.class));
        entity.setBillId(converter.fromRow(row, prefix + "_bill_id", UUID.class));
        entity.setPaymentMethodId(converter.fromRow(row, prefix + "_payment_method_id", UUID.class));
        return entity;
    }
}
