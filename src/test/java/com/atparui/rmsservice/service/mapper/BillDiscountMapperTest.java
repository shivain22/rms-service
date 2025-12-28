package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.BillDiscountAsserts.*;
import static com.atparui.rmsservice.domain.BillDiscountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BillDiscountMapperTest {

    private BillDiscountMapper billDiscountMapper;

    @BeforeEach
    void setUp() {
        billDiscountMapper = new BillDiscountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBillDiscountSample1();
        var actual = billDiscountMapper.toEntity(billDiscountMapper.toDto(expected));
        assertBillDiscountAllPropertiesEquals(expected, actual);
    }
}
