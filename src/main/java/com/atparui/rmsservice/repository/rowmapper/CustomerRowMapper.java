package com.atparui.rmsservice.repository.rowmapper;

import com.atparui.rmsservice.domain.Customer;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Customer}, with proper type conversions.
 */
@Service
public class CustomerRowMapper implements BiFunction<Row, String, Customer> {

    private final ColumnConverter converter;

    public CustomerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Customer} stored in the database.
     */
    @Override
    public Customer apply(Row row, String prefix) {
        Customer entity = new Customer();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setCustomerCode(converter.fromRow(row, prefix + "_customer_code", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setDateOfBirth(converter.fromRow(row, prefix + "_date_of_birth", LocalDate.class));
        entity.setAddressLine1(converter.fromRow(row, prefix + "_address_line_1", String.class));
        entity.setAddressLine2(converter.fromRow(row, prefix + "_address_line_2", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setState(converter.fromRow(row, prefix + "_state", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setPostalCode(converter.fromRow(row, prefix + "_postal_code", String.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", UUID.class));
        return entity;
    }
}
