package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BranchTestSamples.*;
import static com.atparui.rmsservice.domain.ShiftTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShiftTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shift.class);
        Shift shift1 = getShiftSample1();
        Shift shift2 = new Shift();
        assertThat(shift1).isNotEqualTo(shift2);

        shift2.setId(shift1.getId());
        assertThat(shift1).isEqualTo(shift2);

        shift2 = getShiftSample2();
        assertThat(shift1).isNotEqualTo(shift2);
    }

    @Test
    void branchTest() {
        Shift shift = getShiftRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        shift.setBranch(branchBack);
        assertThat(shift.getBranch()).isEqualTo(branchBack);

        shift.branch(null);
        assertThat(shift.getBranch()).isNull();
    }
}
