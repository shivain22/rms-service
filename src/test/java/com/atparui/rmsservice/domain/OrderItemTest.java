package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.MenuItemTestSamples.*;
import static com.atparui.rmsservice.domain.MenuItemVariantTestSamples.*;
import static com.atparui.rmsservice.domain.OrderItemTestSamples.*;
import static com.atparui.rmsservice.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItem.class);
        OrderItem orderItem1 = getOrderItemSample1();
        OrderItem orderItem2 = new OrderItem();
        assertThat(orderItem1).isNotEqualTo(orderItem2);

        orderItem2.setId(orderItem1.getId());
        assertThat(orderItem1).isEqualTo(orderItem2);

        orderItem2 = getOrderItemSample2();
        assertThat(orderItem1).isNotEqualTo(orderItem2);
    }

    @Test
    void orderTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        orderItem.setOrder(orderBack);
        assertThat(orderItem.getOrder()).isEqualTo(orderBack);

        orderItem.order(null);
        assertThat(orderItem.getOrder()).isNull();
    }

    @Test
    void menuItemTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        MenuItem menuItemBack = getMenuItemRandomSampleGenerator();

        orderItem.setMenuItem(menuItemBack);
        assertThat(orderItem.getMenuItem()).isEqualTo(menuItemBack);

        orderItem.menuItem(null);
        assertThat(orderItem.getMenuItem()).isNull();
    }

    @Test
    void menuItemVariantTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        MenuItemVariant menuItemVariantBack = getMenuItemVariantRandomSampleGenerator();

        orderItem.setMenuItemVariant(menuItemVariantBack);
        assertThat(orderItem.getMenuItemVariant()).isEqualTo(menuItemVariantBack);

        orderItem.menuItemVariant(null);
        assertThat(orderItem.getMenuItemVariant()).isNull();
    }
}
