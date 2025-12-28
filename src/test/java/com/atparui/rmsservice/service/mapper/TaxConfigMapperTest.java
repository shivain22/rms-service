package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.TaxConfigAsserts.*;
import static com.atparui.rmsservice.domain.TaxConfigTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaxConfigMapperTest {

    private TaxConfigMapper taxConfigMapper;

    @BeforeEach
    void setUp() {
        taxConfigMapper = new TaxConfigMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTaxConfigSample1();
        var actual = taxConfigMapper.toEntity(taxConfigMapper.toDto(expected));
        assertTaxConfigAllPropertiesEquals(expected, actual);
    }
}
