package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.MenuItemAddonTestSamples.*;
import static com.atparui.rmsservice.domain.MenuItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemAddonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItemAddon.class);
        MenuItemAddon menuItemAddon1 = getMenuItemAddonSample1();
        MenuItemAddon menuItemAddon2 = new MenuItemAddon();
        assertThat(menuItemAddon1).isNotEqualTo(menuItemAddon2);

        menuItemAddon2.setId(menuItemAddon1.getId());
        assertThat(menuItemAddon1).isEqualTo(menuItemAddon2);

        menuItemAddon2 = getMenuItemAddonSample2();
        assertThat(menuItemAddon1).isNotEqualTo(menuItemAddon2);
    }

    @Test
    void menuItemTest() {
        MenuItemAddon menuItemAddon = getMenuItemAddonRandomSampleGenerator();
        MenuItem menuItemBack = getMenuItemRandomSampleGenerator();

        menuItemAddon.setMenuItem(menuItemBack);
        assertThat(menuItemAddon.getMenuItem()).isEqualTo(menuItemBack);

        menuItemAddon.menuItem(null);
        assertThat(menuItemAddon.getMenuItem()).isNull();
    }
}
