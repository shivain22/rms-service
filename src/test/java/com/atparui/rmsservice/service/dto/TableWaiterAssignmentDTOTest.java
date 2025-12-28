package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TableWaiterAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TableWaiterAssignmentDTO.class);
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO1 = new TableWaiterAssignmentDTO();
        tableWaiterAssignmentDTO1.setId(UUID.randomUUID());
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO2 = new TableWaiterAssignmentDTO();
        assertThat(tableWaiterAssignmentDTO1).isNotEqualTo(tableWaiterAssignmentDTO2);
        tableWaiterAssignmentDTO2.setId(tableWaiterAssignmentDTO1.getId());
        assertThat(tableWaiterAssignmentDTO1).isEqualTo(tableWaiterAssignmentDTO2);
        tableWaiterAssignmentDTO2.setId(UUID.randomUUID());
        assertThat(tableWaiterAssignmentDTO1).isNotEqualTo(tableWaiterAssignmentDTO2);
        tableWaiterAssignmentDTO1.setId(null);
        assertThat(tableWaiterAssignmentDTO1).isNotEqualTo(tableWaiterAssignmentDTO2);
    }
}
