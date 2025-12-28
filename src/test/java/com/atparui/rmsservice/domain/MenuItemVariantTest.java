package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.MenuItemTestSamples.*;
import static com.atparui.rmsservice.domain.MenuItemVariantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemVariantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemVariant.class);
        MenuItemVariant menuItemVariant1 = getMenuItemVariantSample1();
        MenuItemVariant menuItemVariant2 = new MenuItemVariant();
        assertThat(menuItemVariant1).isNotEqualTo(menuItemVariant2);

        menuItemVariant2.setId(menuItemVariant1.getId());
        assertThat(menuItemVariant1).isEqualTo(menuItemVariant2);

        menuItemVariant2 = getMenuItemVariantSample2();
        assertThat(menuItemVariant1).isNotEqualTo(menuItemVariant2);
    }

    @Test
    void menuItemTest() {
        MenuItemVariant menuItemVariant = getMenuItemVariantRandomSampleGenerator();
        MenuItem menuItemBack = getMenuItemRandomSampleGenerator();

        menuItemVariant.setMenuItem(menuItemBack);
        assertThat(menuItemVariant.getMenuItem()).isEqualTo(menuItemBack);

        menuItemVariant.menuItem(null);
        assertThat(menuItemVariant.getMenuItem()).isNull();
    }
}
