package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BillTestSamples.*;
import static com.atparui.rmsservice.domain.BranchTestSamples.*;
import static com.atparui.rmsservice.domain.CustomerTestSamples.*;
import static com.atparui.rmsservice.domain.OrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BillTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bill.class);
        Bill bill1 = getBillSample1();
        Bill bill2 = new Bill();
        assertThat(bill1).isNotEqualTo(bill2);

        bill2.setId(bill1.getId());
        assertThat(bill1).isEqualTo(bill2);

        bill2 = getBillSample2();
        assertThat(bill1).isNotEqualTo(bill2);
    }

    @Test
    void orderTest() {
        Bill bill = getBillRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        bill.setOrder(orderBack);
        assertThat(bill.getOrder()).isEqualTo(orderBack);

        bill.order(null);
        assertThat(bill.getOrder()).isNull();
    }

    @Test
    void branchTest() {
        Bill bill = getBillRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        bill.setBranch(branchBack);
        assertThat(bill.getBranch()).isEqualTo(branchBack);

        bill.branch(null);
        assertThat(bill.getBranch()).isNull();
    }

    @Test
    void customerTest() {
        Bill bill = getBillRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        bill.setCustomer(customerBack);
        assertThat(bill.getCustomer()).isEqualTo(customerBack);

        bill.customer(null);
        assertThat(bill.getCustomer()).isNull();
    }
}
