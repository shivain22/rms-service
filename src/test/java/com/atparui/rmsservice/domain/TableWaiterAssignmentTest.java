package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.RmsUserTestSamples.*;
import static com.atparui.rmsservice.domain.TableAssignmentTestSamples.*;
import static com.atparui.rmsservice.domain.TableWaiterAssignmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TableWaiterAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TableWaiterAssignment.class);
        TableWaiterAssignment tableWaiterAssignment1 = getTableWaiterAssignmentSample1();
        TableWaiterAssignment tableWaiterAssignment2 = new TableWaiterAssignment();
        assertThat(tableWaiterAssignment1).isNotEqualTo(tableWaiterAssignment2);

        tableWaiterAssignment2.setId(tableWaiterAssignment1.getId());
        assertThat(tableWaiterAssignment1).isEqualTo(tableWaiterAssignment2);

        tableWaiterAssignment2 = getTableWaiterAssignmentSample2();
        assertThat(tableWaiterAssignment1).isNotEqualTo(tableWaiterAssignment2);
    }

    @Test
    void tableAssignmentTest() {
        TableWaiterAssignment tableWaiterAssignment = getTableWaiterAssignmentRandomSampleGenerator();
        TableAssignment tableAssignmentBack = getTableAssignmentRandomSampleGenerator();

        tableWaiterAssignment.setTableAssignment(tableAssignmentBack);
        assertThat(tableWaiterAssignment.getTableAssignment()).isEqualTo(tableAssignmentBack);

        tableWaiterAssignment.tableAssignment(null);
        assertThat(tableWaiterAssignment.getTableAssignment()).isNull();
    }

    @Test
    void waiterTest() {
        TableWaiterAssignment tableWaiterAssignment = getTableWaiterAssignmentRandomSampleGenerator();
        RmsUser rmsUserBack = getRmsUserRandomSampleGenerator();

        tableWaiterAssignment.setWaiter(rmsUserBack);
        assertThat(tableWaiterAssignment.getWaiter()).isEqualTo(rmsUserBack);

        tableWaiterAssignment.waiter(null);
        assertThat(tableWaiterAssignment.getWaiter()).isNull();
    }
}
