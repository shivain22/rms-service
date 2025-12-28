package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderItemCustomizationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItemCustomizationDTO.class);
        OrderItemCustomizationDTO orderItemCustomizationDTO1 = new OrderItemCustomizationDTO();
        orderItemCustomizationDTO1.setId(UUID.randomUUID());
        OrderItemCustomizationDTO orderItemCustomizationDTO2 = new OrderItemCustomizationDTO();
        assertThat(orderItemCustomizationDTO1).isNotEqualTo(orderItemCustomizationDTO2);
        orderItemCustomizationDTO2.setId(orderItemCustomizationDTO1.getId());
        assertThat(orderItemCustomizationDTO1).isEqualTo(orderItemCustomizationDTO2);
        orderItemCustomizationDTO2.setId(UUID.randomUUID());
        assertThat(orderItemCustomizationDTO1).isNotEqualTo(orderItemCustomizationDTO2);
        orderItemCustomizationDTO1.setId(null);
        assertThat(orderItemCustomizationDTO1).isNotEqualTo(orderItemCustomizationDTO2);
    }
}
