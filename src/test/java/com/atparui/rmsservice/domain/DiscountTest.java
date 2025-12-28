package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.DiscountTestSamples.*;
import static com.atparui.rmsservice.domain.RestaurantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiscountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Discount.class);
        Discount discount1 = getDiscountSample1();
        Discount discount2 = new Discount();
        assertThat(discount1).isNotEqualTo(discount2);

        discount2.setId(discount1.getId());
        assertThat(discount1).isEqualTo(discount2);

        discount2 = getDiscountSample2();
        assertThat(discount1).isNotEqualTo(discount2);
    }

    @Test
    void restaurantTest() {
        Discount discount = getDiscountRandomSampleGenerator();
        Restaurant restaurantBack = getRestaurantRandomSampleGenerator();

        discount.setRestaurant(restaurantBack);
        assertThat(discount.getRestaurant()).isEqualTo(restaurantBack);

        discount.restaurant(null);
        assertThat(discount.getRestaurant()).isNull();
    }
}
