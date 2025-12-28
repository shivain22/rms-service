package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RestaurantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantDTO.class);
        RestaurantDTO restaurantDTO1 = new RestaurantDTO();
        restaurantDTO1.setId(UUID.randomUUID());
        RestaurantDTO restaurantDTO2 = new RestaurantDTO();
        assertThat(restaurantDTO1).isNotEqualTo(restaurantDTO2);
        restaurantDTO2.setId(restaurantDTO1.getId());
        assertThat(restaurantDTO1).isEqualTo(restaurantDTO2);
        restaurantDTO2.setId(UUID.randomUUID());
        assertThat(restaurantDTO1).isNotEqualTo(restaurantDTO2);
        restaurantDTO1.setId(null);
        assertThat(restaurantDTO1).isNotEqualTo(restaurantDTO2);
    }
}
