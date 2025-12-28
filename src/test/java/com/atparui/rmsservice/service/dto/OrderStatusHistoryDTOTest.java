package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderStatusHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderStatusHistoryDTO.class);
        OrderStatusHistoryDTO orderStatusHistoryDTO1 = new OrderStatusHistoryDTO();
        orderStatusHistoryDTO1.setId(UUID.randomUUID());
        OrderStatusHistoryDTO orderStatusHistoryDTO2 = new OrderStatusHistoryDTO();
        assertThat(orderStatusHistoryDTO1).isNotEqualTo(orderStatusHistoryDTO2);
        orderStatusHistoryDTO2.setId(orderStatusHistoryDTO1.getId());
        assertThat(orderStatusHistoryDTO1).isEqualTo(orderStatusHistoryDTO2);
        orderStatusHistoryDTO2.setId(UUID.randomUUID());
        assertThat(orderStatusHistoryDTO1).isNotEqualTo(orderStatusHistoryDTO2);
        orderStatusHistoryDTO1.setId(null);
        assertThat(orderStatusHistoryDTO1).isNotEqualTo(orderStatusHistoryDTO2);
    }
}
