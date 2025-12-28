package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserBranchRoleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserBranchRoleDTO.class);
        UserBranchRoleDTO userBranchRoleDTO1 = new UserBranchRoleDTO();
        userBranchRoleDTO1.setId(UUID.randomUUID());
        UserBranchRoleDTO userBranchRoleDTO2 = new UserBranchRoleDTO();
        assertThat(userBranchRoleDTO1).isNotEqualTo(userBranchRoleDTO2);
        userBranchRoleDTO2.setId(userBranchRoleDTO1.getId());
        assertThat(userBranchRoleDTO1).isEqualTo(userBranchRoleDTO2);
        userBranchRoleDTO2.setId(UUID.randomUUID());
        assertThat(userBranchRoleDTO1).isNotEqualTo(userBranchRoleDTO2);
        userBranchRoleDTO1.setId(null);
        assertThat(userBranchRoleDTO1).isNotEqualTo(userBranchRoleDTO2);
    }
}
