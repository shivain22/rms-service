package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.DiscountAsserts.*;
import static com.atparui.rmsservice.domain.DiscountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiscountMapperTest {

    private DiscountMapper discountMapper;

    @BeforeEach
    void setUp() {
        discountMapper = new DiscountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDiscountSample1();
        var actual = discountMapper.toEntity(discountMapper.toDto(expected));
        assertDiscountAllPropertiesEquals(expected, actual);
    }
}
