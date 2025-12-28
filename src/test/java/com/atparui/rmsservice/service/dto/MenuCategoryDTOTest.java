package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MenuCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuCategoryDTO.class);
        MenuCategoryDTO menuCategoryDTO1 = new MenuCategoryDTO();
        menuCategoryDTO1.setId(UUID.randomUUID());
        MenuCategoryDTO menuCategoryDTO2 = new MenuCategoryDTO();
        assertThat(menuCategoryDTO1).isNotEqualTo(menuCategoryDTO2);
        menuCategoryDTO2.setId(menuCategoryDTO1.getId());
        assertThat(menuCategoryDTO1).isEqualTo(menuCategoryDTO2);
        menuCategoryDTO2.setId(UUID.randomUUID());
        assertThat(menuCategoryDTO1).isNotEqualTo(menuCategoryDTO2);
        menuCategoryDTO1.setId(null);
        assertThat(menuCategoryDTO1).isNotEqualTo(menuCategoryDTO2);
    }
}
