package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.RmsUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RmsUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RmsUser.class);
        RmsUser rmsUser1 = getRmsUserSample1();
        RmsUser rmsUser2 = new RmsUser();
        assertThat(rmsUser1).isNotEqualTo(rmsUser2);

        rmsUser2.setId(rmsUser1.getId());
        assertThat(rmsUser1).isEqualTo(rmsUser2);

        rmsUser2 = getRmsUserSample2();
        assertThat(rmsUser1).isNotEqualTo(rmsUser2);
    }
}
