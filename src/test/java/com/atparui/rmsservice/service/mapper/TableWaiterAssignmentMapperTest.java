package com.atparui.rmsservice.service.mapper;

import static com.atparui.rmsservice.domain.TableWaiterAssignmentAsserts.*;
import static com.atparui.rmsservice.domain.TableWaiterAssignmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TableWaiterAssignmentMapperTest {

    private TableWaiterAssignmentMapper tableWaiterAssignmentMapper;

    @BeforeEach
    void setUp() {
        tableWaiterAssignmentMapper = new TableWaiterAssignmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTableWaiterAssignmentSample1();
        var actual = tableWaiterAssignmentMapper.toEntity(tableWaiterAssignmentMapper.toDto(expected));
        assertTableWaiterAssignmentAllPropertiesEquals(expected, actual);
    }
}
