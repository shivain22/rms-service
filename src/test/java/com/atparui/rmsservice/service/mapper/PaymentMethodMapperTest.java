package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.PaymentMethodAsserts.*;
import static com.atparui.rmsservice.domain.PaymentMethodTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentMethodMapperTest {

    private PaymentMethodMapper paymentMethodMapper;

    @BeforeEach
    void setUp() {
        paymentMethodMapper = new PaymentMethodMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPaymentMethodSample1();
        var actual = paymentMethodMapper.toEntity(paymentMethodMapper.toDto(expected));
        assertPaymentMethodAllPropertiesEquals(expected, actual);
    }
}
