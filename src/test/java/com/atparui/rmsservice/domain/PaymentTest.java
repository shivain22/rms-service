package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BillTestSamples.*;
import static com.atparui.rmsservice.domain.PaymentMethodTestSamples.*;
import static com.atparui.rmsservice.domain.PaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void billTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        Bill billBack = getBillRandomSampleGenerator();

        payment.setBill(billBack);
        assertThat(payment.getBill()).isEqualTo(billBack);

        payment.bill(null);
        assertThat(payment.getBill()).isNull();
    }

    @Test
    void paymentMethodTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        PaymentMethod paymentMethodBack = getPaymentMethodRandomSampleGenerator();

        payment.setPaymentMethod(paymentMethodBack);
        assertThat(payment.getPaymentMethod()).isEqualTo(paymentMethodBack);

        payment.paymentMethod(null);
        assertThat(payment.getPaymentMethod()).isNull();
    }
}
