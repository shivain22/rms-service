package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.MenuItemAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.MenuItem;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.MenuItemRepository;
import com.atparui.rmsservice.repository.search.MenuItemSearchRepository;
import com.atparui.rmsservice.service.dto.MenuItemDTO;
import com.atparui.rmsservice.service.mapper.MenuItemMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link MenuItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MenuItemResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ITEM_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CUISINE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CUISINE_TYPE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_VEGETARIAN = false;
    private static final Boolean UPDATED_IS_VEGETARIAN = true;

    private static final Boolean DEFAULT_IS_VEGAN = false;
    private static final Boolean UPDATED_IS_VEGAN = true;

    private static final Boolean DEFAULT_IS_ALCOHOLIC = false;
    private static final Boolean UPDATED_IS_ALCOHOLIC = true;

    private static final Integer DEFAULT_SPICE_LEVEL = 1;
    private static final Integer UPDATED_SPICE_LEVEL = 2;

    private static final Integer DEFAULT_PREPARATION_TIME = 1;
    private static final Integer UPDATED_PREPARATION_TIME = 2;

    private static final BigDecimal DEFAULT_BASE_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BASE_PRICE = new BigDecimal(2);

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_AVAILABLE = false;
    private static final Boolean UPDATED_IS_AVAILABLE = true;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final String ENTITY_API_URL = "/api/menu-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/menu-items/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuItemMapper menuItemMapper;

    @Autowired
    private MenuItemSearchRepository menuItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MenuItem menuItem;

    private MenuItem insertedMenuItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItem createEntity() {
        return new MenuItem()
            .id(UUID.randomUUID())
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .description(DEFAULT_DESCRIPTION)
            .itemType(DEFAULT_ITEM_TYPE)
            .cuisineType(DEFAULT_CUISINE_TYPE)
            .isVegetarian(DEFAULT_IS_VEGETARIAN)
            .isVegan(DEFAULT_IS_VEGAN)
            .isAlcoholic(DEFAULT_IS_ALCOHOLIC)
            .spiceLevel(DEFAULT_SPICE_LEVEL)
            .preparationTime(DEFAULT_PREPARATION_TIME)
            .basePrice(DEFAULT_BASE_PRICE)
            .imageUrl(DEFAULT_IMAGE_URL)
            .isAvailable(DEFAULT_IS_AVAILABLE)
            .isActive(DEFAULT_IS_ACTIVE)
            .displayOrder(DEFAULT_DISPLAY_ORDER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItem createUpdatedEntity() {
        return new MenuItem()
            .id(UUID.randomUUID())
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .itemType(UPDATED_ITEM_TYPE)
            .cuisineType(UPDATED_CUISINE_TYPE)
            .isVegetarian(UPDATED_IS_VEGETARIAN)
            .isVegan(UPDATED_IS_VEGAN)
            .isAlcoholic(UPDATED_IS_ALCOHOLIC)
            .spiceLevel(UPDATED_SPICE_LEVEL)
            .preparationTime(UPDATED_PREPARATION_TIME)
            .basePrice(UPDATED_BASE_PRICE)
            .imageUrl(UPDATED_IMAGE_URL)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .isActive(UPDATED_IS_ACTIVE)
            .displayOrder(UPDATED_DISPLAY_ORDER);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MenuItem.class).block();
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
        menuItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMenuItem != null) {
            menuItemRepository.delete(insertedMenuItem).block();
            menuItemSearchRepository.delete(insertedMenuItem).block();
            insertedMenuItem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMenuItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        menuItem.setId(null);
        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);
        var returnedMenuItemDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MenuItemDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MenuItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMenuItem = menuItemMapper.toEntity(returnedMenuItemDTO);
        assertMenuItemUpdatableFieldsEquals(returnedMenuItem, getPersistedMenuItem(returnedMenuItem));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMenuItem = returnedMenuItem;
    }

    @Test
    void createMenuItemWithExistingId() throws Exception {
        // Create the MenuItem with an existing ID
        insertedMenuItem = menuItemRepository.save(menuItem).block();
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        // set the field null
        menuItem.setName(null);

        // Create the MenuItem, which fails.
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        // set the field null
        menuItem.setCode(null);

        // Create the MenuItem, which fails.
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkItemTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        // set the field null
        menuItem.setItemType(null);

        // Create the MenuItem, which fails.
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkBasePriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        // set the field null
        menuItem.setBasePrice(null);

        // Create the MenuItem, which fails.
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllMenuItems() {
        // Initialize the database
        menuItem.setId(UUID.randomUUID());
        insertedMenuItem = menuItemRepository.save(menuItem).block();

        // Get all the menuItemList
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
            .value(hasItem(menuItem.getId().toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].itemType")
            .value(hasItem(DEFAULT_ITEM_TYPE))
            .jsonPath("$.[*].cuisineType")
            .value(hasItem(DEFAULT_CUISINE_TYPE))
            .jsonPath("$.[*].isVegetarian")
            .value(hasItem(DEFAULT_IS_VEGETARIAN))
            .jsonPath("$.[*].isVegan")
            .value(hasItem(DEFAULT_IS_VEGAN))
            .jsonPath("$.[*].isAlcoholic")
            .value(hasItem(DEFAULT_IS_ALCOHOLIC))
            .jsonPath("$.[*].spiceLevel")
            .value(hasItem(DEFAULT_SPICE_LEVEL))
            .jsonPath("$.[*].preparationTime")
            .value(hasItem(DEFAULT_PREPARATION_TIME))
            .jsonPath("$.[*].basePrice")
            .value(hasItem(sameNumber(DEFAULT_BASE_PRICE)))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].isAvailable")
            .value(hasItem(DEFAULT_IS_AVAILABLE))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE))
            .jsonPath("$.[*].displayOrder")
            .value(hasItem(DEFAULT_DISPLAY_ORDER));
    }

    @Test
    void getMenuItem() {
        // Initialize the database
        menuItem.setId(UUID.randomUUID());
        insertedMenuItem = menuItemRepository.save(menuItem).block();

        // Get the menuItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, menuItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(menuItem.getId().toString()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.itemType")
            .value(is(DEFAULT_ITEM_TYPE))
            .jsonPath("$.cuisineType")
            .value(is(DEFAULT_CUISINE_TYPE))
            .jsonPath("$.isVegetarian")
            .value(is(DEFAULT_IS_VEGETARIAN))
            .jsonPath("$.isVegan")
            .value(is(DEFAULT_IS_VEGAN))
            .jsonPath("$.isAlcoholic")
            .value(is(DEFAULT_IS_ALCOHOLIC))
            .jsonPath("$.spiceLevel")
            .value(is(DEFAULT_SPICE_LEVEL))
            .jsonPath("$.preparationTime")
            .value(is(DEFAULT_PREPARATION_TIME))
            .jsonPath("$.basePrice")
            .value(is(sameNumber(DEFAULT_BASE_PRICE)))
            .jsonPath("$.imageUrl")
            .value(is(DEFAULT_IMAGE_URL))
            .jsonPath("$.isAvailable")
            .value(is(DEFAULT_IS_AVAILABLE))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE))
            .jsonPath("$.displayOrder")
            .value(is(DEFAULT_DISPLAY_ORDER));
    }

    @Test
    void getNonExistingMenuItem() {
        // Get the menuItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMenuItem() throws Exception {
        // Initialize the database
        menuItem.setId(UUID.randomUUID());
        insertedMenuItem = menuItemRepository.save(menuItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemSearchRepository.save(menuItem).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());

        // Update the menuItem
        MenuItem updatedMenuItem = menuItemRepository.findById(menuItem.getId()).block();
        updatedMenuItem
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .itemType(UPDATED_ITEM_TYPE)
            .cuisineType(UPDATED_CUISINE_TYPE)
            .isVegetarian(UPDATED_IS_VEGETARIAN)
            .isVegan(UPDATED_IS_VEGAN)
            .isAlcoholic(UPDATED_IS_ALCOHOLIC)
            .spiceLevel(UPDATED_SPICE_LEVEL)
            .preparationTime(UPDATED_PREPARATION_TIME)
            .basePrice(UPDATED_BASE_PRICE)
            .imageUrl(UPDATED_IMAGE_URL)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .isActive(UPDATED_IS_ACTIVE)
            .displayOrder(UPDATED_DISPLAY_ORDER);
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(updatedMenuItem);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, menuItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuItemToMatchAllProperties(updatedMenuItem);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MenuItem> menuItemSearchList = Streamable.of(menuItemSearchRepository.findAll().collectList().block()).toList();
                MenuItem testMenuItemSearch = menuItemSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertMenuItemAllPropertiesEquals(testMenuItemSearch, updatedMenuItem);
                assertMenuItemUpdatableFieldsEquals(testMenuItemSearch, updatedMenuItem);
            });
    }

    @Test
    void putNonExistingMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        menuItem.setId(UUID.randomUUID());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, menuItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        menuItem.setId(UUID.randomUUID());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        menuItem.setId(UUID.randomUUID());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateMenuItemWithPatch() throws Exception {
        // Initialize the database
        menuItem.setId(UUID.randomUUID());
        insertedMenuItem = menuItemRepository.save(menuItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItem using partial update
        MenuItem partialUpdatedMenuItem = new MenuItem();
        partialUpdatedMenuItem.setId(menuItem.getId());

        partialUpdatedMenuItem
            .code(UPDATED_CODE)
            .cuisineType(UPDATED_CUISINE_TYPE)
            .isVegan(UPDATED_IS_VEGAN)
            .isAlcoholic(UPDATED_IS_ALCOHOLIC)
            .spiceLevel(UPDATED_SPICE_LEVEL)
            .basePrice(UPDATED_BASE_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenuItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenuItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMenuItem, menuItem), getPersistedMenuItem(menuItem));
    }

    @Test
    void fullUpdateMenuItemWithPatch() throws Exception {
        // Initialize the database
        menuItem.setId(UUID.randomUUID());
        insertedMenuItem = menuItemRepository.save(menuItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItem using partial update
        MenuItem partialUpdatedMenuItem = new MenuItem();
        partialUpdatedMenuItem.setId(menuItem.getId());

        partialUpdatedMenuItem
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .itemType(UPDATED_ITEM_TYPE)
            .cuisineType(UPDATED_CUISINE_TYPE)
            .isVegetarian(UPDATED_IS_VEGETARIAN)
            .isVegan(UPDATED_IS_VEGAN)
            .isAlcoholic(UPDATED_IS_ALCOHOLIC)
            .spiceLevel(UPDATED_SPICE_LEVEL)
            .preparationTime(UPDATED_PREPARATION_TIME)
            .basePrice(UPDATED_BASE_PRICE)
            .imageUrl(UPDATED_IMAGE_URL)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .isActive(UPDATED_IS_ACTIVE)
            .displayOrder(UPDATED_DISPLAY_ORDER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenuItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenuItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemUpdatableFieldsEquals(partialUpdatedMenuItem, getPersistedMenuItem(partialUpdatedMenuItem));
    }

    @Test
    void patchNonExistingMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        menuItem.setId(UUID.randomUUID());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, menuItemDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        menuItem.setId(UUID.randomUUID());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamMenuItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        menuItem.setId(UUID.randomUUID());

        // Create the MenuItem
        MenuItemDTO menuItemDTO = menuItemMapper.toDto(menuItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MenuItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteMenuItem() {
        // Initialize the database
        menuItem.setId(UUID.randomUUID());
        insertedMenuItem = menuItemRepository.save(menuItem).block();
        menuItemRepository.save(menuItem).block();
        menuItemSearchRepository.save(menuItem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the menuItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, menuItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(menuItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchMenuItem() {
        // Initialize the database
        menuItem.setId(UUID.randomUUID());
        insertedMenuItem = menuItemRepository.save(menuItem).block();
        menuItemSearchRepository.save(menuItem).block();

        // Search the menuItem
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + menuItem.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(menuItem.getId().toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].itemType")
            .value(hasItem(DEFAULT_ITEM_TYPE))
            .jsonPath("$.[*].cuisineType")
            .value(hasItem(DEFAULT_CUISINE_TYPE))
            .jsonPath("$.[*].isVegetarian")
            .value(hasItem(DEFAULT_IS_VEGETARIAN))
            .jsonPath("$.[*].isVegan")
            .value(hasItem(DEFAULT_IS_VEGAN))
            .jsonPath("$.[*].isAlcoholic")
            .value(hasItem(DEFAULT_IS_ALCOHOLIC))
            .jsonPath("$.[*].spiceLevel")
            .value(hasItem(DEFAULT_SPICE_LEVEL))
            .jsonPath("$.[*].preparationTime")
            .value(hasItem(DEFAULT_PREPARATION_TIME))
            .jsonPath("$.[*].basePrice")
            .value(hasItem(sameNumber(DEFAULT_BASE_PRICE)))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].isAvailable")
            .value(hasItem(DEFAULT_IS_AVAILABLE))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE))
            .jsonPath("$.[*].displayOrder")
            .value(hasItem(DEFAULT_DISPLAY_ORDER));
    }

    protected long getRepositoryCount() {
        return menuItemRepository.count().block();
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

    protected MenuItem getPersistedMenuItem(MenuItem menuItem) {
        return menuItemRepository.findById(menuItem.getId()).block();
    }

    protected void assertPersistedMenuItemToMatchAllProperties(MenuItem expectedMenuItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuItemAllPropertiesEquals(expectedMenuItem, getPersistedMenuItem(expectedMenuItem));
        assertMenuItemUpdatableFieldsEquals(expectedMenuItem, getPersistedMenuItem(expectedMenuItem));
    }

    protected void assertPersistedMenuItemToMatchUpdatableProperties(MenuItem expectedMenuItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuItemAllUpdatablePropertiesEquals(expectedMenuItem, getPersistedMenuItem(expectedMenuItem));
        assertMenuItemUpdatableFieldsEquals(expectedMenuItem, getPersistedMenuItem(expectedMenuItem));
    }
}
