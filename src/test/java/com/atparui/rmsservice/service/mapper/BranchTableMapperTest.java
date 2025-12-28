package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.BranchTableAsserts.*;
import static com.atparui.rmsservice.domain.BranchTableTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BranchTableMapperTest {

    private BranchTableMapper branchTableMapper;

    @BeforeEach
    void setUp() {
        branchTableMapper = new BranchTableMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBranchTableSample1();
        var actual = branchTableMapper.toEntity(branchTableMapper.toDto(expected));
        assertBranchTableAllPropertiesEquals(expected, actual);
    }
}
