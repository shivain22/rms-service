package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BranchTestSamples.*;
import static com.atparui.rmsservice.domain.MenuCategoryTestSamples.*;
import static com.atparui.rmsservice.domain.MenuItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuItem.class);
        MenuItem menuItem1 = getMenuItemSample1();
        MenuItem menuItem2 = new MenuItem();
        assertThat(menuItem1).isNotEqualTo(menuItem2);

        menuItem2.setId(menuItem1.getId());
        assertThat(menuItem1).isEqualTo(menuItem2);

        menuItem2 = getMenuItemSample2();
        assertThat(menuItem1).isNotEqualTo(menuItem2);
    }

    @Test
    void branchTest() {
        MenuItem menuItem = getMenuItemRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        menuItem.setBranch(branchBack);
        assertThat(menuItem.getBranch()).isEqualTo(branchBack);

        menuItem.branch(null);
        assertThat(menuItem.getBranch()).isNull();
    }

    @Test
    void menuCategoryTest() {
        MenuItem menuItem = getMenuItemRandomSampleGenerator();
        MenuCategory menuCategoryBack = getMenuCategoryRandomSampleGenerator();

        menuItem.setMenuCategory(menuCategoryBack);
        assertThat(menuItem.getMenuCategory()).isEqualTo(menuCategoryBack);

        menuItem.menuCategory(null);
        assertThat(menuItem.getMenuCategory()).isNull();
    }
}
