package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.AppNavigationMenuItemAsserts.*;
import static com.atparui.rmsservice.domain.AppNavigationMenuItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppNavigationMenuItemMapperTest {

    private AppNavigationMenuItemMapper appNavigationMenuItemMapper;

    @BeforeEach
    void setUp() {
        appNavigationMenuItemMapper = new AppNavigationMenuItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAppNavigationMenuItemSample1();
        var actual = appNavigationMenuItemMapper.toEntity(appNavigationMenuItemMapper.toDto(expected));
        assertAppNavigationMenuItemAllPropertiesEquals(expected, actual);
    }
}
