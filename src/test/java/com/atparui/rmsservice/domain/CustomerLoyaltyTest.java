package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.CustomerLoyaltyTestSamples.*;
import static com.atparui.rmsservice.domain.CustomerTestSamples.*;
import static com.atparui.rmsservice.domain.RestaurantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerLoyaltyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerLoyalty.class);
        CustomerLoyalty customerLoyalty1 = getCustomerLoyaltySample1();
        CustomerLoyalty customerLoyalty2 = new CustomerLoyalty();
        assertThat(customerLoyalty1).isNotEqualTo(customerLoyalty2);

        customerLoyalty2.setId(customerLoyalty1.getId());
        assertThat(customerLoyalty1).isEqualTo(customerLoyalty2);

        customerLoyalty2 = getCustomerLoyaltySample2();
        assertThat(customerLoyalty1).isNotEqualTo(customerLoyalty2);
    }

    @Test
    void customerTest() {
        CustomerLoyalty customerLoyalty = getCustomerLoyaltyRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        customerLoyalty.setCustomer(customerBack);
        assertThat(customerLoyalty.getCustomer()).isEqualTo(customerBack);

        customerLoyalty.customer(null);
        assertThat(customerLoyalty.getCustomer()).isNull();
    }

    @Test
    void restaurantTest() {
        CustomerLoyalty customerLoyalty = getCustomerLoyaltyRandomSampleGenerator();
        Restaurant restaurantBack = getRestaurantRandomSampleGenerator();

        customerLoyalty.setRestaurant(restaurantBack);
        assertThat(customerLoyalty.getRestaurant()).isEqualTo(restaurantBack);

        customerLoyalty.restaurant(null);
        assertThat(customerLoyalty.getRestaurant()).isNull();
    }
}
