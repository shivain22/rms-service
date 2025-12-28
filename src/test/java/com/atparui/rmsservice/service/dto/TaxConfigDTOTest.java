package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TaxConfigDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaxConfigDTO.class);
        TaxConfigDTO taxConfigDTO1 = new TaxConfigDTO();
        taxConfigDTO1.setId(UUID.randomUUID());
        TaxConfigDTO taxConfigDTO2 = new TaxConfigDTO();
        assertThat(taxConfigDTO1).isNotEqualTo(taxConfigDTO2);
        taxConfigDTO2.setId(taxConfigDTO1.getId());
        assertThat(taxConfigDTO1).isEqualTo(taxConfigDTO2);
        taxConfigDTO2.setId(UUID.randomUUID());
        assertThat(taxConfigDTO1).isNotEqualTo(taxConfigDTO2);
        taxConfigDTO1.setId(null);
        assertThat(taxConfigDTO1).isNotEqualTo(taxConfigDTO2);
    }
}
