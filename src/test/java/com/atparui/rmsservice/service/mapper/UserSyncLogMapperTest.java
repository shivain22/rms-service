package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.UserSyncLogAsserts.*;
import static com.atparui.rmsservice.domain.UserSyncLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserSyncLogMapperTest {

    private UserSyncLogMapper userSyncLogMapper;

    @BeforeEach
    void setUp() {
        userSyncLogMapper = new UserSyncLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserSyncLogSample1();
        var actual = userSyncLogMapper.toEntity(userSyncLogMapper.toDto(expected));
        assertUserSyncLogAllPropertiesEquals(expected, actual);
    }
}
