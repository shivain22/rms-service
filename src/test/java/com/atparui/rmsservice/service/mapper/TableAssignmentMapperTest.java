package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.TableAssignmentAsserts.*;
import static com.atparui.rmsservice.domain.TableAssignmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TableAssignmentMapperTest {

    private TableAssignmentMapper tableAssignmentMapper;

    @BeforeEach
    void setUp() {
        tableAssignmentMapper = new TableAssignmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTableAssignmentSample1();
        var actual = tableAssignmentMapper.toEntity(tableAssignmentMapper.toDto(expected));
        assertTableAssignmentAllPropertiesEquals(expected, actual);
    }
}
