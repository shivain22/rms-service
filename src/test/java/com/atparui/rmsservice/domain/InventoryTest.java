package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BranchTestSamples.*;
import static com.atparui.rmsservice.domain.InventoryTestSamples.*;
import static com.atparui.rmsservice.domain.MenuItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Inventory.class);
        Inventory inventory1 = getInventorySample1();
        Inventory inventory2 = new Inventory();
        assertThat(inventory1).isNotEqualTo(inventory2);

        inventory2.setId(inventory1.getId());
        assertThat(inventory1).isEqualTo(inventory2);

        inventory2 = getInventorySample2();
        assertThat(inventory1).isNotEqualTo(inventory2);
    }

    @Test
    void branchTest() {
        Inventory inventory = getInventoryRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        inventory.setBranch(branchBack);
        assertThat(inventory.getBranch()).isEqualTo(branchBack);

        inventory.branch(null);
        assertThat(inventory.getBranch()).isNull();
    }

    @Test
    void menuItemTest() {
        Inventory inventory = getInventoryRandomSampleGenerator();
        MenuItem menuItemBack = getMenuItemRandomSampleGenerator();

        inventory.setMenuItem(menuItemBack);
        assertThat(inventory.getMenuItem()).isEqualTo(menuItemBack);

        inventory.menuItem(null);
        assertThat(inventory.getMenuItem()).isNull();
    }
}
