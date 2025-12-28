package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.RmsUserAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.RmsUser;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.RmsUserRepository;
import com.atparui.rmsservice.repository.search.RmsUserSearchRepository;
import com.atparui.rmsservice.service.dto.RmsUserDTO;
import com.atparui.rmsservice.service.mapper.RmsUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link RmsUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RmsUserResourceIT {

    private static final String DEFAULT_EXTERNAL_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_USER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DISPLAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PROFILE_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE_IMAGE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_LAST_SYNC_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_SYNC_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SYNC_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_SYNC_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_SYNC_ERROR = "AAAAAAAAAA";
    private static final String UPDATED_SYNC_ERROR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rms-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/rms-users/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RmsUserRepository rmsUserRepository;

    @Autowired
    private RmsUserMapper rmsUserMapper;

    @Autowired
    private RmsUserSearchRepository rmsUserSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RmsUser rmsUser;

    private RmsUser insertedRmsUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RmsUser createEntity() {
        return new RmsUser()
            .id(UUID.randomUUID())
            .externalUserId(DEFAULT_EXTERNAL_USER_ID)
            .username(DEFAULT_USERNAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .displayName(DEFAULT_DISPLAY_NAME)
            .profileImageUrl(DEFAULT_PROFILE_IMAGE_URL)
            .isActive(DEFAULT_IS_ACTIVE)
            .lastSyncAt(DEFAULT_LAST_SYNC_AT)
            .syncStatus(DEFAULT_SYNC_STATUS)
            .syncError(DEFAULT_SYNC_ERROR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RmsUser createUpdatedEntity() {
        return new RmsUser()
            .id(UUID.randomUUID())
            .externalUserId(UPDATED_EXTERNAL_USER_ID)
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .displayName(UPDATED_DISPLAY_NAME)
            .profileImageUrl(UPDATED_PROFILE_IMAGE_URL)
            .isActive(UPDATED_IS_ACTIVE)
            .lastSyncAt(UPDATED_LAST_SYNC_AT)
            .syncStatus(UPDATED_SYNC_STATUS)
            .syncError(UPDATED_SYNC_ERROR);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RmsUser.class).block();
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
        rmsUser = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRmsUser != null) {
            rmsUserRepository.delete(insertedRmsUser).block();
            rmsUserSearchRepository.delete(insertedRmsUser).block();
            insertedRmsUser = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRmsUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        rmsUser.setId(null);
        // Create the RmsUser
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);
        var returnedRmsUserDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RmsUserDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the RmsUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRmsUser = rmsUserMapper.toEntity(returnedRmsUserDTO);
        assertRmsUserUpdatableFieldsEquals(returnedRmsUser, getPersistedRmsUser(returnedRmsUser));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedRmsUser = returnedRmsUser;
    }

    @Test
    void createRmsUserWithExistingId() throws Exception {
        // Create the RmsUser with an existing ID
        insertedRmsUser = rmsUserRepository.save(rmsUser).block();
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RmsUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkExternalUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        // set the field null
        rmsUser.setExternalUserId(null);

        // Create the RmsUser, which fails.
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        // set the field null
        rmsUser.setUsername(null);

        // Create the RmsUser, which fails.
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllRmsUsers() {
        // Initialize the database
        rmsUser.setId(UUID.randomUUID());
        insertedRmsUser = rmsUserRepository.save(rmsUser).block();

        // Get all the rmsUserList
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
            .value(hasItem(rmsUser.getId().toString()))
            .jsonPath("$.[*].externalUserId")
            .value(hasItem(DEFAULT_EXTERNAL_USER_ID))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].displayName")
            .value(hasItem(DEFAULT_DISPLAY_NAME))
            .jsonPath("$.[*].profileImageUrl")
            .value(hasItem(DEFAULT_PROFILE_IMAGE_URL))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE))
            .jsonPath("$.[*].lastSyncAt")
            .value(hasItem(DEFAULT_LAST_SYNC_AT.toString()))
            .jsonPath("$.[*].syncStatus")
            .value(hasItem(DEFAULT_SYNC_STATUS))
            .jsonPath("$.[*].syncError")
            .value(hasItem(DEFAULT_SYNC_ERROR));
    }

    @Test
    void getRmsUser() {
        // Initialize the database
        rmsUser.setId(UUID.randomUUID());
        insertedRmsUser = rmsUserRepository.save(rmsUser).block();

        // Get the rmsUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rmsUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rmsUser.getId().toString()))
            .jsonPath("$.externalUserId")
            .value(is(DEFAULT_EXTERNAL_USER_ID))
            .jsonPath("$.username")
            .value(is(DEFAULT_USERNAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.displayName")
            .value(is(DEFAULT_DISPLAY_NAME))
            .jsonPath("$.profileImageUrl")
            .value(is(DEFAULT_PROFILE_IMAGE_URL))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE))
            .jsonPath("$.lastSyncAt")
            .value(is(DEFAULT_LAST_SYNC_AT.toString()))
            .jsonPath("$.syncStatus")
            .value(is(DEFAULT_SYNC_STATUS))
            .jsonPath("$.syncError")
            .value(is(DEFAULT_SYNC_ERROR));
    }

    @Test
    void getNonExistingRmsUser() {
        // Get the rmsUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRmsUser() throws Exception {
        // Initialize the database
        rmsUser.setId(UUID.randomUUID());
        insertedRmsUser = rmsUserRepository.save(rmsUser).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        rmsUserSearchRepository.save(rmsUser).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());

        // Update the rmsUser
        RmsUser updatedRmsUser = rmsUserRepository.findById(rmsUser.getId()).block();
        updatedRmsUser
            .externalUserId(UPDATED_EXTERNAL_USER_ID)
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .displayName(UPDATED_DISPLAY_NAME)
            .profileImageUrl(UPDATED_PROFILE_IMAGE_URL)
            .isActive(UPDATED_IS_ACTIVE)
            .lastSyncAt(UPDATED_LAST_SYNC_AT)
            .syncStatus(UPDATED_SYNC_STATUS)
            .syncError(UPDATED_SYNC_ERROR);
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(updatedRmsUser);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rmsUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RmsUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRmsUserToMatchAllProperties(updatedRmsUser);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<RmsUser> rmsUserSearchList = Streamable.of(rmsUserSearchRepository.findAll().collectList().block()).toList();
                RmsUser testRmsUserSearch = rmsUserSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertRmsUserAllPropertiesEquals(testRmsUserSearch, updatedRmsUser);
                assertRmsUserUpdatableFieldsEquals(testRmsUserSearch, updatedRmsUser);
            });
    }

    @Test
    void putNonExistingRmsUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        rmsUser.setId(UUID.randomUUID());

        // Create the RmsUser
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rmsUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RmsUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchRmsUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        rmsUser.setId(UUID.randomUUID());

        // Create the RmsUser
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RmsUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamRmsUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        rmsUser.setId(UUID.randomUUID());

        // Create the RmsUser
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RmsUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateRmsUserWithPatch() throws Exception {
        // Initialize the database
        rmsUser.setId(UUID.randomUUID());
        insertedRmsUser = rmsUserRepository.save(rmsUser).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rmsUser using partial update
        RmsUser partialUpdatedRmsUser = new RmsUser();
        partialUpdatedRmsUser.setId(rmsUser.getId());

        partialUpdatedRmsUser
            .externalUserId(UPDATED_EXTERNAL_USER_ID)
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .displayName(UPDATED_DISPLAY_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .lastSyncAt(UPDATED_LAST_SYNC_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRmsUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRmsUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RmsUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRmsUserUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRmsUser, rmsUser), getPersistedRmsUser(rmsUser));
    }

    @Test
    void fullUpdateRmsUserWithPatch() throws Exception {
        // Initialize the database
        rmsUser.setId(UUID.randomUUID());
        insertedRmsUser = rmsUserRepository.save(rmsUser).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rmsUser using partial update
        RmsUser partialUpdatedRmsUser = new RmsUser();
        partialUpdatedRmsUser.setId(rmsUser.getId());

        partialUpdatedRmsUser
            .externalUserId(UPDATED_EXTERNAL_USER_ID)
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .displayName(UPDATED_DISPLAY_NAME)
            .profileImageUrl(UPDATED_PROFILE_IMAGE_URL)
            .isActive(UPDATED_IS_ACTIVE)
            .lastSyncAt(UPDATED_LAST_SYNC_AT)
            .syncStatus(UPDATED_SYNC_STATUS)
            .syncError(UPDATED_SYNC_ERROR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRmsUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRmsUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RmsUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRmsUserUpdatableFieldsEquals(partialUpdatedRmsUser, getPersistedRmsUser(partialUpdatedRmsUser));
    }

    @Test
    void patchNonExistingRmsUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        rmsUser.setId(UUID.randomUUID());

        // Create the RmsUser
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rmsUserDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RmsUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchRmsUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        rmsUser.setId(UUID.randomUUID());

        // Create the RmsUser
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RmsUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamRmsUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        rmsUser.setId(UUID.randomUUID());

        // Create the RmsUser
        RmsUserDTO rmsUserDTO = rmsUserMapper.toDto(rmsUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rmsUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RmsUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteRmsUser() {
        // Initialize the database
        rmsUser.setId(UUID.randomUUID());
        insertedRmsUser = rmsUserRepository.save(rmsUser).block();
        rmsUserRepository.save(rmsUser).block();
        rmsUserSearchRepository.save(rmsUser).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the rmsUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rmsUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(rmsUserSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchRmsUser() {
        // Initialize the database
        rmsUser.setId(UUID.randomUUID());
        insertedRmsUser = rmsUserRepository.save(rmsUser).block();
        rmsUserSearchRepository.save(rmsUser).block();

        // Search the rmsUser
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + rmsUser.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(rmsUser.getId().toString()))
            .jsonPath("$.[*].externalUserId")
            .value(hasItem(DEFAULT_EXTERNAL_USER_ID))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].displayName")
            .value(hasItem(DEFAULT_DISPLAY_NAME))
            .jsonPath("$.[*].profileImageUrl")
            .value(hasItem(DEFAULT_PROFILE_IMAGE_URL))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE))
            .jsonPath("$.[*].lastSyncAt")
            .value(hasItem(DEFAULT_LAST_SYNC_AT.toString()))
            .jsonPath("$.[*].syncStatus")
            .value(hasItem(DEFAULT_SYNC_STATUS))
            .jsonPath("$.[*].syncError")
            .value(hasItem(DEFAULT_SYNC_ERROR.toString()));
    }

    protected long getRepositoryCount() {
        return rmsUserRepository.count().block();
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

    protected RmsUser getPersistedRmsUser(RmsUser rmsUser) {
        return rmsUserRepository.findById(rmsUser.getId()).block();
    }

    protected void assertPersistedRmsUserToMatchAllProperties(RmsUser expectedRmsUser) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRmsUserAllPropertiesEquals(expectedRmsUser, getPersistedRmsUser(expectedRmsUser));
        assertRmsUserUpdatableFieldsEquals(expectedRmsUser, getPersistedRmsUser(expectedRmsUser));
    }

    protected void assertPersistedRmsUserToMatchUpdatableProperties(RmsUser expectedRmsUser) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRmsUserAllUpdatablePropertiesEquals(expectedRmsUser, getPersistedRmsUser(expectedRmsUser));
        assertRmsUserUpdatableFieldsEquals(expectedRmsUser, getPersistedRmsUser(expectedRmsUser));
    }
}
