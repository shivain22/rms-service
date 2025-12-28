package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MenuItemAddonDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemAddonDTO.class);
        MenuItemAddonDTO menuItemAddonDTO1 = new MenuItemAddonDTO();
        menuItemAddonDTO1.setId(UUID.randomUUID());
        MenuItemAddonDTO menuItemAddonDTO2 = new MenuItemAddonDTO();
        assertThat(menuItemAddonDTO1).isNotEqualTo(menuItemAddonDTO2);
        menuItemAddonDTO2.setId(menuItemAddonDTO1.getId());
        assertThat(menuItemAddonDTO1).isEqualTo(menuItemAddonDTO2);
        menuItemAddonDTO2.setId(UUID.randomUUID());
        assertThat(menuItemAddonDTO1).isNotEqualTo(menuItemAddonDTO2);
        menuItemAddonDTO1.setId(null);
        assertThat(menuItemAddonDTO1).isNotEqualTo(menuItemAddonDTO2);
    }
}
