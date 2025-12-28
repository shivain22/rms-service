package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PaymentMethodDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentMethodDTO.class);
        PaymentMethodDTO paymentMethodDTO1 = new PaymentMethodDTO();
        paymentMethodDTO1.setId(UUID.randomUUID());
        PaymentMethodDTO paymentMethodDTO2 = new PaymentMethodDTO();
        assertThat(paymentMethodDTO1).isNotEqualTo(paymentMethodDTO2);
        paymentMethodDTO2.setId(paymentMethodDTO1.getId());
        assertThat(paymentMethodDTO1).isEqualTo(paymentMethodDTO2);
        paymentMethodDTO2.setId(UUID.randomUUID());
        assertThat(paymentMethodDTO1).isNotEqualTo(paymentMethodDTO2);
        paymentMethodDTO1.setId(null);
        assertThat(paymentMethodDTO1).isNotEqualTo(paymentMethodDTO2);
    }
}
