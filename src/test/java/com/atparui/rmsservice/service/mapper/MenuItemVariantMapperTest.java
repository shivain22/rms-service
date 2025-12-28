package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.MenuItemVariantAsserts.*;
import static com.atparui.rmsservice.domain.MenuItemVariantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuItemVariantMapperTest {

    private MenuItemVariantMapper menuItemVariantMapper;

    @BeforeEach
    void setUp() {
        menuItemVariantMapper = new MenuItemVariantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMenuItemVariantSample1();
        var actual = menuItemVariantMapper.toEntity(menuItemVariantMapper.toDto(expected));
        assertMenuItemVariantAllPropertiesEquals(expected, actual);
    }
}
