package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CustomerLoyaltyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerLoyaltyDTO.class);
        CustomerLoyaltyDTO customerLoyaltyDTO1 = new CustomerLoyaltyDTO();
        customerLoyaltyDTO1.setId(UUID.randomUUID());
        CustomerLoyaltyDTO customerLoyaltyDTO2 = new CustomerLoyaltyDTO();
        assertThat(customerLoyaltyDTO1).isNotEqualTo(customerLoyaltyDTO2);
        customerLoyaltyDTO2.setId(customerLoyaltyDTO1.getId());
        assertThat(customerLoyaltyDTO1).isEqualTo(customerLoyaltyDTO2);
        customerLoyaltyDTO2.setId(UUID.randomUUID());
        assertThat(customerLoyaltyDTO1).isNotEqualTo(customerLoyaltyDTO2);
        customerLoyaltyDTO1.setId(null);
        assertThat(customerLoyaltyDTO1).isNotEqualTo(customerLoyaltyDTO2);
    }
}
