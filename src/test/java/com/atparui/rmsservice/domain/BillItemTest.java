package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BillItemTestSamples.*;
import static com.atparui.rmsservice.domain.BillTestSamples.*;
import static com.atparui.rmsservice.domain.OrderItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BillItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillItem.class);
        BillItem billItem1 = getBillItemSample1();
        BillItem billItem2 = new BillItem();
        assertThat(billItem1).isNotEqualTo(billItem2);

        billItem2.setId(billItem1.getId());
        assertThat(billItem1).isEqualTo(billItem2);

        billItem2 = getBillItemSample2();
        assertThat(billItem1).isNotEqualTo(billItem2);
    }

    @Test
    void billTest() {
        BillItem billItem = getBillItemRandomSampleGenerator();
        Bill billBack = getBillRandomSampleGenerator();

        billItem.setBill(billBack);
        assertThat(billItem.getBill()).isEqualTo(billBack);

        billItem.bill(null);
        assertThat(billItem.getBill()).isNull();
    }

    @Test
    void orderItemTest() {
        BillItem billItem = getBillItemRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        billItem.setOrderItem(orderItemBack);
        assertThat(billItem.getOrderItem()).isEqualTo(orderItemBack);

        billItem.orderItem(null);
        assertThat(billItem.getOrderItem()).isNull();
    }
}
