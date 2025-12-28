package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.RestaurantAsserts.*;
import static com.atparui.rmsservice.domain.RestaurantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestaurantMapperTest {

    private RestaurantMapper restaurantMapper;

    @BeforeEach
    void setUp() {
        restaurantMapper = new RestaurantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRestaurantSample1();
        var actual = restaurantMapper.toEntity(restaurantMapper.toDto(expected));
        assertRestaurantAllPropertiesEquals(expected, actual);
    }
}
