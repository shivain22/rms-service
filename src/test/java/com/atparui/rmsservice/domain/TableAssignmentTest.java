package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BranchTableTestSamples.*;
import static com.atparui.rmsservice.domain.RmsUserTestSamples.*;
import static com.atparui.rmsservice.domain.ShiftTestSamples.*;
import static com.atparui.rmsservice.domain.TableAssignmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TableAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TableAssignment.class);
        TableAssignment tableAssignment1 = getTableAssignmentSample1();
        TableAssignment tableAssignment2 = new TableAssignment();
        assertThat(tableAssignment1).isNotEqualTo(tableAssignment2);

        tableAssignment2.setId(tableAssignment1.getId());
        assertThat(tableAssignment1).isEqualTo(tableAssignment2);

        tableAssignment2 = getTableAssignmentSample2();
        assertThat(tableAssignment1).isNotEqualTo(tableAssignment2);
    }

    @Test
    void branchTableTest() {
        TableAssignment tableAssignment = getTableAssignmentRandomSampleGenerator();
        BranchTable branchTableBack = getBranchTableRandomSampleGenerator();

        tableAssignment.setBranchTable(branchTableBack);
        assertThat(tableAssignment.getBranchTable()).isEqualTo(branchTableBack);

        tableAssignment.branchTable(null);
        assertThat(tableAssignment.getBranchTable()).isNull();
    }

    @Test
    void shiftTest() {
        TableAssignment tableAssignment = getTableAssignmentRandomSampleGenerator();
        Shift shiftBack = getShiftRandomSampleGenerator();

        tableAssignment.setShift(shiftBack);
        assertThat(tableAssignment.getShift()).isEqualTo(shiftBack);

        tableAssignment.shift(null);
        assertThat(tableAssignment.getShift()).isNull();
    }

    @Test
    void supervisorTest() {
        TableAssignment tableAssignment = getTableAssignmentRandomSampleGenerator();
        RmsUser rmsUserBack = getRmsUserRandomSampleGenerator();

        tableAssignment.setSupervisor(rmsUserBack);
        assertThat(tableAssignment.getSupervisor()).isEqualTo(rmsUserBack);

        tableAssignment.supervisor(null);
        assertThat(tableAssignment.getSupervisor()).isNull();
    }
}
