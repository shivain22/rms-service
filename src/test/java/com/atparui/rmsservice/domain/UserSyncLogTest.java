package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.RmsUserTestSamples.*;
import static com.atparui.rmsservice.domain.UserSyncLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserSyncLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSyncLog.class);
        UserSyncLog userSyncLog1 = getUserSyncLogSample1();
        UserSyncLog userSyncLog2 = new UserSyncLog();
        assertThat(userSyncLog1).isNotEqualTo(userSyncLog2);

        userSyncLog2.setId(userSyncLog1.getId());
        assertThat(userSyncLog1).isEqualTo(userSyncLog2);

        userSyncLog2 = getUserSyncLogSample2();
        assertThat(userSyncLog1).isNotEqualTo(userSyncLog2);
    }

    @Test
    void userTest() {
        UserSyncLog userSyncLog = getUserSyncLogRandomSampleGenerator();
        RmsUser rmsUserBack = getRmsUserRandomSampleGenerator();

        userSyncLog.setUser(rmsUserBack);
        assertThat(userSyncLog.getUser()).isEqualTo(rmsUserBack);

        userSyncLog.user(null);
        assertThat(userSyncLog.getUser()).isNull();
    }
}
