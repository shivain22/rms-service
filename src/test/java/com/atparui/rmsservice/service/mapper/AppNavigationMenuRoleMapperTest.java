package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.AppNavigationMenuRoleAsserts.*;
import static com.atparui.rmsservice.domain.AppNavigationMenuRoleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppNavigationMenuRoleMapperTest {

    private AppNavigationMenuRoleMapper appNavigationMenuRoleMapper;

    @BeforeEach
    void setUp() {
        appNavigationMenuRoleMapper = new AppNavigationMenuRoleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAppNavigationMenuRoleSample1();
        var actual = appNavigationMenuRoleMapper.toEntity(appNavigationMenuRoleMapper.toDto(expected));
        assertAppNavigationMenuRoleAllPropertiesEquals(expected, actual);
    }
}
