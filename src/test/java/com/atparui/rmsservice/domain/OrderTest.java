package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BranchTableTestSamples.*;
import static com.atparui.rmsservice.domain.BranchTestSamples.*;
import static com.atparui.rmsservice.domain.CustomerTestSamples.*;
import static com.atparui.rmsservice.domain.OrderTestSamples.*;
import static com.atparui.rmsservice.domain.RmsUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Order.class);
        Order order1 = getOrderSample1();
        Order order2 = new Order();
        assertThat(order1).isNotEqualTo(order2);

        order2.setId(order1.getId());
        assertThat(order1).isEqualTo(order2);

        order2 = getOrderSample2();
        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    void branchTest() {
        Order order = getOrderRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        order.setBranch(branchBack);
        assertThat(order.getBranch()).isEqualTo(branchBack);

        order.branch(null);
        assertThat(order.getBranch()).isNull();
    }

    @Test
    void customerTest() {
        Order order = getOrderRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        order.setCustomer(customerBack);
        assertThat(order.getCustomer()).isEqualTo(customerBack);

        order.customer(null);
        assertThat(order.getCustomer()).isNull();
    }

    @Test
    void userTest() {
        Order order = getOrderRandomSampleGenerator();
        RmsUser rmsUserBack = getRmsUserRandomSampleGenerator();

        order.setUser(rmsUserBack);
        assertThat(order.getUser()).isEqualTo(rmsUserBack);

        order.user(null);
        assertThat(order.getUser()).isNull();
    }

    @Test
    void branchTableTest() {
        Order order = getOrderRandomSampleGenerator();
        BranchTable branchTableBack = getBranchTableRandomSampleGenerator();

        order.setBranchTable(branchTableBack);
        assertThat(order.getBranchTable()).isEqualTo(branchTableBack);

        order.branchTable(null);
        assertThat(order.getBranchTable()).isNull();
    }
}
