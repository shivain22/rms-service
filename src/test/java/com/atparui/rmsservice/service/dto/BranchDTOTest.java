package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BranchDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BranchDTO.class);
        BranchDTO branchDTO1 = new BranchDTO();
        branchDTO1.setId(UUID.randomUUID());
        BranchDTO branchDTO2 = new BranchDTO();
        assertThat(branchDTO1).isNotEqualTo(branchDTO2);
        branchDTO2.setId(branchDTO1.getId());
        assertThat(branchDTO1).isEqualTo(branchDTO2);
        branchDTO2.setId(UUID.randomUUID());
        assertThat(branchDTO1).isNotEqualTo(branchDTO2);
        branchDTO1.setId(null);
        assertThat(branchDTO1).isNotEqualTo(branchDTO2);
    }
}
