package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MenuItemVariantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemVariantDTO.class);
        MenuItemVariantDTO menuItemVariantDTO1 = new MenuItemVariantDTO();
        menuItemVariantDTO1.setId(UUID.randomUUID());
        MenuItemVariantDTO menuItemVariantDTO2 = new MenuItemVariantDTO();
        assertThat(menuItemVariantDTO1).isNotEqualTo(menuItemVariantDTO2);
        menuItemVariantDTO2.setId(menuItemVariantDTO1.getId());
        assertThat(menuItemVariantDTO1).isEqualTo(menuItemVariantDTO2);
        menuItemVariantDTO2.setId(UUID.randomUUID());
        assertThat(menuItemVariantDTO1).isNotEqualTo(menuItemVariantDTO2);
        menuItemVariantDTO1.setId(null);
        assertThat(menuItemVariantDTO1).isNotEqualTo(menuItemVariantDTO2);
    }
}
