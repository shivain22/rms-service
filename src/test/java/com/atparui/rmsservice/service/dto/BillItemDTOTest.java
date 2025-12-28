package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BillItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillItemDTO.class);
        BillItemDTO billItemDTO1 = new BillItemDTO();
        billItemDTO1.setId(UUID.randomUUID());
        BillItemDTO billItemDTO2 = new BillItemDTO();
        assertThat(billItemDTO1).isNotEqualTo(billItemDTO2);
        billItemDTO2.setId(billItemDTO1.getId());
        assertThat(billItemDTO1).isEqualTo(billItemDTO2);
        billItemDTO2.setId(UUID.randomUUID());
        assertThat(billItemDTO1).isNotEqualTo(billItemDTO2);
        billItemDTO1.setId(null);
        assertThat(billItemDTO1).isNotEqualTo(billItemDTO2);
    }
}
