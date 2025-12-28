package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BillDiscountTestSamples.*;
import static com.atparui.rmsservice.domain.BillTestSamples.*;
import static com.atparui.rmsservice.domain.DiscountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BillDiscountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillDiscount.class);
        BillDiscount billDiscount1 = getBillDiscountSample1();
        BillDiscount billDiscount2 = new BillDiscount();
        assertThat(billDiscount1).isNotEqualTo(billDiscount2);

        billDiscount2.setId(billDiscount1.getId());
        assertThat(billDiscount1).isEqualTo(billDiscount2);

        billDiscount2 = getBillDiscountSample2();
        assertThat(billDiscount1).isNotEqualTo(billDiscount2);
    }

    @Test
    void billTest() {
        BillDiscount billDiscount = getBillDiscountRandomSampleGenerator();
        Bill billBack = getBillRandomSampleGenerator();

        billDiscount.setBill(billBack);
        assertThat(billDiscount.getBill()).isEqualTo(billBack);

        billDiscount.bill(null);
        assertThat(billDiscount.getBill()).isNull();
    }

    @Test
    void discountTest() {
        BillDiscount billDiscount = getBillDiscountRandomSampleGenerator();
        Discount discountBack = getDiscountRandomSampleGenerator();

        billDiscount.setDiscount(discountBack);
        assertThat(billDiscount.getDiscount()).isEqualTo(discountBack);

        billDiscount.discount(null);
        assertThat(billDiscount.getDiscount()).isNull();
    }
}
