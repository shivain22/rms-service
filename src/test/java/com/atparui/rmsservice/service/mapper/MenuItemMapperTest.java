package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.MenuItemAsserts.*;
import static com.atparui.rmsservice.domain.MenuItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuItemMapperTest {

    private MenuItemMapper menuItemMapper;

    @BeforeEach
    void setUp() {
        menuItemMapper = new MenuItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMenuItemSample1();
        var actual = menuItemMapper.toEntity(menuItemMapper.toDto(expected));
        assertMenuItemAllPropertiesEquals(expected, actual);
    }
}
