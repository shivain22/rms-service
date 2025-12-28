package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.RestaurantTestSamples.*;
import static com.atparui.rmsservice.domain.TaxConfigTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaxConfigTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaxConfig.class);
        TaxConfig taxConfig1 = getTaxConfigSample1();
        TaxConfig taxConfig2 = new TaxConfig();
        assertThat(taxConfig1).isNotEqualTo(taxConfig2);

        taxConfig2.setId(taxConfig1.getId());
        assertThat(taxConfig1).isEqualTo(taxConfig2);

        taxConfig2 = getTaxConfigSample2();
        assertThat(taxConfig1).isNotEqualTo(taxConfig2);
    }

    @Test
    void restaurantTest() {
        TaxConfig taxConfig = getTaxConfigRandomSampleGenerator();
        Restaurant restaurantBack = getRestaurantRandomSampleGenerator();

        taxConfig.setRestaurant(restaurantBack);
        assertThat(taxConfig.getRestaurant()).isEqualTo(restaurantBack);

        taxConfig.restaurant(null);
        assertThat(taxConfig.getRestaurant()).isNull();
    }
}
