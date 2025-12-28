package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.TableWaiterAssignmentAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.TableWaiterAssignment;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.TableWaiterAssignmentRepository;
import com.atparui.rmsservice.service.dto.TableWaiterAssignmentDTO;
import com.atparui.rmsservice.service.mapper.TableWaiterAssignmentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link TableWaiterAssignmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TableWaiterAssignmentResourceIT {

    private static final LocalDate DEFAULT_ASSIGNMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ASSIGNMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/table-waiter-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TableWaiterAssignmentRepository tableWaiterAssignmentRepository;

    @Autowired
    private TableWaiterAssignmentMapper tableWaiterAssignmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TableWaiterAssignment tableWaiterAssignment;

    private TableWaiterAssignment insertedTableWaiterAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TableWaiterAssignment createEntity() {
        return new TableWaiterAssignment()
            .id(UUID.randomUUID())
            .assignmentDate(DEFAULT_ASSIGNMENT_DATE)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TableWaiterAssignment createUpdatedEntity() {
        return new TableWaiterAssignment()
            .id(UUID.randomUUID())
            .assignmentDate(UPDATED_ASSIGNMENT_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TableWaiterAssignment.class).block();
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
        tableWaiterAssignment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTableWaiterAssignment != null) {
            tableWaiterAssignmentRepository.delete(insertedTableWaiterAssignment).block();
            insertedTableWaiterAssignment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTableWaiterAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        tableWaiterAssignment.setId(null);
        // Create the TableWaiterAssignment
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(tableWaiterAssignment);
        var returnedTableWaiterAssignmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TableWaiterAssignmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TableWaiterAssignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTableWaiterAssignment = tableWaiterAssignmentMapper.toEntity(returnedTableWaiterAssignmentDTO);
        assertTableWaiterAssignmentUpdatableFieldsEquals(
            returnedTableWaiterAssignment,
            getPersistedTableWaiterAssignment(returnedTableWaiterAssignment)
        );

        insertedTableWaiterAssignment = returnedTableWaiterAssignment;
    }

    @Test
    void createTableWaiterAssignmentWithExistingId() throws Exception {
        // Create the TableWaiterAssignment with an existing ID
        insertedTableWaiterAssignment = tableWaiterAssignmentRepository.save(tableWaiterAssignment).block();
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(tableWaiterAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableWaiterAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAssignmentDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tableWaiterAssignment.setAssignmentDate(null);

        // Create the TableWaiterAssignment, which fails.
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(tableWaiterAssignment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTableWaiterAssignmentsAsStream() {
        // Initialize the database
        tableWaiterAssignment.setId(UUID.randomUUID());
        tableWaiterAssignmentRepository.save(tableWaiterAssignment).block();

        List<TableWaiterAssignment> tableWaiterAssignmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TableWaiterAssignmentDTO.class)
            .getResponseBody()
            .map(tableWaiterAssignmentMapper::toEntity)
            .filter(tableWaiterAssignment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tableWaiterAssignmentList).isNotNull();
        assertThat(tableWaiterAssignmentList).hasSize(1);
        TableWaiterAssignment testTableWaiterAssignment = tableWaiterAssignmentList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertTableWaiterAssignmentAllPropertiesEquals(tableWaiterAssignment, testTableWaiterAssignment);
        assertTableWaiterAssignmentUpdatableFieldsEquals(tableWaiterAssignment, testTableWaiterAssignment);
    }

    @Test
    void getAllTableWaiterAssignments() {
        // Initialize the database
        tableWaiterAssignment.setId(UUID.randomUUID());
        insertedTableWaiterAssignment = tableWaiterAssignmentRepository.save(tableWaiterAssignment).block();

        // Get all the tableWaiterAssignmentList
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
            .value(hasItem(tableWaiterAssignment.getId().toString()))
            .jsonPath("$.[*].assignmentDate")
            .value(hasItem(DEFAULT_ASSIGNMENT_DATE.toString()))
            .jsonPath("$.[*].startTime")
            .value(hasItem(DEFAULT_START_TIME.toString()))
            .jsonPath("$.[*].endTime")
            .value(hasItem(DEFAULT_END_TIME.toString()))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getTableWaiterAssignment() {
        // Initialize the database
        tableWaiterAssignment.setId(UUID.randomUUID());
        insertedTableWaiterAssignment = tableWaiterAssignmentRepository.save(tableWaiterAssignment).block();

        // Get the tableWaiterAssignment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tableWaiterAssignment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tableWaiterAssignment.getId().toString()))
            .jsonPath("$.assignmentDate")
            .value(is(DEFAULT_ASSIGNMENT_DATE.toString()))
            .jsonPath("$.startTime")
            .value(is(DEFAULT_START_TIME.toString()))
            .jsonPath("$.endTime")
            .value(is(DEFAULT_END_TIME.toString()))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingTableWaiterAssignment() {
        // Get the tableWaiterAssignment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTableWaiterAssignment() throws Exception {
        // Initialize the database
        tableWaiterAssignment.setId(UUID.randomUUID());
        insertedTableWaiterAssignment = tableWaiterAssignmentRepository.save(tableWaiterAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tableWaiterAssignment
        TableWaiterAssignment updatedTableWaiterAssignment = tableWaiterAssignmentRepository
            .findById(tableWaiterAssignment.getId())
            .block();
        updatedTableWaiterAssignment
            .assignmentDate(UPDATED_ASSIGNMENT_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .isActive(UPDATED_IS_ACTIVE);
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(updatedTableWaiterAssignment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tableWaiterAssignmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TableWaiterAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTableWaiterAssignmentToMatchAllProperties(updatedTableWaiterAssignment);
    }

    @Test
    void putNonExistingTableWaiterAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableWaiterAssignment.setId(UUID.randomUUID());

        // Create the TableWaiterAssignment
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(tableWaiterAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tableWaiterAssignmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableWaiterAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTableWaiterAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableWaiterAssignment.setId(UUID.randomUUID());

        // Create the TableWaiterAssignment
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(tableWaiterAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableWaiterAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTableWaiterAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableWaiterAssignment.setId(UUID.randomUUID());

        // Create the TableWaiterAssignment
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(tableWaiterAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TableWaiterAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTableWaiterAssignmentWithPatch() throws Exception {
        // Initialize the database
        tableWaiterAssignment.setId(UUID.randomUUID());
        insertedTableWaiterAssignment = tableWaiterAssignmentRepository.save(tableWaiterAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tableWaiterAssignment using partial update
        TableWaiterAssignment partialUpdatedTableWaiterAssignment = new TableWaiterAssignment();
        partialUpdatedTableWaiterAssignment.setId(tableWaiterAssignment.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTableWaiterAssignment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTableWaiterAssignment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TableWaiterAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTableWaiterAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTableWaiterAssignment, tableWaiterAssignment),
            getPersistedTableWaiterAssignment(tableWaiterAssignment)
        );
    }

    @Test
    void fullUpdateTableWaiterAssignmentWithPatch() throws Exception {
        // Initialize the database
        tableWaiterAssignment.setId(UUID.randomUUID());
        insertedTableWaiterAssignment = tableWaiterAssignmentRepository.save(tableWaiterAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tableWaiterAssignment using partial update
        TableWaiterAssignment partialUpdatedTableWaiterAssignment = new TableWaiterAssignment();
        partialUpdatedTableWaiterAssignment.setId(tableWaiterAssignment.getId());

        partialUpdatedTableWaiterAssignment
            .assignmentDate(UPDATED_ASSIGNMENT_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTableWaiterAssignment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTableWaiterAssignment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TableWaiterAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTableWaiterAssignmentUpdatableFieldsEquals(
            partialUpdatedTableWaiterAssignment,
            getPersistedTableWaiterAssignment(partialUpdatedTableWaiterAssignment)
        );
    }

    @Test
    void patchNonExistingTableWaiterAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableWaiterAssignment.setId(UUID.randomUUID());

        // Create the TableWaiterAssignment
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(tableWaiterAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tableWaiterAssignmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableWaiterAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTableWaiterAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableWaiterAssignment.setId(UUID.randomUUID());

        // Create the TableWaiterAssignment
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(tableWaiterAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableWaiterAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTableWaiterAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableWaiterAssignment.setId(UUID.randomUUID());

        // Create the TableWaiterAssignment
        TableWaiterAssignmentDTO tableWaiterAssignmentDTO = tableWaiterAssignmentMapper.toDto(tableWaiterAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tableWaiterAssignmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TableWaiterAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTableWaiterAssignment() {
        // Initialize the database
        tableWaiterAssignment.setId(UUID.randomUUID());
        insertedTableWaiterAssignment = tableWaiterAssignmentRepository.save(tableWaiterAssignment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tableWaiterAssignment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tableWaiterAssignment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tableWaiterAssignmentRepository.count().block();
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

    protected TableWaiterAssignment getPersistedTableWaiterAssignment(TableWaiterAssignment tableWaiterAssignment) {
        return tableWaiterAssignmentRepository.findById(tableWaiterAssignment.getId()).block();
    }

    protected void assertPersistedTableWaiterAssignmentToMatchAllProperties(TableWaiterAssignment expectedTableWaiterAssignment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTableWaiterAssignmentAllPropertiesEquals(expectedTableWaiterAssignment, getPersistedTableWaiterAssignment(expectedTableWaiterAssignment));
        assertTableWaiterAssignmentUpdatableFieldsEquals(
            expectedTableWaiterAssignment,
            getPersistedTableWaiterAssignment(expectedTableWaiterAssignment)
        );
    }

    protected void assertPersistedTableWaiterAssignmentToMatchUpdatableProperties(TableWaiterAssignment expectedTableWaiterAssignment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTableWaiterAssignmentAllUpdatablePropertiesEquals(expectedTableWaiterAssignment, getPersistedTableWaiterAssignment(expectedTableWaiterAssignment));
        assertTableWaiterAssignmentUpdatableFieldsEquals(
            expectedTableWaiterAssignment,
            getPersistedTableWaiterAssignment(expectedTableWaiterAssignment)
        );
    }
}
