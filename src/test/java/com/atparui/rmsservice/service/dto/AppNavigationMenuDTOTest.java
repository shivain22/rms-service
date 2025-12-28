package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AppNavigationMenuDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppNavigationMenuDTO.class);
        AppNavigationMenuDTO appNavigationMenuDTO1 = new AppNavigationMenuDTO();
        appNavigationMenuDTO1.setId(UUID.randomUUID());
        AppNavigationMenuDTO appNavigationMenuDTO2 = new AppNavigationMenuDTO();
        assertThat(appNavigationMenuDTO1).isNotEqualTo(appNavigationMenuDTO2);
        appNavigationMenuDTO2.setId(appNavigationMenuDTO1.getId());
        assertThat(appNavigationMenuDTO1).isEqualTo(appNavigationMenuDTO2);
        appNavigationMenuDTO2.setId(UUID.randomUUID());
        assertThat(appNavigationMenuDTO1).isNotEqualTo(appNavigationMenuDTO2);
        appNavigationMenuDTO1.setId(null);
        assertThat(appNavigationMenuDTO1).isNotEqualTo(appNavigationMenuDTO2);
    }
}
