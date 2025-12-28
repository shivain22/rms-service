package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RmsUserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RmsUserDTO.class);
        RmsUserDTO rmsUserDTO1 = new RmsUserDTO();
        rmsUserDTO1.setId(UUID.randomUUID());
        RmsUserDTO rmsUserDTO2 = new RmsUserDTO();
        assertThat(rmsUserDTO1).isNotEqualTo(rmsUserDTO2);
        rmsUserDTO2.setId(rmsUserDTO1.getId());
        assertThat(rmsUserDTO1).isEqualTo(rmsUserDTO2);
        rmsUserDTO2.setId(UUID.randomUUID());
        assertThat(rmsUserDTO1).isNotEqualTo(rmsUserDTO2);
        rmsUserDTO1.setId(null);
        assertThat(rmsUserDTO1).isNotEqualTo(rmsUserDTO2);
    }
}
