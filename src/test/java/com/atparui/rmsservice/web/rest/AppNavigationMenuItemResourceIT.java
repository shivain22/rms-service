package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.AppNavigationMenuItemAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.AppNavigationMenuItem;
import com.atparui.rmsservice.repository.AppNavigationMenuItemRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.service.dto.AppNavigationMenuItemDTO;
import com.atparui.rmsservice.service.mapper.AppNavigationMenuItemMapper;
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
 * Integration tests for the {@link AppNavigationMenuItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AppNavigationMenuItemResourceIT {

    private static final String DEFAULT_ITEM_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ITEM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ICON = "AAAAAAAAAA";
    private static final String UPDATED_ICON = "BBBBBBBBBB";

    private static final String DEFAULT_ROUTE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_ROUTE_PATH = "BBBBBBBBBB";

    private static final String DEFAULT_COMPONENT_PATH = "AAAAAAAAAA";
    private static final String UPDATED_COMPONENT_PATH = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final String DEFAULT_BADGE_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_BADGE_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_BADGE_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_BADGE_COLOR = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/app-navigation-menu-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppNavigationMenuItemRepository appNavigationMenuItemRepository;

    @Autowired
    private AppNavigationMenuItemMapper appNavigationMenuItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AppNavigationMenuItem appNavigationMenuItem;

    private AppNavigationMenuItem insertedAppNavigationMenuItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppNavigationMenuItem createEntity() {
        return new AppNavigationMenuItem()
            .id(UUID.randomUUID())
            .itemCode(DEFAULT_ITEM_CODE)
            .itemName(DEFAULT_ITEM_NAME)
            .description(DEFAULT_DESCRIPTION)
            .icon(DEFAULT_ICON)
            .routePath(DEFAULT_ROUTE_PATH)
            .componentPath(DEFAULT_COMPONENT_PATH)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .badgeText(DEFAULT_BADGE_TEXT)
            .badgeColor(DEFAULT_BADGE_COLOR)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppNavigationMenuItem createUpdatedEntity() {
        return new AppNavigationMenuItem()
            .id(UUID.randomUUID())
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .description(UPDATED_DESCRIPTION)
            .icon(UPDATED_ICON)
            .routePath(UPDATED_ROUTE_PATH)
            .componentPath(UPDATED_COMPONENT_PATH)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .badgeText(UPDATED_BADGE_TEXT)
            .badgeColor(UPDATED_BADGE_COLOR)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AppNavigationMenuItem.class).block();
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
        appNavigationMenuItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppNavigationMenuItem != null) {
            appNavigationMenuItemRepository.delete(insertedAppNavigationMenuItem).block();
            insertedAppNavigationMenuItem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAppNavigationMenuItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        appNavigationMenuItem.setId(null);
        // Create the AppNavigationMenuItem
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);
        var returnedAppNavigationMenuItemDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AppNavigationMenuItemDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AppNavigationMenuItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppNavigationMenuItem = appNavigationMenuItemMapper.toEntity(returnedAppNavigationMenuItemDTO);
        assertAppNavigationMenuItemUpdatableFieldsEquals(
            returnedAppNavigationMenuItem,
            getPersistedAppNavigationMenuItem(returnedAppNavigationMenuItem)
        );

        insertedAppNavigationMenuItem = returnedAppNavigationMenuItem;
    }

    @Test
    void createAppNavigationMenuItemWithExistingId() throws Exception {
        // Create the AppNavigationMenuItem with an existing ID
        insertedAppNavigationMenuItem = appNavigationMenuItemRepository.save(appNavigationMenuItem).block();
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkItemCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appNavigationMenuItem.setItemCode(null);

        // Create the AppNavigationMenuItem, which fails.
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkItemNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appNavigationMenuItem.setItemName(null);

        // Create the AppNavigationMenuItem, which fails.
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRoutePathIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appNavigationMenuItem.setRoutePath(null);

        // Create the AppNavigationMenuItem, which fails.
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAppNavigationMenuItems() {
        // Initialize the database
        appNavigationMenuItem.setId(UUID.randomUUID());
        insertedAppNavigationMenuItem = appNavigationMenuItemRepository.save(appNavigationMenuItem).block();

        // Get all the appNavigationMenuItemList
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
            .value(hasItem(appNavigationMenuItem.getId().toString()))
            .jsonPath("$.[*].itemCode")
            .value(hasItem(DEFAULT_ITEM_CODE))
            .jsonPath("$.[*].itemName")
            .value(hasItem(DEFAULT_ITEM_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].icon")
            .value(hasItem(DEFAULT_ICON))
            .jsonPath("$.[*].routePath")
            .value(hasItem(DEFAULT_ROUTE_PATH))
            .jsonPath("$.[*].componentPath")
            .value(hasItem(DEFAULT_COMPONENT_PATH))
            .jsonPath("$.[*].displayOrder")
            .value(hasItem(DEFAULT_DISPLAY_ORDER))
            .jsonPath("$.[*].badgeText")
            .value(hasItem(DEFAULT_BADGE_TEXT))
            .jsonPath("$.[*].badgeColor")
            .value(hasItem(DEFAULT_BADGE_COLOR))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getAppNavigationMenuItem() {
        // Initialize the database
        appNavigationMenuItem.setId(UUID.randomUUID());
        insertedAppNavigationMenuItem = appNavigationMenuItemRepository.save(appNavigationMenuItem).block();

        // Get the appNavigationMenuItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appNavigationMenuItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appNavigationMenuItem.getId().toString()))
            .jsonPath("$.itemCode")
            .value(is(DEFAULT_ITEM_CODE))
            .jsonPath("$.itemName")
            .value(is(DEFAULT_ITEM_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.icon")
            .value(is(DEFAULT_ICON))
            .jsonPath("$.routePath")
            .value(is(DEFAULT_ROUTE_PATH))
            .jsonPath("$.componentPath")
            .value(is(DEFAULT_COMPONENT_PATH))
            .jsonPath("$.displayOrder")
            .value(is(DEFAULT_DISPLAY_ORDER))
            .jsonPath("$.badgeText")
            .value(is(DEFAULT_BADGE_TEXT))
            .jsonPath("$.badgeColor")
            .value(is(DEFAULT_BADGE_COLOR))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingAppNavigationMenuItem() {
        // Get the appNavigationMenuItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAppNavigationMenuItem() throws Exception {
        // Initialize the database
        appNavigationMenuItem.setId(UUID.randomUUID());
        insertedAppNavigationMenuItem = appNavigationMenuItemRepository.save(appNavigationMenuItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appNavigationMenuItem
        AppNavigationMenuItem updatedAppNavigationMenuItem = appNavigationMenuItemRepository
            .findById(appNavigationMenuItem.getId())
            .block();
        updatedAppNavigationMenuItem
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .description(UPDATED_DESCRIPTION)
            .icon(UPDATED_ICON)
            .routePath(UPDATED_ROUTE_PATH)
            .componentPath(UPDATED_COMPONENT_PATH)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .badgeText(UPDATED_BADGE_TEXT)
            .badgeColor(UPDATED_BADGE_COLOR)
            .isActive(UPDATED_IS_ACTIVE);
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(updatedAppNavigationMenuItem);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appNavigationMenuItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppNavigationMenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppNavigationMenuItemToMatchAllProperties(updatedAppNavigationMenuItem);
    }

    @Test
    void putNonExistingAppNavigationMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuItem.setId(UUID.randomUUID());

        // Create the AppNavigationMenuItem
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appNavigationMenuItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAppNavigationMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuItem.setId(UUID.randomUUID());

        // Create the AppNavigationMenuItem
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAppNavigationMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuItem.setId(UUID.randomUUID());

        // Create the AppNavigationMenuItem
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppNavigationMenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAppNavigationMenuItemWithPatch() throws Exception {
        // Initialize the database
        appNavigationMenuItem.setId(UUID.randomUUID());
        insertedAppNavigationMenuItem = appNavigationMenuItemRepository.save(appNavigationMenuItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appNavigationMenuItem using partial update
        AppNavigationMenuItem partialUpdatedAppNavigationMenuItem = new AppNavigationMenuItem();
        partialUpdatedAppNavigationMenuItem.setId(appNavigationMenuItem.getId());

        partialUpdatedAppNavigationMenuItem
            .itemCode(UPDATED_ITEM_CODE)
            .description(UPDATED_DESCRIPTION)
            .componentPath(UPDATED_COMPONENT_PATH)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .badgeText(UPDATED_BADGE_TEXT)
            .badgeColor(UPDATED_BADGE_COLOR)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppNavigationMenuItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppNavigationMenuItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppNavigationMenuItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppNavigationMenuItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAppNavigationMenuItem, appNavigationMenuItem),
            getPersistedAppNavigationMenuItem(appNavigationMenuItem)
        );
    }

    @Test
    void fullUpdateAppNavigationMenuItemWithPatch() throws Exception {
        // Initialize the database
        appNavigationMenuItem.setId(UUID.randomUUID());
        insertedAppNavigationMenuItem = appNavigationMenuItemRepository.save(appNavigationMenuItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appNavigationMenuItem using partial update
        AppNavigationMenuItem partialUpdatedAppNavigationMenuItem = new AppNavigationMenuItem();
        partialUpdatedAppNavigationMenuItem.setId(appNavigationMenuItem.getId());

        partialUpdatedAppNavigationMenuItem
            .itemCode(UPDATED_ITEM_CODE)
            .itemName(UPDATED_ITEM_NAME)
            .description(UPDATED_DESCRIPTION)
            .icon(UPDATED_ICON)
            .routePath(UPDATED_ROUTE_PATH)
            .componentPath(UPDATED_COMPONENT_PATH)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .badgeText(UPDATED_BADGE_TEXT)
            .badgeColor(UPDATED_BADGE_COLOR)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppNavigationMenuItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppNavigationMenuItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppNavigationMenuItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppNavigationMenuItemUpdatableFieldsEquals(
            partialUpdatedAppNavigationMenuItem,
            getPersistedAppNavigationMenuItem(partialUpdatedAppNavigationMenuItem)
        );
    }

    @Test
    void patchNonExistingAppNavigationMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuItem.setId(UUID.randomUUID());

        // Create the AppNavigationMenuItem
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appNavigationMenuItemDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAppNavigationMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuItem.setId(UUID.randomUUID());

        // Create the AppNavigationMenuItem
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAppNavigationMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenuItem.setId(UUID.randomUUID());

        // Create the AppNavigationMenuItem
        AppNavigationMenuItemDTO appNavigationMenuItemDTO = appNavigationMenuItemMapper.toDto(appNavigationMenuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appNavigationMenuItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppNavigationMenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAppNavigationMenuItem() {
        // Initialize the database
        appNavigationMenuItem.setId(UUID.randomUUID());
        insertedAppNavigationMenuItem = appNavigationMenuItemRepository.save(appNavigationMenuItem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appNavigationMenuItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appNavigationMenuItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appNavigationMenuItemRepository.count().block();
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

    protected AppNavigationMenuItem getPersistedAppNavigationMenuItem(AppNavigationMenuItem appNavigationMenuItem) {
        return appNavigationMenuItemRepository.findById(appNavigationMenuItem.getId()).block();
    }

    protected void assertPersistedAppNavigationMenuItemToMatchAllProperties(AppNavigationMenuItem expectedAppNavigationMenuItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppNavigationMenuItemAllPropertiesEquals(expectedAppNavigationMenuItem, getPersistedAppNavigationMenuItem(expectedAppNavigationMenuItem));
        assertAppNavigationMenuItemUpdatableFieldsEquals(
            expectedAppNavigationMenuItem,
            getPersistedAppNavigationMenuItem(expectedAppNavigationMenuItem)
        );
    }

    protected void assertPersistedAppNavigationMenuItemToMatchUpdatableProperties(AppNavigationMenuItem expectedAppNavigationMenuItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppNavigationMenuItemAllUpdatablePropertiesEquals(expectedAppNavigationMenuItem, getPersistedAppNavigationMenuItem(expectedAppNavigationMenuItem));
        assertAppNavigationMenuItemUpdatableFieldsEquals(
            expectedAppNavigationMenuItem,
            getPersistedAppNavigationMenuItem(expectedAppNavigationMenuItem)
        );
    }
}
