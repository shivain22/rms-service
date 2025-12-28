package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MenuItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemDTO.class);
        MenuItemDTO menuItemDTO1 = new MenuItemDTO();
        menuItemDTO1.setId(UUID.randomUUID());
        MenuItemDTO menuItemDTO2 = new MenuItemDTO();
        assertThat(menuItemDTO1).isNotEqualTo(menuItemDTO2);
        menuItemDTO2.setId(menuItemDTO1.getId());
        assertThat(menuItemDTO1).isEqualTo(menuItemDTO2);
        menuItemDTO2.setId(UUID.randomUUID());
        assertThat(menuItemDTO1).isNotEqualTo(menuItemDTO2);
        menuItemDTO1.setId(null);
        assertThat(menuItemDTO1).isNotEqualTo(menuItemDTO2);
    }
}
