package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.CustomerLoyaltyAsserts.*;
import static com.atparui.rmsservice.domain.CustomerLoyaltyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerLoyaltyMapperTest {

    private CustomerLoyaltyMapper customerLoyaltyMapper;

    @BeforeEach
    void setUp() {
        customerLoyaltyMapper = new CustomerLoyaltyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCustomerLoyaltySample1();
        var actual = customerLoyaltyMapper.toEntity(customerLoyaltyMapper.toDto(expected));
        assertCustomerLoyaltyAllPropertiesEquals(expected, actual);
    }
}
