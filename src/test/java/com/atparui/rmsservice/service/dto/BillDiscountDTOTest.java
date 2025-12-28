package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BillDiscountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillDiscountDTO.class);
        BillDiscountDTO billDiscountDTO1 = new BillDiscountDTO();
        billDiscountDTO1.setId(UUID.randomUUID());
        BillDiscountDTO billDiscountDTO2 = new BillDiscountDTO();
        assertThat(billDiscountDTO1).isNotEqualTo(billDiscountDTO2);
        billDiscountDTO2.setId(billDiscountDTO1.getId());
        assertThat(billDiscountDTO1).isEqualTo(billDiscountDTO2);
        billDiscountDTO2.setId(UUID.randomUUID());
        assertThat(billDiscountDTO1).isNotEqualTo(billDiscountDTO2);
        billDiscountDTO1.setId(null);
        assertThat(billDiscountDTO1).isNotEqualTo(billDiscountDTO2);
    }
}
