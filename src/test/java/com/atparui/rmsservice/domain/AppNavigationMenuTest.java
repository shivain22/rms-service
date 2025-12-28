package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.AppNavigationMenuTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppNavigationMenuTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppNavigationMenu.class);
        AppNavigationMenu appNavigationMenu1 = getAppNavigationMenuSample1();
        AppNavigationMenu appNavigationMenu2 = new AppNavigationMenu();
        assertThat(appNavigationMenu1).isNotEqualTo(appNavigationMenu2);

        appNavigationMenu2.setId(appNavigationMenu1.getId());
        assertThat(appNavigationMenu1).isEqualTo(appNavigationMenu2);

        appNavigationMenu2 = getAppNavigationMenuSample2();
        assertThat(appNavigationMenu1).isNotEqualTo(appNavigationMenu2);
    }
}
