package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.AppNavigationMenuItemTestSamples.*;
import static com.atparui.rmsservice.domain.AppNavigationMenuTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppNavigationMenuItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppNavigationMenuItem.class);
        AppNavigationMenuItem appNavigationMenuItem1 = getAppNavigationMenuItemSample1();
        AppNavigationMenuItem appNavigationMenuItem2 = new AppNavigationMenuItem();
        assertThat(appNavigationMenuItem1).isNotEqualTo(appNavigationMenuItem2);

        appNavigationMenuItem2.setId(appNavigationMenuItem1.getId());
        assertThat(appNavigationMenuItem1).isEqualTo(appNavigationMenuItem2);

        appNavigationMenuItem2 = getAppNavigationMenuItemSample2();
        assertThat(appNavigationMenuItem1).isNotEqualTo(appNavigationMenuItem2);
    }

    @Test
    void parentMenuTest() {
        AppNavigationMenuItem appNavigationMenuItem = getAppNavigationMenuItemRandomSampleGenerator();
        AppNavigationMenu appNavigationMenuBack = getAppNavigationMenuRandomSampleGenerator();

        appNavigationMenuItem.setParentMenu(appNavigationMenuBack);
        assertThat(appNavigationMenuItem.getParentMenu()).isEqualTo(appNavigationMenuBack);

        appNavigationMenuItem.parentMenu(null);
        assertThat(appNavigationMenuItem.getParentMenu()).isNull();
    }
}
