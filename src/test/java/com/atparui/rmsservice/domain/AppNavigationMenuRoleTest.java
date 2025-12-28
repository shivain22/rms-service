package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.AppNavigationMenuItemTestSamples.*;
import static com.atparui.rmsservice.domain.AppNavigationMenuRoleTestSamples.*;
import static com.atparui.rmsservice.domain.AppNavigationMenuTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppNavigationMenuRoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppNavigationMenuRole.class);
        AppNavigationMenuRole appNavigationMenuRole1 = getAppNavigationMenuRoleSample1();
        AppNavigationMenuRole appNavigationMenuRole2 = new AppNavigationMenuRole();
        assertThat(appNavigationMenuRole1).isNotEqualTo(appNavigationMenuRole2);

        appNavigationMenuRole2.setId(appNavigationMenuRole1.getId());
        assertThat(appNavigationMenuRole1).isEqualTo(appNavigationMenuRole2);

        appNavigationMenuRole2 = getAppNavigationMenuRoleSample2();
        assertThat(appNavigationMenuRole1).isNotEqualTo(appNavigationMenuRole2);
    }

    @Test
    void appNavigationMenuTest() {
        AppNavigationMenuRole appNavigationMenuRole = getAppNavigationMenuRoleRandomSampleGenerator();
        AppNavigationMenu appNavigationMenuBack = getAppNavigationMenuRandomSampleGenerator();

        appNavigationMenuRole.setAppNavigationMenu(appNavigationMenuBack);
        assertThat(appNavigationMenuRole.getAppNavigationMenu()).isEqualTo(appNavigationMenuBack);

        appNavigationMenuRole.appNavigationMenu(null);
        assertThat(appNavigationMenuRole.getAppNavigationMenu()).isNull();
    }

    @Test
    void appNavigationMenuItemTest() {
        AppNavigationMenuRole appNavigationMenuRole = getAppNavigationMenuRoleRandomSampleGenerator();
        AppNavigationMenuItem appNavigationMenuItemBack = getAppNavigationMenuItemRandomSampleGenerator();

        appNavigationMenuRole.setAppNavigationMenuItem(appNavigationMenuItemBack);
        assertThat(appNavigationMenuRole.getAppNavigationMenuItem()).isEqualTo(appNavigationMenuItemBack);

        appNavigationMenuRole.appNavigationMenuItem(null);
        assertThat(appNavigationMenuRole.getAppNavigationMenuItem()).isNull();
    }
}
