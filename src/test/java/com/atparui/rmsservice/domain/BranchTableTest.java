package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BranchTableTestSamples.*;
import static com.atparui.rmsservice.domain.BranchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BranchTableTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BranchTable.class);
        BranchTable branchTable1 = getBranchTableSample1();
        BranchTable branchTable2 = new BranchTable();
        assertThat(branchTable1).isNotEqualTo(branchTable2);

        branchTable2.setId(branchTable1.getId());
        assertThat(branchTable1).isEqualTo(branchTable2);

        branchTable2 = getBranchTableSample2();
        assertThat(branchTable1).isNotEqualTo(branchTable2);
    }

    @Test
    void branchTest() {
        BranchTable branchTable = getBranchTableRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        branchTable.setBranch(branchBack);
        assertThat(branchTable.getBranch()).isEqualTo(branchBack);

        branchTable.branch(null);
        assertThat(branchTable.getBranch()).isNull();
    }
}
