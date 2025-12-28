package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DiscountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DiscountDTO.class);
        DiscountDTO discountDTO1 = new DiscountDTO();
        discountDTO1.setId(UUID.randomUUID());
        DiscountDTO discountDTO2 = new DiscountDTO();
        assertThat(discountDTO1).isNotEqualTo(discountDTO2);
        discountDTO2.setId(discountDTO1.getId());
        assertThat(discountDTO1).isEqualTo(discountDTO2);
        discountDTO2.setId(UUID.randomUUID());
        assertThat(discountDTO1).isNotEqualTo(discountDTO2);
        discountDTO1.setId(null);
        assertThat(discountDTO1).isNotEqualTo(discountDTO2);
    }
}
