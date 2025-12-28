package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.RmsUserAsserts.*;
import static com.atparui.rmsservice.domain.RmsUserTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RmsUserMapperTest {

    private RmsUserMapper rmsUserMapper;

    @BeforeEach
    void setUp() {
        rmsUserMapper = new RmsUserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRmsUserSample1();
        var actual = rmsUserMapper.toEntity(rmsUserMapper.toDto(expected));
        assertRmsUserAllPropertiesEquals(expected, actual);
    }
}
