package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.MenuItemAddonTestSamples.*;
import static com.atparui.rmsservice.domain.OrderItemCustomizationTestSamples.*;
import static com.atparui.rmsservice.domain.OrderItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemCustomizationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItemCustomization.class);
        OrderItemCustomization orderItemCustomization1 = getOrderItemCustomizationSample1();
        OrderItemCustomization orderItemCustomization2 = new OrderItemCustomization();
        assertThat(orderItemCustomization1).isNotEqualTo(orderItemCustomization2);

        orderItemCustomization2.setId(orderItemCustomization1.getId());
        assertThat(orderItemCustomization1).isEqualTo(orderItemCustomization2);

        orderItemCustomization2 = getOrderItemCustomizationSample2();
        assertThat(orderItemCustomization1).isNotEqualTo(orderItemCustomization2);
    }

    @Test
    void orderItemTest() {
        OrderItemCustomization orderItemCustomization = getOrderItemCustomizationRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        orderItemCustomization.setOrderItem(orderItemBack);
        assertThat(orderItemCustomization.getOrderItem()).isEqualTo(orderItemBack);

        orderItemCustomization.orderItem(null);
        assertThat(orderItemCustomization.getOrderItem()).isNull();
    }

    @Test
    void menuItemAddonTest() {
        OrderItemCustomization orderItemCustomization = getOrderItemCustomizationRandomSampleGenerator();
        MenuItemAddon menuItemAddonBack = getMenuItemAddonRandomSampleGenerator();

        orderItemCustomization.setMenuItemAddon(menuItemAddonBack);
        assertThat(orderItemCustomization.getMenuItemAddon()).isEqualTo(menuItemAddonBack);

        orderItemCustomization.menuItemAddon(null);
        assertThat(orderItemCustomization.getMenuItemAddon()).isNull();
    }
}
