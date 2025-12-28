package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.AppNavigationMenuAsserts.*;
import static com.atparui.rmsservice.domain.AppNavigationMenuTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppNavigationMenuMapperTest {

    private AppNavigationMenuMapper appNavigationMenuMapper;

    @BeforeEach
    void setUp() {
        appNavigationMenuMapper = new AppNavigationMenuMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAppNavigationMenuSample1();
        var actual = appNavigationMenuMapper.toEntity(appNavigationMenuMapper.toDto(expected));
        assertAppNavigationMenuAllPropertiesEquals(expected, actual);
    }
}
