package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BranchTestSamples.*;
import static com.atparui.rmsservice.domain.RmsUserTestSamples.*;
import static com.atparui.rmsservice.domain.UserBranchRoleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserBranchRoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserBranchRole.class);
        UserBranchRole userBranchRole1 = getUserBranchRoleSample1();
        UserBranchRole userBranchRole2 = new UserBranchRole();
        assertThat(userBranchRole1).isNotEqualTo(userBranchRole2);

        userBranchRole2.setId(userBranchRole1.getId());
        assertThat(userBranchRole1).isEqualTo(userBranchRole2);

        userBranchRole2 = getUserBranchRoleSample2();
        assertThat(userBranchRole1).isNotEqualTo(userBranchRole2);
    }

    @Test
    void userTest() {
        UserBranchRole userBranchRole = getUserBranchRoleRandomSampleGenerator();
        RmsUser rmsUserBack = getRmsUserRandomSampleGenerator();

        userBranchRole.setUser(rmsUserBack);
        assertThat(userBranchRole.getUser()).isEqualTo(rmsUserBack);

        userBranchRole.user(null);
        assertThat(userBranchRole.getUser()).isNull();
    }

    @Test
    void branchTest() {
        UserBranchRole userBranchRole = getUserBranchRoleRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        userBranchRole.setBranch(branchBack);
        assertThat(userBranchRole.getBranch()).isEqualTo(branchBack);

        userBranchRole.branch(null);
        assertThat(userBranchRole.getBranch()).isNull();
    }
}
