package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.BranchAsserts.*;
import static com.atparui.rmsservice.domain.BranchTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BranchMapperTest {

    private BranchMapper branchMapper;

    @BeforeEach
    void setUp() {
        branchMapper = new BranchMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBranchSample1();
        var actual = branchMapper.toEntity(branchMapper.toDto(expected));
        assertBranchAllPropertiesEquals(expected, actual);
    }
}
