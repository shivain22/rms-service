package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.MenuCategoryAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.MenuCategory;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.MenuCategoryRepository;
import com.atparui.rmsservice.repository.search.MenuCategorySearchRepository;
import com.atparui.rmsservice.service.dto.MenuCategoryDTO;
import com.atparui.rmsservice.service.mapper.MenuCategoryMapper;
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
 * Integration tests for the {@link MenuCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MenuCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/menu-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/menu-categories/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private MenuCategoryMapper menuCategoryMapper;

    @Autowired
    private MenuCategorySearchRepository menuCategorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MenuCategory menuCategory;

    private MenuCategory insertedMenuCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuCategory createEntity() {
        return new MenuCategory()
            .id(UUID.randomUUID())
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .description(DEFAULT_DESCRIPTION)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .imageUrl(DEFAULT_IMAGE_URL)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuCategory createUpdatedEntity() {
        return new MenuCategory()
            .id(UUID.randomUUID())
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .imageUrl(UPDATED_IMAGE_URL)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MenuCategory.class).block();
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
        menuCategory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMenuCategory != null) {
            menuCategoryRepository.delete(insertedMenuCategory).block();
            menuCategorySearchRepository.delete(insertedMenuCategory).block();
            insertedMenuCategory = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMenuCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        menuCategory.setId(null);
        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);
        var returnedMenuCategoryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MenuCategoryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MenuCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMenuCategory = menuCategoryMapper.toEntity(returnedMenuCategoryDTO);
        assertMenuCategoryUpdatableFieldsEquals(returnedMenuCategory, getPersistedMenuCategory(returnedMenuCategory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMenuCategory = returnedMenuCategory;
    }

    @Test
    void createMenuCategoryWithExistingId() throws Exception {
        // Create the MenuCategory with an existing ID
        insertedMenuCategory = menuCategoryRepository.save(menuCategory).block();
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        // set the field null
        menuCategory.setName(null);

        // Create the MenuCategory, which fails.
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        // set the field null
        menuCategory.setCode(null);

        // Create the MenuCategory, which fails.
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMenuCategories() {
        // Initialize the database
        menuCategory.setId(UUID.randomUUID());
        insertedMenuCategory = menuCategoryRepository.save(menuCategory).block();

        // Get all the menuCategoryList
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
            .value(hasItem(menuCategory.getId().toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].displayOrder")
            .value(hasItem(DEFAULT_DISPLAY_ORDER))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getMenuCategory() {
        // Initialize the database
        menuCategory.setId(UUID.randomUUID());
        insertedMenuCategory = menuCategoryRepository.save(menuCategory).block();

        // Get the menuCategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, menuCategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(menuCategory.getId().toString()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.displayOrder")
            .value(is(DEFAULT_DISPLAY_ORDER))
            .jsonPath("$.imageUrl")
            .value(is(DEFAULT_IMAGE_URL))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingMenuCategory() {
        // Get the menuCategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMenuCategory() throws Exception {
        // Initialize the database
        menuCategory.setId(UUID.randomUUID());
        insertedMenuCategory = menuCategoryRepository.save(menuCategory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuCategorySearchRepository.save(menuCategory).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());

        // Update the menuCategory
        MenuCategory updatedMenuCategory = menuCategoryRepository.findById(menuCategory.getId()).block();
        updatedMenuCategory
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .imageUrl(UPDATED_IMAGE_URL)
            .isActive(UPDATED_IS_ACTIVE);
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(updatedMenuCategory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, menuCategoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuCategoryToMatchAllProperties(updatedMenuCategory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MenuCategory> menuCategorySearchList = Streamable.of(
                    menuCategorySearchRepository.findAll().collectList().block()
                ).toList();
                MenuCategory testMenuCategorySearch = menuCategorySearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMenuCategoryAllPropertiesEquals(testMenuCategorySearch, updatedMenuCategory);
                assertMenuCategoryUpdatableFieldsEquals(testMenuCategorySearch, updatedMenuCategory);
            });
    }

    @Test
    void putNonExistingMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        menuCategory.setId(UUID.randomUUID());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, menuCategoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        menuCategory.setId(UUID.randomUUID());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        menuCategory.setId(UUID.randomUUID());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMenuCategoryWithPatch() throws Exception {
        // Initialize the database
        menuCategory.setId(UUID.randomUUID());
        insertedMenuCategory = menuCategoryRepository.save(menuCategory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuCategory using partial update
        MenuCategory partialUpdatedMenuCategory = new MenuCategory();
        partialUpdatedMenuCategory.setId(menuCategory.getId());

        partialUpdatedMenuCategory.code(UPDATED_CODE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenuCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenuCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMenuCategory, menuCategory),
            getPersistedMenuCategory(menuCategory)
        );
    }

    @Test
    void fullUpdateMenuCategoryWithPatch() throws Exception {
        // Initialize the database
        menuCategory.setId(UUID.randomUUID());
        insertedMenuCategory = menuCategoryRepository.save(menuCategory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuCategory using partial update
        MenuCategory partialUpdatedMenuCategory = new MenuCategory();
        partialUpdatedMenuCategory.setId(menuCategory.getId());

        partialUpdatedMenuCategory
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .imageUrl(UPDATED_IMAGE_URL)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenuCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenuCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuCategoryUpdatableFieldsEquals(partialUpdatedMenuCategory, getPersistedMenuCategory(partialUpdatedMenuCategory));
    }

    @Test
    void patchNonExistingMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        menuCategory.setId(UUID.randomUUID());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, menuCategoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        menuCategory.setId(UUID.randomUUID());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMenuCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        menuCategory.setId(UUID.randomUUID());

        // Create the MenuCategory
        MenuCategoryDTO menuCategoryDTO = menuCategoryMapper.toDto(menuCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuCategoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MenuCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMenuCategory() {
        // Initialize the database
        menuCategory.setId(UUID.randomUUID());
        insertedMenuCategory = menuCategoryRepository.save(menuCategory).block();
        menuCategoryRepository.save(menuCategory).block();
        menuCategorySearchRepository.save(menuCategory).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the menuCategory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, menuCategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuCategorySearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMenuCategory() {
        // Initialize the database
        menuCategory.setId(UUID.randomUUID());
        insertedMenuCategory = menuCategoryRepository.save(menuCategory).block();
        menuCategorySearchRepository.save(menuCategory).block();

        // Search the menuCategory
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + menuCategory.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(menuCategory.getId().toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].displayOrder")
            .value(hasItem(DEFAULT_DISPLAY_ORDER))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    protected long getRepositoryCount() {
        return menuCategoryRepository.count().block();
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

    protected MenuCategory getPersistedMenuCategory(MenuCategory menuCategory) {
        return menuCategoryRepository.findById(menuCategory.getId()).block();
    }

    protected void assertPersistedMenuCategoryToMatchAllProperties(MenuCategory expectedMenuCategory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuCategoryAllPropertiesEquals(expectedMenuCategory, getPersistedMenuCategory(expectedMenuCategory));
        assertMenuCategoryUpdatableFieldsEquals(expectedMenuCategory, getPersistedMenuCategory(expectedMenuCategory));
    }

    protected void assertPersistedMenuCategoryToMatchUpdatableProperties(MenuCategory expectedMenuCategory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuCategoryAllUpdatablePropertiesEquals(expectedMenuCategory, getPersistedMenuCategory(expectedMenuCategory));
        assertMenuCategoryUpdatableFieldsEquals(expectedMenuCategory, getPersistedMenuCategory(expectedMenuCategory));
    }
}
