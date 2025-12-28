package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.BranchTableAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.BranchTable;
import com.atparui.rmsservice.repository.BranchTableRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.search.BranchTableSearchRepository;
import com.atparui.rmsservice.service.dto.BranchTableDTO;
import com.atparui.rmsservice.service.mapper.BranchTableMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link BranchTableResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BranchTableResourceIT {

    private static final String DEFAULT_TABLE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_TABLE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_TABLE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TABLE_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;

    private static final String DEFAULT_FLOOR = "AAAAAAAAAA";
    private static final String UPDATED_FLOOR = "BBBBBBBBBB";

    private static final String DEFAULT_SECTION = "AAAAAAAAAA";
    private static final String UPDATED_SECTION = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_QR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_QR_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_QR_CODE_URL = "AAAAAAAAAA";
    private static final String UPDATED_QR_CODE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/branch-tables";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/branch-tables/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BranchTableRepository branchTableRepository;

    @Autowired
    private BranchTableMapper branchTableMapper;

    @Autowired
    private BranchTableSearchRepository branchTableSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private BranchTable branchTable;

    private BranchTable insertedBranchTable;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BranchTable createEntity() {
        return new BranchTable()
            .id(UUID.randomUUID())
            .tableNumber(DEFAULT_TABLE_NUMBER)
            .tableName(DEFAULT_TABLE_NAME)
            .capacity(DEFAULT_CAPACITY)
            .floor(DEFAULT_FLOOR)
            .section(DEFAULT_SECTION)
            .status(DEFAULT_STATUS)
            .qrCode(DEFAULT_QR_CODE)
            .qrCodeUrl(DEFAULT_QR_CODE_URL)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BranchTable createUpdatedEntity() {
        return new BranchTable()
            .id(UUID.randomUUID())
            .tableNumber(UPDATED_TABLE_NUMBER)
            .tableName(UPDATED_TABLE_NAME)
            .capacity(UPDATED_CAPACITY)
            .floor(UPDATED_FLOOR)
            .section(UPDATED_SECTION)
            .status(UPDATED_STATUS)
            .qrCode(UPDATED_QR_CODE)
            .qrCodeUrl(UPDATED_QR_CODE_URL)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(BranchTable.class).block();
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
        branchTable = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBranchTable != null) {
            branchTableRepository.delete(insertedBranchTable).block();
            branchTableSearchRepository.delete(insertedBranchTable).block();
            insertedBranchTable = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBranchTable() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        branchTable.setId(null);
        // Create the BranchTable
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);
        var returnedBranchTableDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BranchTableDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the BranchTable in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBranchTable = branchTableMapper.toEntity(returnedBranchTableDTO);
        assertBranchTableUpdatableFieldsEquals(returnedBranchTable, getPersistedBranchTable(returnedBranchTable));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedBranchTable = returnedBranchTable;
    }

    @Test
    void createBranchTableWithExistingId() throws Exception {
        // Create the BranchTable with an existing ID
        insertedBranchTable = branchTableRepository.save(branchTable).block();
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BranchTable in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTableNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        // set the field null
        branchTable.setTableNumber(null);

        // Create the BranchTable, which fails.
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        // set the field null
        branchTable.setCapacity(null);

        // Create the BranchTable, which fails.
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllBranchTables() {
        // Initialize the database
        branchTable.setId(UUID.randomUUID());
        insertedBranchTable = branchTableRepository.save(branchTable).block();

        // Get all the branchTableList
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
            .value(hasItem(branchTable.getId().toString()))
            .jsonPath("$.[*].tableNumber")
            .value(hasItem(DEFAULT_TABLE_NUMBER))
            .jsonPath("$.[*].tableName")
            .value(hasItem(DEFAULT_TABLE_NAME))
            .jsonPath("$.[*].capacity")
            .value(hasItem(DEFAULT_CAPACITY))
            .jsonPath("$.[*].floor")
            .value(hasItem(DEFAULT_FLOOR))
            .jsonPath("$.[*].section")
            .value(hasItem(DEFAULT_SECTION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].qrCode")
            .value(hasItem(DEFAULT_QR_CODE))
            .jsonPath("$.[*].qrCodeUrl")
            .value(hasItem(DEFAULT_QR_CODE_URL))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getBranchTable() {
        // Initialize the database
        branchTable.setId(UUID.randomUUID());
        insertedBranchTable = branchTableRepository.save(branchTable).block();

        // Get the branchTable
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, branchTable.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(branchTable.getId().toString()))
            .jsonPath("$.tableNumber")
            .value(is(DEFAULT_TABLE_NUMBER))
            .jsonPath("$.tableName")
            .value(is(DEFAULT_TABLE_NAME))
            .jsonPath("$.capacity")
            .value(is(DEFAULT_CAPACITY))
            .jsonPath("$.floor")
            .value(is(DEFAULT_FLOOR))
            .jsonPath("$.section")
            .value(is(DEFAULT_SECTION))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
            .jsonPath("$.qrCode")
            .value(is(DEFAULT_QR_CODE))
            .jsonPath("$.qrCodeUrl")
            .value(is(DEFAULT_QR_CODE_URL))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingBranchTable() {
        // Get the branchTable
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBranchTable() throws Exception {
        // Initialize the database
        branchTable.setId(UUID.randomUUID());
        insertedBranchTable = branchTableRepository.save(branchTable).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        branchTableSearchRepository.save(branchTable).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());

        // Update the branchTable
        BranchTable updatedBranchTable = branchTableRepository.findById(branchTable.getId()).block();
        updatedBranchTable
            .tableNumber(UPDATED_TABLE_NUMBER)
            .tableName(UPDATED_TABLE_NAME)
            .capacity(UPDATED_CAPACITY)
            .floor(UPDATED_FLOOR)
            .section(UPDATED_SECTION)
            .status(UPDATED_STATUS)
            .qrCode(UPDATED_QR_CODE)
            .qrCodeUrl(UPDATED_QR_CODE_URL)
            .isActive(UPDATED_IS_ACTIVE);
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(updatedBranchTable);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, branchTableDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BranchTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBranchTableToMatchAllProperties(updatedBranchTable);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BranchTable> branchTableSearchList = Streamable.of(
                    branchTableSearchRepository.findAll().collectList().block()
                ).toList();
                BranchTable testBranchTableSearch = branchTableSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertBranchTableAllPropertiesEquals(testBranchTableSearch, updatedBranchTable);
                assertBranchTableUpdatableFieldsEquals(testBranchTableSearch, updatedBranchTable);
            });
    }

    @Test
    void putNonExistingBranchTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        branchTable.setId(UUID.randomUUID());

        // Create the BranchTable
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, branchTableDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BranchTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchBranchTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        branchTable.setId(UUID.randomUUID());

        // Create the BranchTable
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BranchTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamBranchTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        branchTable.setId(UUID.randomUUID());

        // Create the BranchTable
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BranchTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateBranchTableWithPatch() throws Exception {
        // Initialize the database
        branchTable.setId(UUID.randomUUID());
        insertedBranchTable = branchTableRepository.save(branchTable).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branchTable using partial update
        BranchTable partialUpdatedBranchTable = new BranchTable();
        partialUpdatedBranchTable.setId(branchTable.getId());

        partialUpdatedBranchTable.capacity(UPDATED_CAPACITY).floor(UPDATED_FLOOR).section(UPDATED_SECTION).qrCode(UPDATED_QR_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBranchTable.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBranchTable))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BranchTable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBranchTableUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBranchTable, branchTable),
            getPersistedBranchTable(branchTable)
        );
    }

    @Test
    void fullUpdateBranchTableWithPatch() throws Exception {
        // Initialize the database
        branchTable.setId(UUID.randomUUID());
        insertedBranchTable = branchTableRepository.save(branchTable).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branchTable using partial update
        BranchTable partialUpdatedBranchTable = new BranchTable();
        partialUpdatedBranchTable.setId(branchTable.getId());

        partialUpdatedBranchTable
            .tableNumber(UPDATED_TABLE_NUMBER)
            .tableName(UPDATED_TABLE_NAME)
            .capacity(UPDATED_CAPACITY)
            .floor(UPDATED_FLOOR)
            .section(UPDATED_SECTION)
            .status(UPDATED_STATUS)
            .qrCode(UPDATED_QR_CODE)
            .qrCodeUrl(UPDATED_QR_CODE_URL)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBranchTable.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBranchTable))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BranchTable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBranchTableUpdatableFieldsEquals(partialUpdatedBranchTable, getPersistedBranchTable(partialUpdatedBranchTable));
    }

    @Test
    void patchNonExistingBranchTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        branchTable.setId(UUID.randomUUID());

        // Create the BranchTable
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, branchTableDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BranchTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchBranchTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        branchTable.setId(UUID.randomUUID());

        // Create the BranchTable
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BranchTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamBranchTable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        branchTable.setId(UUID.randomUUID());

        // Create the BranchTable
        BranchTableDTO branchTableDTO = branchTableMapper.toDto(branchTable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(branchTableDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BranchTable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteBranchTable() {
        // Initialize the database
        branchTable.setId(UUID.randomUUID());
        insertedBranchTable = branchTableRepository.save(branchTable).block();
        branchTableRepository.save(branchTable).block();
        branchTableSearchRepository.save(branchTable).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the branchTable
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, branchTable.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchTableSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchBranchTable() {
        // Initialize the database
        branchTable.setId(UUID.randomUUID());
        insertedBranchTable = branchTableRepository.save(branchTable).block();
        branchTableSearchRepository.save(branchTable).block();

        // Search the branchTable
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + branchTable.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(branchTable.getId().toString()))
            .jsonPath("$.[*].tableNumber")
            .value(hasItem(DEFAULT_TABLE_NUMBER))
            .jsonPath("$.[*].tableName")
            .value(hasItem(DEFAULT_TABLE_NAME))
            .jsonPath("$.[*].capacity")
            .value(hasItem(DEFAULT_CAPACITY))
            .jsonPath("$.[*].floor")
            .value(hasItem(DEFAULT_FLOOR))
            .jsonPath("$.[*].section")
            .value(hasItem(DEFAULT_SECTION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].qrCode")
            .value(hasItem(DEFAULT_QR_CODE))
            .jsonPath("$.[*].qrCodeUrl")
            .value(hasItem(DEFAULT_QR_CODE_URL))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    protected long getRepositoryCount() {
        return branchTableRepository.count().block();
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

    protected BranchTable getPersistedBranchTable(BranchTable branchTable) {
        return branchTableRepository.findById(branchTable.getId()).block();
    }

    protected void assertPersistedBranchTableToMatchAllProperties(BranchTable expectedBranchTable) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBranchTableAllPropertiesEquals(expectedBranchTable, getPersistedBranchTable(expectedBranchTable));
        assertBranchTableUpdatableFieldsEquals(expectedBranchTable, getPersistedBranchTable(expectedBranchTable));
    }

    protected void assertPersistedBranchTableToMatchUpdatableProperties(BranchTable expectedBranchTable) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBranchTableAllUpdatablePropertiesEquals(expectedBranchTable, getPersistedBranchTable(expectedBranchTable));
        assertBranchTableUpdatableFieldsEquals(expectedBranchTable, getPersistedBranchTable(expectedBranchTable));
    }
}
