package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.MenuCategoryTestSamples.*;
import static com.atparui.rmsservice.domain.RestaurantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuCategory.class);
        MenuCategory menuCategory1 = getMenuCategorySample1();
        MenuCategory menuCategory2 = new MenuCategory();
        assertThat(menuCategory1).isNotEqualTo(menuCategory2);

        menuCategory2.setId(menuCategory1.getId());
        assertThat(menuCategory1).isEqualTo(menuCategory2);

        menuCategory2 = getMenuCategorySample2();
        assertThat(menuCategory1).isNotEqualTo(menuCategory2);
    }

    @Test
    void restaurantTest() {
        MenuCategory menuCategory = getMenuCategoryRandomSampleGenerator();
        Restaurant restaurantBack = getRestaurantRandomSampleGenerator();

        menuCategory.setRestaurant(restaurantBack);
        assertThat(menuCategory.getRestaurant()).isEqualTo(restaurantBack);

        menuCategory.restaurant(null);
        assertThat(menuCategory.getRestaurant()).isNull();
    }
}
