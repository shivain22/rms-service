package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BillTaxTestSamples.*;
import static com.atparui.rmsservice.domain.BillTestSamples.*;
import static com.atparui.rmsservice.domain.TaxConfigTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BillTaxTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillTax.class);
        BillTax billTax1 = getBillTaxSample1();
        BillTax billTax2 = new BillTax();
        assertThat(billTax1).isNotEqualTo(billTax2);

        billTax2.setId(billTax1.getId());
        assertThat(billTax1).isEqualTo(billTax2);

        billTax2 = getBillTaxSample2();
        assertThat(billTax1).isNotEqualTo(billTax2);
    }

    @Test
    void billTest() {
        BillTax billTax = getBillTaxRandomSampleGenerator();
        Bill billBack = getBillRandomSampleGenerator();

        billTax.setBill(billBack);
        assertThat(billTax.getBill()).isEqualTo(billBack);

        billTax.bill(null);
        assertThat(billTax.getBill()).isNull();
    }

    @Test
    void taxConfigTest() {
        BillTax billTax = getBillTaxRandomSampleGenerator();
        TaxConfig taxConfigBack = getTaxConfigRandomSampleGenerator();

        billTax.setTaxConfig(taxConfigBack);
        assertThat(billTax.getTaxConfig()).isEqualTo(taxConfigBack);

        billTax.taxConfig(null);
        assertThat(billTax.getTaxConfig()).isNull();
    }
}
