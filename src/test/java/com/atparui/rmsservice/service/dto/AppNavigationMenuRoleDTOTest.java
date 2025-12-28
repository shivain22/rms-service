package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AppNavigationMenuRoleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppNavigationMenuRoleDTO.class);
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO1 = new AppNavigationMenuRoleDTO();
        appNavigationMenuRoleDTO1.setId(UUID.randomUUID());
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO2 = new AppNavigationMenuRoleDTO();
        assertThat(appNavigationMenuRoleDTO1).isNotEqualTo(appNavigationMenuRoleDTO2);
        appNavigationMenuRoleDTO2.setId(appNavigationMenuRoleDTO1.getId());
        assertThat(appNavigationMenuRoleDTO1).isEqualTo(appNavigationMenuRoleDTO2);
        appNavigationMenuRoleDTO2.setId(UUID.randomUUID());
        assertThat(appNavigationMenuRoleDTO1).isNotEqualTo(appNavigationMenuRoleDTO2);
        appNavigationMenuRoleDTO1.setId(null);
        assertThat(appNavigationMenuRoleDTO1).isNotEqualTo(appNavigationMenuRoleDTO2);
    }
}
