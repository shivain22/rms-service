package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.UserBranchRoleAsserts.*;
import static com.atparui.rmsservice.domain.UserBranchRoleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserBranchRoleMapperTest {

    private UserBranchRoleMapper userBranchRoleMapper;

    @BeforeEach
    void setUp() {
        userBranchRoleMapper = new UserBranchRoleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserBranchRoleSample1();
        var actual = userBranchRoleMapper.toEntity(userBranchRoleMapper.toDto(expected));
        assertUserBranchRoleAllPropertiesEquals(expected, actual);
    }
}
