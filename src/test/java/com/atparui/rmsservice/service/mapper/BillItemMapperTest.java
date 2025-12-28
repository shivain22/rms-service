package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.BillItemAsserts.*;
import static com.atparui.rmsservice.domain.BillItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BillItemMapperTest {

    private BillItemMapper billItemMapper;

    @BeforeEach
    void setUp() {
        billItemMapper = new BillItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBillItemSample1();
        var actual = billItemMapper.toEntity(billItemMapper.toDto(expected));
        assertBillItemAllPropertiesEquals(expected, actual);
    }
}
