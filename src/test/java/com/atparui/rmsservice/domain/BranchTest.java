package com.atparui.rmsservice.domain;

import static com.atparui.rmsservice.domain.BranchTestSamples.*;
import static com.atparui.rmsservice.domain.RestaurantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BranchTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Branch.class);
        Branch branch1 = getBranchSample1();
        Branch branch2 = new Branch();
        assertThat(branch1).isNotEqualTo(branch2);

        branch2.setId(branch1.getId());
        assertThat(branch1).isEqualTo(branch2);

        branch2 = getBranchSample2();
        assertThat(branch1).isNotEqualTo(branch2);
    }

    @Test
    void restaurantTest() {
        Branch branch = getBranchRandomSampleGenerator();
        Restaurant restaurantBack = getRestaurantRandomSampleGenerator();

        branch.setRestaurant(restaurantBack);
        assertThat(branch.getRestaurant()).isEqualTo(restaurantBack);

        branch.restaurant(null);
        assertThat(branch.getRestaurant()).isNull();
    }
}
