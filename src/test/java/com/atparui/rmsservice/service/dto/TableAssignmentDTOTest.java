package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TableAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TableAssignmentDTO.class);
        TableAssignmentDTO tableAssignmentDTO1 = new TableAssignmentDTO();
        tableAssignmentDTO1.setId(UUID.randomUUID());
        TableAssignmentDTO tableAssignmentDTO2 = new TableAssignmentDTO();
        assertThat(tableAssignmentDTO1).isNotEqualTo(tableAssignmentDTO2);
        tableAssignmentDTO2.setId(tableAssignmentDTO1.getId());
        assertThat(tableAssignmentDTO1).isEqualTo(tableAssignmentDTO2);
        tableAssignmentDTO2.setId(UUID.randomUUID());
        assertThat(tableAssignmentDTO1).isNotEqualTo(tableAssignmentDTO2);
        tableAssignmentDTO1.setId(null);
        assertThat(tableAssignmentDTO1).isNotEqualTo(tableAssignmentDTO2);
    }
}
