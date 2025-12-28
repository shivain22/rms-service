package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.MenuCategoryAsserts.*;
import static com.atparui.rmsservice.domain.MenuCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuCategoryMapperTest {

    private MenuCategoryMapper menuCategoryMapper;

    @BeforeEach
    void setUp() {
        menuCategoryMapper = new MenuCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMenuCategorySample1();
        var actual = menuCategoryMapper.toEntity(menuCategoryMapper.toDto(expected));
        assertMenuCategoryAllPropertiesEquals(expected, actual);
    }
}
