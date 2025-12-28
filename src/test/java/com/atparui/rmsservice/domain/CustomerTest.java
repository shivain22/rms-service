package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.CustomerTestSamples.*;
import static com.atparui.rmsservice.domain.RmsUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void userTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        RmsUser rmsUserBack = getRmsUserRandomSampleGenerator();

        customer.setUser(rmsUserBack);
        assertThat(customer.getUser()).isEqualTo(rmsUserBack);

        customer.user(null);
        assertThat(customer.getUser()).isNull();
    }
}
