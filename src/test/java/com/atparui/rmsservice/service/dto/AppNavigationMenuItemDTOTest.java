package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AppNavigationMenuItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppNavigationMenuItemDTO.class);
        AppNavigationMenuItemDTO appNavigationMenuItemDTO1 = new AppNavigationMenuItemDTO();
        appNavigationMenuItemDTO1.setId(UUID.randomUUID());
        AppNavigationMenuItemDTO appNavigationMenuItemDTO2 = new AppNavigationMenuItemDTO();
        assertThat(appNavigationMenuItemDTO1).isNotEqualTo(appNavigationMenuItemDTO2);
        appNavigationMenuItemDTO2.setId(appNavigationMenuItemDTO1.getId());
        assertThat(appNavigationMenuItemDTO1).isEqualTo(appNavigationMenuItemDTO2);
        appNavigationMenuItemDTO2.setId(UUID.randomUUID());
        assertThat(appNavigationMenuItemDTO1).isNotEqualTo(appNavigationMenuItemDTO2);
        appNavigationMenuItemDTO1.setId(null);
        assertThat(appNavigationMenuItemDTO1).isNotEqualTo(appNavigationMenuItemDTO2);
    }
}
