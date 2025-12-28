package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.TableAssignmentAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.TableAssignment;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.TableAssignmentRepository;
import com.atparui.rmsservice.service.dto.TableAssignmentDTO;
import com.atparui.rmsservice.service.mapper.TableAssignmentMapper;
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
 * Integration tests for the {@link TableAssignmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TableAssignmentResourceIT {

    private static final LocalDate DEFAULT_ASSIGNMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ASSIGNMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/table-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TableAssignmentRepository tableAssignmentRepository;

    @Autowired
    private TableAssignmentMapper tableAssignmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TableAssignment tableAssignment;

    private TableAssignment insertedTableAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TableAssignment createEntity() {
        return new TableAssignment()
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
    public static TableAssignment createUpdatedEntity() {
        return new TableAssignment()
            .id(UUID.randomUUID())
            .assignmentDate(UPDATED_ASSIGNMENT_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TableAssignment.class).block();
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
        tableAssignment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTableAssignment != null) {
            tableAssignmentRepository.delete(insertedTableAssignment).block();
            insertedTableAssignment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTableAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        tableAssignment.setId(null);
        // Create the TableAssignment
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(tableAssignment);
        var returnedTableAssignmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TableAssignmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TableAssignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTableAssignment = tableAssignmentMapper.toEntity(returnedTableAssignmentDTO);
        assertTableAssignmentUpdatableFieldsEquals(returnedTableAssignment, getPersistedTableAssignment(returnedTableAssignment));

        insertedTableAssignment = returnedTableAssignment;
    }

    @Test
    void createTableAssignmentWithExistingId() throws Exception {
        // Create the TableAssignment with an existing ID
        insertedTableAssignment = tableAssignmentRepository.save(tableAssignment).block();
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(tableAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAssignmentDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tableAssignment.setAssignmentDate(null);

        // Create the TableAssignment, which fails.
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(tableAssignment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTableAssignmentsAsStream() {
        // Initialize the database
        tableAssignment.setId(UUID.randomUUID());
        tableAssignmentRepository.save(tableAssignment).block();

        List<TableAssignment> tableAssignmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TableAssignmentDTO.class)
            .getResponseBody()
            .map(tableAssignmentMapper::toEntity)
            .filter(tableAssignment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tableAssignmentList).isNotNull();
        assertThat(tableAssignmentList).hasSize(1);
        TableAssignment testTableAssignment = tableAssignmentList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertTableAssignmentAllPropertiesEquals(tableAssignment, testTableAssignment);
        assertTableAssignmentUpdatableFieldsEquals(tableAssignment, testTableAssignment);
    }

    @Test
    void getAllTableAssignments() {
        // Initialize the database
        tableAssignment.setId(UUID.randomUUID());
        insertedTableAssignment = tableAssignmentRepository.save(tableAssignment).block();

        // Get all the tableAssignmentList
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
            .value(hasItem(tableAssignment.getId().toString()))
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
    void getTableAssignment() {
        // Initialize the database
        tableAssignment.setId(UUID.randomUUID());
        insertedTableAssignment = tableAssignmentRepository.save(tableAssignment).block();

        // Get the tableAssignment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tableAssignment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tableAssignment.getId().toString()))
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
    void getNonExistingTableAssignment() {
        // Get the tableAssignment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTableAssignment() throws Exception {
        // Initialize the database
        tableAssignment.setId(UUID.randomUUID());
        insertedTableAssignment = tableAssignmentRepository.save(tableAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tableAssignment
        TableAssignment updatedTableAssignment = tableAssignmentRepository.findById(tableAssignment.getId()).block();
        updatedTableAssignment
            .assignmentDate(UPDATED_ASSIGNMENT_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .isActive(UPDATED_IS_ACTIVE);
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(updatedTableAssignment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tableAssignmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTableAssignmentToMatchAllProperties(updatedTableAssignment);
    }

    @Test
    void putNonExistingTableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableAssignment.setId(UUID.randomUUID());

        // Create the TableAssignment
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(tableAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tableAssignmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableAssignment.setId(UUID.randomUUID());

        // Create the TableAssignment
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(tableAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableAssignment.setId(UUID.randomUUID());

        // Create the TableAssignment
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(tableAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTableAssignmentWithPatch() throws Exception {
        // Initialize the database
        tableAssignment.setId(UUID.randomUUID());
        insertedTableAssignment = tableAssignmentRepository.save(tableAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tableAssignment using partial update
        TableAssignment partialUpdatedTableAssignment = new TableAssignment();
        partialUpdatedTableAssignment.setId(tableAssignment.getId());

        partialUpdatedTableAssignment.assignmentDate(UPDATED_ASSIGNMENT_DATE).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTableAssignment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTableAssignment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TableAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTableAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTableAssignment, tableAssignment),
            getPersistedTableAssignment(tableAssignment)
        );
    }

    @Test
    void fullUpdateTableAssignmentWithPatch() throws Exception {
        // Initialize the database
        tableAssignment.setId(UUID.randomUUID());
        insertedTableAssignment = tableAssignmentRepository.save(tableAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tableAssignment using partial update
        TableAssignment partialUpdatedTableAssignment = new TableAssignment();
        partialUpdatedTableAssignment.setId(tableAssignment.getId());

        partialUpdatedTableAssignment
            .assignmentDate(UPDATED_ASSIGNMENT_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTableAssignment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTableAssignment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TableAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTableAssignmentUpdatableFieldsEquals(
            partialUpdatedTableAssignment,
            getPersistedTableAssignment(partialUpdatedTableAssignment)
        );
    }

    @Test
    void patchNonExistingTableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableAssignment.setId(UUID.randomUUID());

        // Create the TableAssignment
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(tableAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tableAssignmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableAssignment.setId(UUID.randomUUID());

        // Create the TableAssignment
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(tableAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tableAssignment.setId(UUID.randomUUID());

        // Create the TableAssignment
        TableAssignmentDTO tableAssignmentDTO = tableAssignmentMapper.toDto(tableAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tableAssignmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTableAssignment() {
        // Initialize the database
        tableAssignment.setId(UUID.randomUUID());
        insertedTableAssignment = tableAssignmentRepository.save(tableAssignment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tableAssignment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tableAssignment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tableAssignmentRepository.count().block();
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

    protected TableAssignment getPersistedTableAssignment(TableAssignment tableAssignment) {
        return tableAssignmentRepository.findById(tableAssignment.getId()).block();
    }

    protected void assertPersistedTableAssignmentToMatchAllProperties(TableAssignment expectedTableAssignment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTableAssignmentAllPropertiesEquals(expectedTableAssignment, getPersistedTableAssignment(expectedTableAssignment));
        assertTableAssignmentUpdatableFieldsEquals(expectedTableAssignment, getPersistedTableAssignment(expectedTableAssignment));
    }

    protected void assertPersistedTableAssignmentToMatchUpdatableProperties(TableAssignment expectedTableAssignment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTableAssignmentAllUpdatablePropertiesEquals(expectedTableAssignment, getPersistedTableAssignment(expectedTableAssignment));
        assertTableAssignmentUpdatableFieldsEquals(expectedTableAssignment, getPersistedTableAssignment(expectedTableAssignment));
    }
}
