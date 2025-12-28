package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.InventoryAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.Inventory;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.InventoryRepository;
import com.atparui.rmsservice.service.dto.InventoryDTO;
import com.atparui.rmsservice.service.mapper.InventoryMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link InventoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InventoryResourceIT {

    private static final BigDecimal DEFAULT_CURRENT_STOCK = new BigDecimal(1);
    private static final BigDecimal UPDATED_CURRENT_STOCK = new BigDecimal(2);

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_MIN_STOCK_LEVEL = new BigDecimal(1);
    private static final BigDecimal UPDATED_MIN_STOCK_LEVEL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_MAX_STOCK_LEVEL = new BigDecimal(1);
    private static final BigDecimal UPDATED_MAX_STOCK_LEVEL = new BigDecimal(2);

    private static final Instant DEFAULT_LAST_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_UPDATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/inventories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Inventory inventory;

    private Inventory insertedInventory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventory createEntity() {
        return new Inventory()
            .id(UUID.randomUUID())
            .currentStock(DEFAULT_CURRENT_STOCK)
            .unit(DEFAULT_UNIT)
            .minStockLevel(DEFAULT_MIN_STOCK_LEVEL)
            .maxStockLevel(DEFAULT_MAX_STOCK_LEVEL)
            .lastUpdatedAt(DEFAULT_LAST_UPDATED_AT)
            .lastUpdatedBy(DEFAULT_LAST_UPDATED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventory createUpdatedEntity() {
        return new Inventory()
            .id(UUID.randomUUID())
            .currentStock(UPDATED_CURRENT_STOCK)
            .unit(UPDATED_UNIT)
            .minStockLevel(UPDATED_MIN_STOCK_LEVEL)
            .maxStockLevel(UPDATED_MAX_STOCK_LEVEL)
            .lastUpdatedAt(UPDATED_LAST_UPDATED_AT)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Inventory.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    void initTest() {
        inventory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInventory != null) {
            inventoryRepository.delete(insertedInventory).block();
            insertedInventory = null;
        }
        deleteEntities(em);
    }

    @Test
    void createInventory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        inventory.setId(null);
        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);
        var returnedInventoryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(InventoryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Inventory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInventory = inventoryMapper.toEntity(returnedInventoryDTO);
        assertInventoryUpdatableFieldsEquals(returnedInventory, getPersistedInventory(returnedInventory));

        insertedInventory = returnedInventory;
    }

    @Test
    void createInventoryWithExistingId() throws Exception {
        // Create the Inventory with an existing ID
        insertedInventory = inventoryRepository.save(inventory).block();
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkLastUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventory.setLastUpdatedAt(null);

        // Create the Inventory, which fails.
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllInventoriesAsStream() {
        // Initialize the database
        inventory.setId(UUID.randomUUID());
        inventoryRepository.save(inventory).block();

        List<Inventory> inventoryList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(InventoryDTO.class)
            .getResponseBody()
            .map(inventoryMapper::toEntity)
            .filter(inventory::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(inventoryList).isNotNull();
        assertThat(inventoryList).hasSize(1);
        Inventory testInventory = inventoryList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertInventoryAllPropertiesEquals(inventory, testInventory);
        assertInventoryUpdatableFieldsEquals(inventory, testInventory);
    }

    @Test
    void getAllInventories() {
        // Initialize the database
        inventory.setId(UUID.randomUUID());
        insertedInventory = inventoryRepository.save(inventory).block();

        // Get all the inventoryList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(inventory.getId().toString()))
            .jsonPath("$.[*].currentStock")
            .value(hasItem(sameNumber(DEFAULT_CURRENT_STOCK)))
            .jsonPath("$.[*].unit")
            .value(hasItem(DEFAULT_UNIT))
            .jsonPath("$.[*].minStockLevel")
            .value(hasItem(sameNumber(DEFAULT_MIN_STOCK_LEVEL)))
            .jsonPath("$.[*].maxStockLevel")
            .value(hasItem(sameNumber(DEFAULT_MAX_STOCK_LEVEL)))
            .jsonPath("$.[*].lastUpdatedAt")
            .value(hasItem(DEFAULT_LAST_UPDATED_AT.toString()))
            .jsonPath("$.[*].lastUpdatedBy")
            .value(hasItem(DEFAULT_LAST_UPDATED_BY));
    }

    @Test
    void getInventory() {
        // Initialize the database
        inventory.setId(UUID.randomUUID());
        insertedInventory = inventoryRepository.save(inventory).block();

        // Get the inventory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, inventory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(inventory.getId().toString()))
            .jsonPath("$.currentStock")
            .value(is(sameNumber(DEFAULT_CURRENT_STOCK)))
            .jsonPath("$.unit")
            .value(is(DEFAULT_UNIT))
            .jsonPath("$.minStockLevel")
            .value(is(sameNumber(DEFAULT_MIN_STOCK_LEVEL)))
            .jsonPath("$.maxStockLevel")
            .value(is(sameNumber(DEFAULT_MAX_STOCK_LEVEL)))
            .jsonPath("$.lastUpdatedAt")
            .value(is(DEFAULT_LAST_UPDATED_AT.toString()))
            .jsonPath("$.lastUpdatedBy")
            .value(is(DEFAULT_LAST_UPDATED_BY));
    }

    @Test
    void getNonExistingInventory() {
        // Get the inventory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInventory() throws Exception {
        // Initialize the database
        inventory.setId(UUID.randomUUID());
        insertedInventory = inventoryRepository.save(inventory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventory
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).block();
        updatedInventory
            .currentStock(UPDATED_CURRENT_STOCK)
            .unit(UPDATED_UNIT)
            .minStockLevel(UPDATED_MIN_STOCK_LEVEL)
            .maxStockLevel(UPDATED_MAX_STOCK_LEVEL)
            .lastUpdatedAt(UPDATED_LAST_UPDATED_AT)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);
        InventoryDTO inventoryDTO = inventoryMapper.toDto(updatedInventory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, inventoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventoryToMatchAllProperties(updatedInventory);
    }

    @Test
    void putNonExistingInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(UUID.randomUUID());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, inventoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(UUID.randomUUID());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(UUID.randomUUID());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInventoryWithPatch() throws Exception {
        // Initialize the database
        inventory.setId(UUID.randomUUID());
        insertedInventory = inventoryRepository.save(inventory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventory using partial update
        Inventory partialUpdatedInventory = new Inventory();
        partialUpdatedInventory.setId(inventory.getId());

        partialUpdatedInventory
            .currentStock(UPDATED_CURRENT_STOCK)
            .unit(UPDATED_UNIT)
            .minStockLevel(UPDATED_MIN_STOCK_LEVEL)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInventory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInventory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Inventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInventory, inventory),
            getPersistedInventory(inventory)
        );
    }

    @Test
    void fullUpdateInventoryWithPatch() throws Exception {
        // Initialize the database
        inventory.setId(UUID.randomUUID());
        insertedInventory = inventoryRepository.save(inventory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventory using partial update
        Inventory partialUpdatedInventory = new Inventory();
        partialUpdatedInventory.setId(inventory.getId());

        partialUpdatedInventory
            .currentStock(UPDATED_CURRENT_STOCK)
            .unit(UPDATED_UNIT)
            .minStockLevel(UPDATED_MIN_STOCK_LEVEL)
            .maxStockLevel(UPDATED_MAX_STOCK_LEVEL)
            .lastUpdatedAt(UPDATED_LAST_UPDATED_AT)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInventory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInventory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Inventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryUpdatableFieldsEquals(partialUpdatedInventory, getPersistedInventory(partialUpdatedInventory));
    }

    @Test
    void patchNonExistingInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(UUID.randomUUID());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, inventoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(UUID.randomUUID());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(UUID.randomUUID());

        // Create the Inventory
        InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(inventoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInventory() {
        // Initialize the database
        inventory.setId(UUID.randomUUID());
        insertedInventory = inventoryRepository.save(inventory).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inventory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, inventory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inventoryRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Inventory getPersistedInventory(Inventory inventory) {
        return inventoryRepository.findById(inventory.getId()).block();
    }

    protected void assertPersistedInventoryToMatchAllProperties(Inventory expectedInventory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInventoryAllPropertiesEquals(expectedInventory, getPersistedInventory(expectedInventory));
        assertInventoryUpdatableFieldsEquals(expectedInventory, getPersistedInventory(expectedInventory));
    }

    protected void assertPersistedInventoryToMatchUpdatableProperties(Inventory expectedInventory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInventoryAllUpdatablePropertiesEquals(expectedInventory, getPersistedInventory(expectedInventory));
        assertInventoryUpdatableFieldsEquals(expectedInventory, getPersistedInventory(expectedInventory));
    }
}
