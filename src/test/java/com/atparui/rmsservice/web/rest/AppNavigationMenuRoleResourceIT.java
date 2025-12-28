package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.AppNavigationMenuRoleAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.AppNavigationMenuRole;
import com.atparui.rmsservice.repository.AppNavigationMenuRoleRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.service.dto.AppNavigationMenuRoleDTO;
import com.atparui.rmsservice.service.mapper.AppNavigationMenuRoleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link AppNavigationMenuRoleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AppNavigationMenuRoleResourceIT {

    private static final String DEFAULT_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_ROLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/app-navigation-menu-roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppNavigationMenuRoleRepository appNavigationMenuRoleRepository;

    @Autowired
    private AppNavigationMenuRoleMapper appNavigationMenuRoleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AppNavigationMenuRole appNavigationMenuRole;

    private AppNavigationMenuRole insertedAppNavigationMenuRole;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppNavigationMenuRole createEntity() {
        return new AppNavigationMenuRole().id(UUID.randomUUID()).role(DEFAULT_ROLE).isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppNavigationMenuRole createUpdatedEntity() {
        return new AppNavigationMenuRole().id(UUID.randomUUID()).role(UPDATED_ROLE).isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AppNavigationMenuRole.class).block();
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
        appNavigationMenuRole = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppNavigationMenuRole != null) {
            appNavigationMenuRoleRepository.delete(insertedAppNavigationMenuRole).block();
            insertedAppNavigationMenuRole = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAppNavigationMenuRole() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        appNavigationMenuRole.setId(null);
        // Create the AppNavigationMenuRole
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(appNavigationMenuRole);
        var returnedAppNavigationMenuRoleDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AppNavigationMenuRoleDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AppNavigationMenuRole in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppNavigationMenuRole = appNavigationMenuRoleMapper.toEntity(returnedAppNavigationMenuRoleDTO);
        assertAppNavigationMenuRoleUpdatableFieldsEquals(
            returnedAppNavigationMenuRole,
            getPersistedAppNavigationMenuRole(returnedAppNavigationMenuRole)
        );

        insertedAppNavigationMenuRole = returnedAppNavigationMenuRole;
    }

    @Test
    void createAppNavigationMenuRoleWithExistingId() throws Exception {
        // Create the AppNavigationMenuRole with an existing ID
        insertedAppNavigationMenuRole = appNavigationMenuRoleRepository.save(appNavigationMenuRole).block();
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(appNavigationMenuRole);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuRole in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appNavigationMenuRole.setRole(null);

        // Create the AppNavigationMenuRole, which fails.
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(appNavigationMenuRole);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAppNavigationMenuRoles() {
        // Initialize the database
        appNavigationMenuRole.setId(UUID.randomUUID());
        insertedAppNavigationMenuRole = appNavigationMenuRoleRepository.save(appNavigationMenuRole).block();

        // Get all the appNavigationMenuRoleList
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
            .value(hasItem(appNavigationMenuRole.getId().toString()))
            .jsonPath("$.[*].role")
            .value(hasItem(DEFAULT_ROLE))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getAppNavigationMenuRole() {
        // Initialize the database
        appNavigationMenuRole.setId(UUID.randomUUID());
        insertedAppNavigationMenuRole = appNavigationMenuRoleRepository.save(appNavigationMenuRole).block();

        // Get the appNavigationMenuRole
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appNavigationMenuRole.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appNavigationMenuRole.getId().toString()))
            .jsonPath("$.role")
            .value(is(DEFAULT_ROLE))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingAppNavigationMenuRole() {
        // Get the appNavigationMenuRole
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAppNavigationMenuRole() throws Exception {
        // Initialize the database
        appNavigationMenuRole.setId(UUID.randomUUID());
        insertedAppNavigationMenuRole = appNavigationMenuRoleRepository.save(appNavigationMenuRole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appNavigationMenuRole
        AppNavigationMenuRole updatedAppNavigationMenuRole = appNavigationMenuRoleRepository
            .findById(appNavigationMenuRole.getId())
            .block();
        updatedAppNavigationMenuRole.role(UPDATED_ROLE).isActive(UPDATED_IS_ACTIVE);
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(updatedAppNavigationMenuRole);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appNavigationMenuRoleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppNavigationMenuRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppNavigationMenuRoleToMatchAllProperties(updatedAppNavigationMenuRole);
    }

    @Test
    void putNonExistingAppNavigationMenuRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuRole.setId(UUID.randomUUID());

        // Create the AppNavigationMenuRole
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(appNavigationMenuRole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appNavigationMenuRoleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAppNavigationMenuRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuRole.setId(UUID.randomUUID());

        // Create the AppNavigationMenuRole
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(appNavigationMenuRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAppNavigationMenuRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuRole.setId(UUID.randomUUID());

        // Create the AppNavigationMenuRole
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(appNavigationMenuRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppNavigationMenuRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAppNavigationMenuRoleWithPatch() throws Exception {
        // Initialize the database
        appNavigationMenuRole.setId(UUID.randomUUID());
        insertedAppNavigationMenuRole = appNavigationMenuRoleRepository.save(appNavigationMenuRole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appNavigationMenuRole using partial update
        AppNavigationMenuRole partialUpdatedAppNavigationMenuRole = new AppNavigationMenuRole();
        partialUpdatedAppNavigationMenuRole.setId(appNavigationMenuRole.getId());

        partialUpdatedAppNavigationMenuRole.role(UPDATED_ROLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppNavigationMenuRole.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppNavigationMenuRole))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppNavigationMenuRole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppNavigationMenuRoleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAppNavigationMenuRole, appNavigationMenuRole),
            getPersistedAppNavigationMenuRole(appNavigationMenuRole)
        );
    }

    @Test
    void fullUpdateAppNavigationMenuRoleWithPatch() throws Exception {
        // Initialize the database
        appNavigationMenuRole.setId(UUID.randomUUID());
        insertedAppNavigationMenuRole = appNavigationMenuRoleRepository.save(appNavigationMenuRole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appNavigationMenuRole using partial update
        AppNavigationMenuRole partialUpdatedAppNavigationMenuRole = new AppNavigationMenuRole();
        partialUpdatedAppNavigationMenuRole.setId(appNavigationMenuRole.getId());

        partialUpdatedAppNavigationMenuRole.role(UPDATED_ROLE).isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppNavigationMenuRole.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppNavigationMenuRole))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppNavigationMenuRole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppNavigationMenuRoleUpdatableFieldsEquals(
            partialUpdatedAppNavigationMenuRole,
            getPersistedAppNavigationMenuRole(partialUpdatedAppNavigationMenuRole)
        );
    }

    @Test
    void patchNonExistingAppNavigationMenuRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuRole.setId(UUID.randomUUID());

        // Create the AppNavigationMenuRole
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(appNavigationMenuRole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appNavigationMenuRoleDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAppNavigationMenuRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuRole.setId(UUID.randomUUID());

        // Create the AppNavigationMenuRole
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(appNavigationMenuRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAppNavigationMenuRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuRole.setId(UUID.randomUUID());

        // Create the AppNavigationMenuRole
        AppNavigationMenuRoleDTO appNavigationMenuRoleDTO = appNavigationMenuRoleMapper.toDto(appNavigationMenuRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appNavigationMenuRoleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppNavigationMenuRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAppNavigationMenuRole() {
        // Initialize the database
        appNavigationMenuRole.setId(UUID.randomUUID());
        insertedAppNavigationMenuRole = appNavigationMenuRoleRepository.save(appNavigationMenuRole).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appNavigationMenuRole
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appNavigationMenuRole.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appNavigationMenuRoleRepository.count().block();
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

    protected AppNavigationMenuRole getPersistedAppNavigationMenuRole(AppNavigationMenuRole appNavigationMenuRole) {
        return appNavigationMenuRoleRepository.findById(appNavigationMenuRole.getId()).block();
    }

    protected void assertPersistedAppNavigationMenuRoleToMatchAllProperties(AppNavigationMenuRole expectedAppNavigationMenuRole) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppNavigationMenuRoleAllPropertiesEquals(expectedAppNavigationMenuRole, getPersistedAppNavigationMenuRole(expectedAppNavigationMenuRole));
        assertAppNavigationMenuRoleUpdatableFieldsEquals(
            expectedAppNavigationMenuRole,
            getPersistedAppNavigationMenuRole(expectedAppNavigationMenuRole)
        );
    }

    protected void assertPersistedAppNavigationMenuRoleToMatchUpdatableProperties(AppNavigationMenuRole expectedAppNavigationMenuRole) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppNavigationMenuRoleAllUpdatablePropertiesEquals(expectedAppNavigationMenuRole, getPersistedAppNavigationMenuRole(expectedAppNavigationMenuRole));
        assertAppNavigationMenuRoleUpdatableFieldsEquals(
            expectedAppNavigationMenuRole,
            getPersistedAppNavigationMenuRole(expectedAppNavigationMenuRole)
        );
    }
}
