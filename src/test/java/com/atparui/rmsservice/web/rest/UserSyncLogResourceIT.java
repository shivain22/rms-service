package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.UserSyncLogAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.UserSyncLog;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.UserSyncLogRepository;
import com.atparui.rmsservice.service.dto.UserSyncLogDTO;
import com.atparui.rmsservice.service.mapper.UserSyncLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link UserSyncLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UserSyncLogResourceIT {

    private static final String DEFAULT_SYNC_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SYNC_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_SYNC_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_SYNC_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_EXTERNAL_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_USER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_REQUEST_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_REQUEST_PAYLOAD = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_PAYLOAD = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_SYNCED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SYNCED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SYNCED_BY = "AAAAAAAAAA";
    private static final String UPDATED_SYNCED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-sync-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserSyncLogRepository userSyncLogRepository;

    @Autowired
    private UserSyncLogMapper userSyncLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserSyncLog userSyncLog;

    private UserSyncLog insertedUserSyncLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserSyncLog createEntity() {
        return new UserSyncLog()
            .id(UUID.randomUUID())
            .syncType(DEFAULT_SYNC_TYPE)
            .syncStatus(DEFAULT_SYNC_STATUS)
            .externalUserId(DEFAULT_EXTERNAL_USER_ID)
            .requestPayload(DEFAULT_REQUEST_PAYLOAD)
            .responsePayload(DEFAULT_RESPONSE_PAYLOAD)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .syncedAt(DEFAULT_SYNCED_AT)
            .syncedBy(DEFAULT_SYNCED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserSyncLog createUpdatedEntity() {
        return new UserSyncLog()
            .id(UUID.randomUUID())
            .syncType(UPDATED_SYNC_TYPE)
            .syncStatus(UPDATED_SYNC_STATUS)
            .externalUserId(UPDATED_EXTERNAL_USER_ID)
            .requestPayload(UPDATED_REQUEST_PAYLOAD)
            .responsePayload(UPDATED_RESPONSE_PAYLOAD)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .syncedAt(UPDATED_SYNCED_AT)
            .syncedBy(UPDATED_SYNCED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserSyncLog.class).block();
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
        userSyncLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserSyncLog != null) {
            userSyncLogRepository.delete(insertedUserSyncLog).block();
            insertedUserSyncLog = null;
        }
        deleteEntities(em);
    }

    @Test
    void createUserSyncLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        userSyncLog.setId(null);
        // Create the UserSyncLog
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);
        var returnedUserSyncLogDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(UserSyncLogDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the UserSyncLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserSyncLog = userSyncLogMapper.toEntity(returnedUserSyncLogDTO);
        assertUserSyncLogUpdatableFieldsEquals(returnedUserSyncLog, getPersistedUserSyncLog(returnedUserSyncLog));

        insertedUserSyncLog = returnedUserSyncLog;
    }

    @Test
    void createUserSyncLogWithExistingId() throws Exception {
        // Create the UserSyncLog with an existing ID
        insertedUserSyncLog = userSyncLogRepository.save(userSyncLog).block();
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkSyncTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userSyncLog.setSyncType(null);

        // Create the UserSyncLog, which fails.
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSyncStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userSyncLog.setSyncStatus(null);

        // Create the UserSyncLog, which fails.
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSyncedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userSyncLog.setSyncedAt(null);

        // Create the UserSyncLog, which fails.
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllUserSyncLogsAsStream() {
        // Initialize the database
        userSyncLog.setId(UUID.randomUUID());
        userSyncLogRepository.save(userSyncLog).block();

        List<UserSyncLog> userSyncLogList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(UserSyncLogDTO.class)
            .getResponseBody()
            .map(userSyncLogMapper::toEntity)
            .filter(userSyncLog::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(userSyncLogList).isNotNull();
        assertThat(userSyncLogList).hasSize(1);
        UserSyncLog testUserSyncLog = userSyncLogList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertUserSyncLogAllPropertiesEquals(userSyncLog, testUserSyncLog);
        assertUserSyncLogUpdatableFieldsEquals(userSyncLog, testUserSyncLog);
    }

    @Test
    void getAllUserSyncLogs() {
        // Initialize the database
        userSyncLog.setId(UUID.randomUUID());
        insertedUserSyncLog = userSyncLogRepository.save(userSyncLog).block();

        // Get all the userSyncLogList
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
            .value(hasItem(userSyncLog.getId().toString()))
            .jsonPath("$.[*].syncType")
            .value(hasItem(DEFAULT_SYNC_TYPE))
            .jsonPath("$.[*].syncStatus")
            .value(hasItem(DEFAULT_SYNC_STATUS))
            .jsonPath("$.[*].externalUserId")
            .value(hasItem(DEFAULT_EXTERNAL_USER_ID))
            .jsonPath("$.[*].requestPayload")
            .value(hasItem(DEFAULT_REQUEST_PAYLOAD))
            .jsonPath("$.[*].responsePayload")
            .value(hasItem(DEFAULT_RESPONSE_PAYLOAD))
            .jsonPath("$.[*].errorMessage")
            .value(hasItem(DEFAULT_ERROR_MESSAGE))
            .jsonPath("$.[*].syncedAt")
            .value(hasItem(DEFAULT_SYNCED_AT.toString()))
            .jsonPath("$.[*].syncedBy")
            .value(hasItem(DEFAULT_SYNCED_BY));
    }

    @Test
    void getUserSyncLog() {
        // Initialize the database
        userSyncLog.setId(UUID.randomUUID());
        insertedUserSyncLog = userSyncLogRepository.save(userSyncLog).block();

        // Get the userSyncLog
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userSyncLog.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userSyncLog.getId().toString()))
            .jsonPath("$.syncType")
            .value(is(DEFAULT_SYNC_TYPE))
            .jsonPath("$.syncStatus")
            .value(is(DEFAULT_SYNC_STATUS))
            .jsonPath("$.externalUserId")
            .value(is(DEFAULT_EXTERNAL_USER_ID))
            .jsonPath("$.requestPayload")
            .value(is(DEFAULT_REQUEST_PAYLOAD))
            .jsonPath("$.responsePayload")
            .value(is(DEFAULT_RESPONSE_PAYLOAD))
            .jsonPath("$.errorMessage")
            .value(is(DEFAULT_ERROR_MESSAGE))
            .jsonPath("$.syncedAt")
            .value(is(DEFAULT_SYNCED_AT.toString()))
            .jsonPath("$.syncedBy")
            .value(is(DEFAULT_SYNCED_BY));
    }

    @Test
    void getNonExistingUserSyncLog() {
        // Get the userSyncLog
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUserSyncLog() throws Exception {
        // Initialize the database
        userSyncLog.setId(UUID.randomUUID());
        insertedUserSyncLog = userSyncLogRepository.save(userSyncLog).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userSyncLog
        UserSyncLog updatedUserSyncLog = userSyncLogRepository.findById(userSyncLog.getId()).block();
        updatedUserSyncLog
            .syncType(UPDATED_SYNC_TYPE)
            .syncStatus(UPDATED_SYNC_STATUS)
            .externalUserId(UPDATED_EXTERNAL_USER_ID)
            .requestPayload(UPDATED_REQUEST_PAYLOAD)
            .responsePayload(UPDATED_RESPONSE_PAYLOAD)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .syncedAt(UPDATED_SYNCED_AT)
            .syncedBy(UPDATED_SYNCED_BY);
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(updatedUserSyncLog);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userSyncLogDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserSyncLogToMatchAllProperties(updatedUserSyncLog);
    }

    @Test
    void putNonExistingUserSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSyncLog.setId(UUID.randomUUID());

        // Create the UserSyncLog
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userSyncLogDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSyncLog.setId(UUID.randomUUID());

        // Create the UserSyncLog
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSyncLog.setId(UUID.randomUUID());

        // Create the UserSyncLog
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserSyncLogWithPatch() throws Exception {
        // Initialize the database
        userSyncLog.setId(UUID.randomUUID());
        insertedUserSyncLog = userSyncLogRepository.save(userSyncLog).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userSyncLog using partial update
        UserSyncLog partialUpdatedUserSyncLog = new UserSyncLog();
        partialUpdatedUserSyncLog.setId(userSyncLog.getId());

        partialUpdatedUserSyncLog
            .syncType(UPDATED_SYNC_TYPE)
            .syncStatus(UPDATED_SYNC_STATUS)
            .externalUserId(UPDATED_EXTERNAL_USER_ID)
            .errorMessage(UPDATED_ERROR_MESSAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserSyncLog.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserSyncLog))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserSyncLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserSyncLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserSyncLog, userSyncLog),
            getPersistedUserSyncLog(userSyncLog)
        );
    }

    @Test
    void fullUpdateUserSyncLogWithPatch() throws Exception {
        // Initialize the database
        userSyncLog.setId(UUID.randomUUID());
        insertedUserSyncLog = userSyncLogRepository.save(userSyncLog).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userSyncLog using partial update
        UserSyncLog partialUpdatedUserSyncLog = new UserSyncLog();
        partialUpdatedUserSyncLog.setId(userSyncLog.getId());

        partialUpdatedUserSyncLog
            .syncType(UPDATED_SYNC_TYPE)
            .syncStatus(UPDATED_SYNC_STATUS)
            .externalUserId(UPDATED_EXTERNAL_USER_ID)
            .requestPayload(UPDATED_REQUEST_PAYLOAD)
            .responsePayload(UPDATED_RESPONSE_PAYLOAD)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .syncedAt(UPDATED_SYNCED_AT)
            .syncedBy(UPDATED_SYNCED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserSyncLog.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserSyncLog))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserSyncLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserSyncLogUpdatableFieldsEquals(partialUpdatedUserSyncLog, getPersistedUserSyncLog(partialUpdatedUserSyncLog));
    }

    @Test
    void patchNonExistingUserSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSyncLog.setId(UUID.randomUUID());

        // Create the UserSyncLog
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userSyncLogDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSyncLog.setId(UUID.randomUUID());

        // Create the UserSyncLog
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserSyncLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSyncLog.setId(UUID.randomUUID());

        // Create the UserSyncLog
        UserSyncLogDTO userSyncLogDTO = userSyncLogMapper.toDto(userSyncLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userSyncLogDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserSyncLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserSyncLog() {
        // Initialize the database
        userSyncLog.setId(UUID.randomUUID());
        insertedUserSyncLog = userSyncLogRepository.save(userSyncLog).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userSyncLog
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userSyncLog.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userSyncLogRepository.count().block();
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

    protected UserSyncLog getPersistedUserSyncLog(UserSyncLog userSyncLog) {
        return userSyncLogRepository.findById(userSyncLog.getId()).block();
    }

    protected void assertPersistedUserSyncLogToMatchAllProperties(UserSyncLog expectedUserSyncLog) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserSyncLogAllPropertiesEquals(expectedUserSyncLog, getPersistedUserSyncLog(expectedUserSyncLog));
        assertUserSyncLogUpdatableFieldsEquals(expectedUserSyncLog, getPersistedUserSyncLog(expectedUserSyncLog));
    }

    protected void assertPersistedUserSyncLogToMatchUpdatableProperties(UserSyncLog expectedUserSyncLog) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserSyncLogAllUpdatablePropertiesEquals(expectedUserSyncLog, getPersistedUserSyncLog(expectedUserSyncLog));
        assertUserSyncLogUpdatableFieldsEquals(expectedUserSyncLog, getPersistedUserSyncLog(expectedUserSyncLog));
    }
}
