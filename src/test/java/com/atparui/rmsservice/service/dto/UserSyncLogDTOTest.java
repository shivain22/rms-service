package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserSyncLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSyncLogDTO.class);
        UserSyncLogDTO userSyncLogDTO1 = new UserSyncLogDTO();
        userSyncLogDTO1.setId(UUID.randomUUID());
        UserSyncLogDTO userSyncLogDTO2 = new UserSyncLogDTO();
        assertThat(userSyncLogDTO1).isNotEqualTo(userSyncLogDTO2);
        userSyncLogDTO2.setId(userSyncLogDTO1.getId());
        assertThat(userSyncLogDTO1).isEqualTo(userSyncLogDTO2);
        userSyncLogDTO2.setId(UUID.randomUUID());
        assertThat(userSyncLogDTO1).isNotEqualTo(userSyncLogDTO2);
        userSyncLogDTO1.setId(null);
        assertThat(userSyncLogDTO1).isNotEqualTo(userSyncLogDTO2);
    }
}
