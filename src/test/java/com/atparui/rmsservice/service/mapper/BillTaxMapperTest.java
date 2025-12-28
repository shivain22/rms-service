package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.BillTaxAsserts.*;
import static com.atparui.rmsservice.domain.BillTaxTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BillTaxMapperTest {

    private BillTaxMapper billTaxMapper;

    @BeforeEach
    void setUp() {
        billTaxMapper = new BillTaxMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBillTaxSample1();
        var actual = billTaxMapper.toEntity(billTaxMapper.toDto(expected));
        assertBillTaxAllPropertiesEquals(expected, actual);
    }
}
