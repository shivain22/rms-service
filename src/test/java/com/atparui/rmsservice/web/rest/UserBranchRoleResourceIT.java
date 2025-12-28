package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.UserBranchRoleAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.UserBranchRole;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.UserBranchRoleRepository;
import com.atparui.rmsservice.service.dto.UserBranchRoleDTO;
import com.atparui.rmsservice.service.mapper.UserBranchRoleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link UserBranchRoleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UserBranchRoleResourceIT {

    private static final String DEFAULT_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_ROLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_ASSIGNED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ASSIGNED_BY = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_REVOKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REVOKED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REVOKED_BY = "AAAAAAAAAA";
    private static final String UPDATED_REVOKED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-branch-roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserBranchRoleRepository userBranchRoleRepository;

    @Autowired
    private UserBranchRoleMapper userBranchRoleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserBranchRole userBranchRole;

    private UserBranchRole insertedUserBranchRole;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserBranchRole createEntity() {
        return new UserBranchRole()
            .id(UUID.randomUUID())
            .role(DEFAULT_ROLE)
            .isActive(DEFAULT_IS_ACTIVE)
            .assignedAt(DEFAULT_ASSIGNED_AT)
            .assignedBy(DEFAULT_ASSIGNED_BY)
            .revokedAt(DEFAULT_REVOKED_AT)
            .revokedBy(DEFAULT_REVOKED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserBranchRole createUpdatedEntity() {
        return new UserBranchRole()
            .id(UUID.randomUUID())
            .role(UPDATED_ROLE)
            .isActive(UPDATED_IS_ACTIVE)
            .assignedAt(UPDATED_ASSIGNED_AT)
            .assignedBy(UPDATED_ASSIGNED_BY)
            .revokedAt(UPDATED_REVOKED_AT)
            .revokedBy(UPDATED_REVOKED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserBranchRole.class).block();
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
        userBranchRole = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserBranchRole != null) {
            userBranchRoleRepository.delete(insertedUserBranchRole).block();
            insertedUserBranchRole = null;
        }
        deleteEntities(em);
    }

    @Test
    void createUserBranchRole() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        userBranchRole.setId(null);
        // Create the UserBranchRole
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);
        var returnedUserBranchRoleDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(UserBranchRoleDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the UserBranchRole in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserBranchRole = userBranchRoleMapper.toEntity(returnedUserBranchRoleDTO);
        assertUserBranchRoleUpdatableFieldsEquals(returnedUserBranchRole, getPersistedUserBranchRole(returnedUserBranchRole));

        insertedUserBranchRole = returnedUserBranchRole;
    }

    @Test
    void createUserBranchRoleWithExistingId() throws Exception {
        // Create the UserBranchRole with an existing ID
        insertedUserBranchRole = userBranchRoleRepository.save(userBranchRole).block();
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserBranchRole in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userBranchRole.setRole(null);

        // Create the UserBranchRole, which fails.
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAssignedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userBranchRole.setAssignedAt(null);

        // Create the UserBranchRole, which fails.
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllUserBranchRoles() {
        // Initialize the database
        userBranchRole.setId(UUID.randomUUID());
        insertedUserBranchRole = userBranchRoleRepository.save(userBranchRole).block();

        // Get all the userBranchRoleList
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
            .value(hasItem(userBranchRole.getId().toString()))
            .jsonPath("$.[*].role")
            .value(hasItem(DEFAULT_ROLE))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE))
            .jsonPath("$.[*].assignedAt")
            .value(hasItem(DEFAULT_ASSIGNED_AT.toString()))
            .jsonPath("$.[*].assignedBy")
            .value(hasItem(DEFAULT_ASSIGNED_BY))
            .jsonPath("$.[*].revokedAt")
            .value(hasItem(DEFAULT_REVOKED_AT.toString()))
            .jsonPath("$.[*].revokedBy")
            .value(hasItem(DEFAULT_REVOKED_BY));
    }

    @Test
    void getUserBranchRole() {
        // Initialize the database
        userBranchRole.setId(UUID.randomUUID());
        insertedUserBranchRole = userBranchRoleRepository.save(userBranchRole).block();

        // Get the userBranchRole
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userBranchRole.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userBranchRole.getId().toString()))
            .jsonPath("$.role")
            .value(is(DEFAULT_ROLE))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE))
            .jsonPath("$.assignedAt")
            .value(is(DEFAULT_ASSIGNED_AT.toString()))
            .jsonPath("$.assignedBy")
            .value(is(DEFAULT_ASSIGNED_BY))
            .jsonPath("$.revokedAt")
            .value(is(DEFAULT_REVOKED_AT.toString()))
            .jsonPath("$.revokedBy")
            .value(is(DEFAULT_REVOKED_BY));
    }

    @Test
    void getNonExistingUserBranchRole() {
        // Get the userBranchRole
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUserBranchRole() throws Exception {
        // Initialize the database
        userBranchRole.setId(UUID.randomUUID());
        insertedUserBranchRole = userBranchRoleRepository.save(userBranchRole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userBranchRole
        UserBranchRole updatedUserBranchRole = userBranchRoleRepository.findById(userBranchRole.getId()).block();
        updatedUserBranchRole
            .role(UPDATED_ROLE)
            .isActive(UPDATED_IS_ACTIVE)
            .assignedAt(UPDATED_ASSIGNED_AT)
            .assignedBy(UPDATED_ASSIGNED_BY)
            .revokedAt(UPDATED_REVOKED_AT)
            .revokedBy(UPDATED_REVOKED_BY);
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(updatedUserBranchRole);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userBranchRoleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserBranchRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserBranchRoleToMatchAllProperties(updatedUserBranchRole);
    }

    @Test
    void putNonExistingUserBranchRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userBranchRole.setId(UUID.randomUUID());

        // Create the UserBranchRole
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userBranchRoleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserBranchRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserBranchRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userBranchRole.setId(UUID.randomUUID());

        // Create the UserBranchRole
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserBranchRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserBranchRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userBranchRole.setId(UUID.randomUUID());

        // Create the UserBranchRole
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserBranchRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserBranchRoleWithPatch() throws Exception {
        // Initialize the database
        userBranchRole.setId(UUID.randomUUID());
        insertedUserBranchRole = userBranchRoleRepository.save(userBranchRole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userBranchRole using partial update
        UserBranchRole partialUpdatedUserBranchRole = new UserBranchRole();
        partialUpdatedUserBranchRole.setId(userBranchRole.getId());

        partialUpdatedUserBranchRole.assignedAt(UPDATED_ASSIGNED_AT).assignedBy(UPDATED_ASSIGNED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserBranchRole.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserBranchRole))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserBranchRole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserBranchRoleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserBranchRole, userBranchRole),
            getPersistedUserBranchRole(userBranchRole)
        );
    }

    @Test
    void fullUpdateUserBranchRoleWithPatch() throws Exception {
        // Initialize the database
        userBranchRole.setId(UUID.randomUUID());
        insertedUserBranchRole = userBranchRoleRepository.save(userBranchRole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userBranchRole using partial update
        UserBranchRole partialUpdatedUserBranchRole = new UserBranchRole();
        partialUpdatedUserBranchRole.setId(userBranchRole.getId());

        partialUpdatedUserBranchRole
            .role(UPDATED_ROLE)
            .isActive(UPDATED_IS_ACTIVE)
            .assignedAt(UPDATED_ASSIGNED_AT)
            .assignedBy(UPDATED_ASSIGNED_BY)
            .revokedAt(UPDATED_REVOKED_AT)
            .revokedBy(UPDATED_REVOKED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserBranchRole.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserBranchRole))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserBranchRole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserBranchRoleUpdatableFieldsEquals(partialUpdatedUserBranchRole, getPersistedUserBranchRole(partialUpdatedUserBranchRole));
    }

    @Test
    void patchNonExistingUserBranchRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userBranchRole.setId(UUID.randomUUID());

        // Create the UserBranchRole
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userBranchRoleDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserBranchRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserBranchRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userBranchRole.setId(UUID.randomUUID());

        // Create the UserBranchRole
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserBranchRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserBranchRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userBranchRole.setId(UUID.randomUUID());

        // Create the UserBranchRole
        UserBranchRoleDTO userBranchRoleDTO = userBranchRoleMapper.toDto(userBranchRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userBranchRoleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserBranchRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserBranchRole() {
        // Initialize the database
        userBranchRole.setId(UUID.randomUUID());
        insertedUserBranchRole = userBranchRoleRepository.save(userBranchRole).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userBranchRole
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userBranchRole.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userBranchRoleRepository.count().block();
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

    protected UserBranchRole getPersistedUserBranchRole(UserBranchRole userBranchRole) {
        return userBranchRoleRepository.findById(userBranchRole.getId()).block();
    }

    protected void assertPersistedUserBranchRoleToMatchAllProperties(UserBranchRole expectedUserBranchRole) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserBranchRoleAllPropertiesEquals(expectedUserBranchRole, getPersistedUserBranchRole(expectedUserBranchRole));
        assertUserBranchRoleUpdatableFieldsEquals(expectedUserBranchRole, getPersistedUserBranchRole(expectedUserBranchRole));
    }

    protected void assertPersistedUserBranchRoleToMatchUpdatableProperties(UserBranchRole expectedUserBranchRole) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserBranchRoleAllUpdatablePropertiesEquals(expectedUserBranchRole, getPersistedUserBranchRole(expectedUserBranchRole));
        assertUserBranchRoleUpdatableFieldsEquals(expectedUserBranchRole, getPersistedUserBranchRole(expectedUserBranchRole));
    }
}
