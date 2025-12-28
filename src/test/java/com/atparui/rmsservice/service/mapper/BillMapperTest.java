package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.BillAsserts.*;
import static com.atparui.rmsservice.domain.BillTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BillMapperTest {

    private BillMapper billMapper;

    @BeforeEach
    void setUp() {
        billMapper = new BillMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBillSample1();
        var actual = billMapper.toEntity(billMapper.toDto(expected));
        assertBillAllPropertiesEquals(expected, actual);
    }
}
