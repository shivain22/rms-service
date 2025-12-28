package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.AppNavigationMenuAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.AppNavigationMenu;
import com.atparui.rmsservice.repository.AppNavigationMenuRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.service.dto.AppNavigationMenuDTO;
import com.atparui.rmsservice.service.mapper.AppNavigationMenuMapper;
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
 * Integration tests for the {@link AppNavigationMenuResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AppNavigationMenuResourceIT {

    private static final String DEFAULT_MENU_CODE = "AAAAAAAAAA";
    private static final String UPDATED_MENU_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MENU_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MENU_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_MENU_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MENU_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_ICON = "AAAAAAAAAA";
    private static final String UPDATED_ICON = "BBBBBBBBBB";

    private static final String DEFAULT_ROUTE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_ROUTE_PATH = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/app-navigation-menus";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppNavigationMenuRepository appNavigationMenuRepository;

    @Autowired
    private AppNavigationMenuMapper appNavigationMenuMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AppNavigationMenu appNavigationMenu;

    private AppNavigationMenu insertedAppNavigationMenu;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppNavigationMenu createEntity() {
        return new AppNavigationMenu()
            .id(UUID.randomUUID())
            .menuCode(DEFAULT_MENU_CODE)
            .menuName(DEFAULT_MENU_NAME)
            .description(DEFAULT_DESCRIPTION)
            .menuType(DEFAULT_MENU_TYPE)
            .icon(DEFAULT_ICON)
            .routePath(DEFAULT_ROUTE_PATH)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppNavigationMenu createUpdatedEntity() {
        return new AppNavigationMenu()
            .id(UUID.randomUUID())
            .menuCode(UPDATED_MENU_CODE)
            .menuName(UPDATED_MENU_NAME)
            .description(UPDATED_DESCRIPTION)
            .menuType(UPDATED_MENU_TYPE)
            .icon(UPDATED_ICON)
            .routePath(UPDATED_ROUTE_PATH)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AppNavigationMenu.class).block();
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
        appNavigationMenu = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppNavigationMenu != null) {
            appNavigationMenuRepository.delete(insertedAppNavigationMenu).block();
            insertedAppNavigationMenu = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAppNavigationMenu() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        appNavigationMenu.setId(null);
        // Create the AppNavigationMenu
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);
        var returnedAppNavigationMenuDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AppNavigationMenuDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AppNavigationMenu in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppNavigationMenu = appNavigationMenuMapper.toEntity(returnedAppNavigationMenuDTO);
        assertAppNavigationMenuUpdatableFieldsEquals(returnedAppNavigationMenu, getPersistedAppNavigationMenu(returnedAppNavigationMenu));

        insertedAppNavigationMenu = returnedAppNavigationMenu;
    }

    @Test
    void createAppNavigationMenuWithExistingId() throws Exception {
        // Create the AppNavigationMenu with an existing ID
        insertedAppNavigationMenu = appNavigationMenuRepository.save(appNavigationMenu).block();
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenu in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkMenuCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appNavigationMenu.setMenuCode(null);

        // Create the AppNavigationMenu, which fails.
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMenuNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appNavigationMenu.setMenuName(null);

        // Create the AppNavigationMenu, which fails.
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMenuTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appNavigationMenu.setMenuType(null);

        // Create the AppNavigationMenu, which fails.
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAppNavigationMenus() {
        // Initialize the database
        appNavigationMenu.setId(UUID.randomUUID());
        insertedAppNavigationMenu = appNavigationMenuRepository.save(appNavigationMenu).block();

        // Get all the appNavigationMenuList
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
            .value(hasItem(appNavigationMenu.getId().toString()))
            .jsonPath("$.[*].menuCode")
            .value(hasItem(DEFAULT_MENU_CODE))
            .jsonPath("$.[*].menuName")
            .value(hasItem(DEFAULT_MENU_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].menuType")
            .value(hasItem(DEFAULT_MENU_TYPE))
            .jsonPath("$.[*].icon")
            .value(hasItem(DEFAULT_ICON))
            .jsonPath("$.[*].routePath")
            .value(hasItem(DEFAULT_ROUTE_PATH))
            .jsonPath("$.[*].displayOrder")
            .value(hasItem(DEFAULT_DISPLAY_ORDER))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getAppNavigationMenu() {
        // Initialize the database
        appNavigationMenu.setId(UUID.randomUUID());
        insertedAppNavigationMenu = appNavigationMenuRepository.save(appNavigationMenu).block();

        // Get the appNavigationMenu
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appNavigationMenu.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appNavigationMenu.getId().toString()))
            .jsonPath("$.menuCode")
            .value(is(DEFAULT_MENU_CODE))
            .jsonPath("$.menuName")
            .value(is(DEFAULT_MENU_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.menuType")
            .value(is(DEFAULT_MENU_TYPE))
            .jsonPath("$.icon")
            .value(is(DEFAULT_ICON))
            .jsonPath("$.routePath")
            .value(is(DEFAULT_ROUTE_PATH))
            .jsonPath("$.displayOrder")
            .value(is(DEFAULT_DISPLAY_ORDER))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingAppNavigationMenu() {
        // Get the appNavigationMenu
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAppNavigationMenu() throws Exception {
        // Initialize the database
        appNavigationMenu.setId(UUID.randomUUID());
        insertedAppNavigationMenu = appNavigationMenuRepository.save(appNavigationMenu).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appNavigationMenu
        AppNavigationMenu updatedAppNavigationMenu = appNavigationMenuRepository.findById(appNavigationMenu.getId()).block();
        updatedAppNavigationMenu
            .menuCode(UPDATED_MENU_CODE)
            .menuName(UPDATED_MENU_NAME)
            .description(UPDATED_DESCRIPTION)
            .menuType(UPDATED_MENU_TYPE)
            .icon(UPDATED_ICON)
            .routePath(UPDATED_ROUTE_PATH)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isActive(UPDATED_IS_ACTIVE);
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(updatedAppNavigationMenu);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appNavigationMenuDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppNavigationMenu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppNavigationMenuToMatchAllProperties(updatedAppNavigationMenu);
    }

    @Test
    void putNonExistingAppNavigationMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenu.setId(UUID.randomUUID());

        // Create the AppNavigationMenu
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appNavigationMenuDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAppNavigationMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenu.setId(UUID.randomUUID());

        // Create the AppNavigationMenu
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAppNavigationMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenu.setId(UUID.randomUUID());

        // Create the AppNavigationMenu
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppNavigationMenu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAppNavigationMenuWithPatch() throws Exception {
        // Initialize the database
        appNavigationMenu.setId(UUID.randomUUID());
        insertedAppNavigationMenu = appNavigationMenuRepository.save(appNavigationMenu).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appNavigationMenu using partial update
        AppNavigationMenu partialUpdatedAppNavigationMenu = new AppNavigationMenu();
        partialUpdatedAppNavigationMenu.setId(appNavigationMenu.getId());

        partialUpdatedAppNavigationMenu
            .menuName(UPDATED_MENU_NAME)
            .description(UPDATED_DESCRIPTION)
            .menuType(UPDATED_MENU_TYPE)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppNavigationMenu.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppNavigationMenu))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppNavigationMenu in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppNavigationMenuUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAppNavigationMenu, appNavigationMenu),
            getPersistedAppNavigationMenu(appNavigationMenu)
        );
    }

    @Test
    void fullUpdateAppNavigationMenuWithPatch() throws Exception {
        // Initialize the database
        appNavigationMenu.setId(UUID.randomUUID());
        insertedAppNavigationMenu = appNavigationMenuRepository.save(appNavigationMenu).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appNavigationMenu using partial update
        AppNavigationMenu partialUpdatedAppNavigationMenu = new AppNavigationMenu();
        partialUpdatedAppNavigationMenu.setId(appNavigationMenu.getId());

        partialUpdatedAppNavigationMenu
            .menuCode(UPDATED_MENU_CODE)
            .menuName(UPDATED_MENU_NAME)
            .description(UPDATED_DESCRIPTION)
            .menuType(UPDATED_MENU_TYPE)
            .icon(UPDATED_ICON)
            .routePath(UPDATED_ROUTE_PATH)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppNavigationMenu.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppNavigationMenu))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppNavigationMenu in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppNavigationMenuUpdatableFieldsEquals(
            partialUpdatedAppNavigationMenu,
            getPersistedAppNavigationMenu(partialUpdatedAppNavigationMenu)
        );
    }

    @Test
    void patchNonExistingAppNavigationMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenu.setId(UUID.randomUUID());

        // Create the AppNavigationMenu
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appNavigationMenuDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAppNavigationMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenu.setId(UUID.randomUUID());

        // Create the AppNavigationMenu
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppNavigationMenu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAppNavigationMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appNavigationMenu.setId(UUID.randomUUID());

        // Create the AppNavigationMenu
        AppNavigationMenuDTO appNavigationMenuDTO = appNavigationMenuMapper.toDto(appNavigationMenu);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appNavigationMenuDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppNavigationMenu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAppNavigationMenu() {
        // Initialize the database
        appNavigationMenu.setId(UUID.randomUUID());
        insertedAppNavigationMenu = appNavigationMenuRepository.save(appNavigationMenu).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appNavigationMenu
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appNavigationMenu.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appNavigationMenuRepository.count().block();
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

    protected AppNavigationMenu getPersistedAppNavigationMenu(AppNavigationMenu appNavigationMenu) {
        return appNavigationMenuRepository.findById(appNavigationMenu.getId()).block();
    }

    protected void assertPersistedAppNavigationMenuToMatchAllProperties(AppNavigationMenu expectedAppNavigationMenu) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppNavigationMenuAllPropertiesEquals(expectedAppNavigationMenu, getPersistedAppNavigationMenu(expectedAppNavigationMenu));
        assertAppNavigationMenuUpdatableFieldsEquals(expectedAppNavigationMenu, getPersistedAppNavigationMenu(expectedAppNavigationMenu));
    }

    protected void assertPersistedAppNavigationMenuToMatchUpdatableProperties(AppNavigationMenu expectedAppNavigationMenu) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppNavigationMenuAllUpdatablePropertiesEquals(expectedAppNavigationMenu, getPersistedAppNavigationMenu(expectedAppNavigationMenu));
        assertAppNavigationMenuUpdatableFieldsEquals(expectedAppNavigationMenu, getPersistedAppNavigationMenu(expectedAppNavigationMenu));
    }
}
