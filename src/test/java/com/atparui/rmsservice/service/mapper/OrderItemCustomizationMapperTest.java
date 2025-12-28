package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.OrderItemCustomizationAsserts.*;
import static com.atparui.rmsservice.domain.OrderItemCustomizationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderItemCustomizationMapperTest {

    private OrderItemCustomizationMapper orderItemCustomizationMapper;

    @BeforeEach
    void setUp() {
        orderItemCustomizationMapper = new OrderItemCustomizationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrderItemCustomizationSample1();
        var actual = orderItemCustomizationMapper.toEntity(orderItemCustomizationMapper.toDto(expected));
        assertOrderItemCustomizationAllPropertiesEquals(expected, actual);
    }
}
