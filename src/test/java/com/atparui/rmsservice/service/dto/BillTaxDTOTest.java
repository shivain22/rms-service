package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BillTaxDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillTaxDTO.class);
        BillTaxDTO billTaxDTO1 = new BillTaxDTO();
        billTaxDTO1.setId(UUID.randomUUID());
        BillTaxDTO billTaxDTO2 = new BillTaxDTO();
        assertThat(billTaxDTO1).isNotEqualTo(billTaxDTO2);
        billTaxDTO2.setId(billTaxDTO1.getId());
        assertThat(billTaxDTO1).isEqualTo(billTaxDTO2);
        billTaxDTO2.setId(UUID.randomUUID());
        assertThat(billTaxDTO1).isNotEqualTo(billTaxDTO2);
        billTaxDTO1.setId(null);
        assertThat(billTaxDTO1).isNotEqualTo(billTaxDTO2);
    }
}
