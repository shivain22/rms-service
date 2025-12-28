package com.atparui.rmsservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.atparui.rmsservice.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InventoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InventoryDTO.class);
        InventoryDTO inventoryDTO1 = new InventoryDTO();
        inventoryDTO1.setId(UUID.randomUUID());
        InventoryDTO inventoryDTO2 = new InventoryDTO();
        assertThat(inventoryDTO1).isNotEqualTo(inventoryDTO2);
        inventoryDTO2.setId(inventoryDTO1.getId());
        assertThat(inventoryDTO1).isEqualTo(inventoryDTO2);
        inventoryDTO2.setId(UUID.randomUUID());
        assertThat(inventoryDTO1).isNotEqualTo(inventoryDTO2);
        inventoryDTO1.setId(null);
        assertThat(inventoryDTO1).isNotEqualTo(inventoryDTO2);
    }
}
