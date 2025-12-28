package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BranchTableDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BranchTableDTO.class);
        BranchTableDTO branchTableDTO1 = new BranchTableDTO();
        branchTableDTO1.setId(UUID.randomUUID());
        BranchTableDTO branchTableDTO2 = new BranchTableDTO();
        assertThat(branchTableDTO1).isNotEqualTo(branchTableDTO2);
        branchTableDTO2.setId(branchTableDTO1.getId());
        assertThat(branchTableDTO1).isEqualTo(branchTableDTO2);
        branchTableDTO2.setId(UUID.randomUUID());
        assertThat(branchTableDTO1).isNotEqualTo(branchTableDTO2);
        branchTableDTO1.setId(null);
        assertThat(branchTableDTO1).isNotEqualTo(branchTableDTO2);
    }
}
