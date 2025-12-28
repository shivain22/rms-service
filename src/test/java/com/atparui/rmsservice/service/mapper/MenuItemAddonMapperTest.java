package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.MenuItemAddonAsserts.*;
import static com.atparui.rmsservice.domain.MenuItemAddonTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuItemAddonMapperTest {

    private MenuItemAddonMapper menuItemAddonMapper;

    @BeforeEach
    void setUp() {
        menuItemAddonMapper = new MenuItemAddonMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMenuItemAddonSample1();
        var actual = menuItemAddonMapper.toEntity(menuItemAddonMapper.toDto(expected));
        assertMenuItemAddonAllPropertiesEquals(expected, actual);
    }
}
