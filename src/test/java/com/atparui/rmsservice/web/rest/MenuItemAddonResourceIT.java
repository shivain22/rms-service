package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.MenuItemAddonAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.MenuItemAddon;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.MenuItemAddonRepository;
import com.atparui.rmsservice.service.dto.MenuItemAddonDTO;
import com.atparui.rmsservice.service.mapper.MenuItemAddonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Duration;
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
 * Integration tests for the {@link MenuItemAddonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MenuItemAddonResourceIT {

    private static final String DEFAULT_ADDON_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ADDON_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDON_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ADDON_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/menu-item-addons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuItemAddonRepository menuItemAddonRepository;

    @Autowired
    private MenuItemAddonMapper menuItemAddonMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MenuItemAddon menuItemAddon;

    private MenuItemAddon insertedMenuItemAddon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemAddon createEntity() {
        return new MenuItemAddon()
            .id(UUID.randomUUID())
            .addonName(DEFAULT_ADDON_NAME)
            .addonCode(DEFAULT_ADDON_CODE)
            .price(DEFAULT_PRICE)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemAddon createUpdatedEntity() {
        return new MenuItemAddon()
            .id(UUID.randomUUID())
            .addonName(UPDATED_ADDON_NAME)
            .addonCode(UPDATED_ADDON_CODE)
            .price(UPDATED_PRICE)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MenuItemAddon.class).block();
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
        menuItemAddon = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMenuItemAddon != null) {
            menuItemAddonRepository.delete(insertedMenuItemAddon).block();
            insertedMenuItemAddon = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMenuItemAddon() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        menuItemAddon.setId(null);
        // Create the MenuItemAddon
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);
        var returnedMenuItemAddonDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MenuItemAddonDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MenuItemAddon in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMenuItemAddon = menuItemAddonMapper.toEntity(returnedMenuItemAddonDTO);
        assertMenuItemAddonUpdatableFieldsEquals(returnedMenuItemAddon, getPersistedMenuItemAddon(returnedMenuItemAddon));

        insertedMenuItemAddon = returnedMenuItemAddon;
    }

    @Test
    void createMenuItemAddonWithExistingId() throws Exception {
        // Create the MenuItemAddon with an existing ID
        insertedMenuItemAddon = menuItemAddonRepository.save(menuItemAddon).block();
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemAddon in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAddonNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemAddon.setAddonName(null);

        // Create the MenuItemAddon, which fails.
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAddonCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemAddon.setAddonCode(null);

        // Create the MenuItemAddon, which fails.
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemAddon.setPrice(null);

        // Create the MenuItemAddon, which fails.
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMenuItemAddonsAsStream() {
        // Initialize the database
        menuItemAddon.setId(UUID.randomUUID());
        menuItemAddonRepository.save(menuItemAddon).block();

        List<MenuItemAddon> menuItemAddonList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(MenuItemAddonDTO.class)
            .getResponseBody()
            .map(menuItemAddonMapper::toEntity)
            .filter(menuItemAddon::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(menuItemAddonList).isNotNull();
        assertThat(menuItemAddonList).hasSize(1);
        MenuItemAddon testMenuItemAddon = menuItemAddonList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertMenuItemAddonAllPropertiesEquals(menuItemAddon, testMenuItemAddon);
        assertMenuItemAddonUpdatableFieldsEquals(menuItemAddon, testMenuItemAddon);
    }

    @Test
    void getAllMenuItemAddons() {
        // Initialize the database
        menuItemAddon.setId(UUID.randomUUID());
        insertedMenuItemAddon = menuItemAddonRepository.save(menuItemAddon).block();

        // Get all the menuItemAddonList
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
            .value(hasItem(menuItemAddon.getId().toString()))
            .jsonPath("$.[*].addonName")
            .value(hasItem(DEFAULT_ADDON_NAME))
            .jsonPath("$.[*].addonCode")
            .value(hasItem(DEFAULT_ADDON_CODE))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getMenuItemAddon() {
        // Initialize the database
        menuItemAddon.setId(UUID.randomUUID());
        insertedMenuItemAddon = menuItemAddonRepository.save(menuItemAddon).block();

        // Get the menuItemAddon
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, menuItemAddon.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(menuItemAddon.getId().toString()))
            .jsonPath("$.addonName")
            .value(is(DEFAULT_ADDON_NAME))
            .jsonPath("$.addonCode")
            .value(is(DEFAULT_ADDON_CODE))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingMenuItemAddon() {
        // Get the menuItemAddon
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMenuItemAddon() throws Exception {
        // Initialize the database
        menuItemAddon.setId(UUID.randomUUID());
        insertedMenuItemAddon = menuItemAddonRepository.save(menuItemAddon).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemAddon
        MenuItemAddon updatedMenuItemAddon = menuItemAddonRepository.findById(menuItemAddon.getId()).block();
        updatedMenuItemAddon.addonName(UPDATED_ADDON_NAME).addonCode(UPDATED_ADDON_CODE).price(UPDATED_PRICE).isActive(UPDATED_IS_ACTIVE);
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(updatedMenuItemAddon);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, menuItemAddonDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuItemAddon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuItemAddonToMatchAllProperties(updatedMenuItemAddon);
    }

    @Test
    void putNonExistingMenuItemAddon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAddon.setId(UUID.randomUUID());

        // Create the MenuItemAddon
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, menuItemAddonDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemAddon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMenuItemAddon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAddon.setId(UUID.randomUUID());

        // Create the MenuItemAddon
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemAddon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMenuItemAddon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAddon.setId(UUID.randomUUID());

        // Create the MenuItemAddon
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MenuItemAddon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMenuItemAddonWithPatch() throws Exception {
        // Initialize the database
        menuItemAddon.setId(UUID.randomUUID());
        insertedMenuItemAddon = menuItemAddonRepository.save(menuItemAddon).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemAddon using partial update
        MenuItemAddon partialUpdatedMenuItemAddon = new MenuItemAddon();
        partialUpdatedMenuItemAddon.setId(menuItemAddon.getId());

        partialUpdatedMenuItemAddon.addonName(UPDATED_ADDON_NAME).isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenuItemAddon.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenuItemAddon))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuItemAddon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemAddonUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMenuItemAddon, menuItemAddon),
            getPersistedMenuItemAddon(menuItemAddon)
        );
    }

    @Test
    void fullUpdateMenuItemAddonWithPatch() throws Exception {
        // Initialize the database
        menuItemAddon.setId(UUID.randomUUID());
        insertedMenuItemAddon = menuItemAddonRepository.save(menuItemAddon).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemAddon using partial update
        MenuItemAddon partialUpdatedMenuItemAddon = new MenuItemAddon();
        partialUpdatedMenuItemAddon.setId(menuItemAddon.getId());

        partialUpdatedMenuItemAddon
            .addonName(UPDATED_ADDON_NAME)
            .addonCode(UPDATED_ADDON_CODE)
            .price(UPDATED_PRICE)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenuItemAddon.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenuItemAddon))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuItemAddon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemAddonUpdatableFieldsEquals(partialUpdatedMenuItemAddon, getPersistedMenuItemAddon(partialUpdatedMenuItemAddon));
    }

    @Test
    void patchNonExistingMenuItemAddon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAddon.setId(UUID.randomUUID());

        // Create the MenuItemAddon
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, menuItemAddonDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemAddon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMenuItemAddon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAddon.setId(UUID.randomUUID());

        // Create the MenuItemAddon
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemAddon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMenuItemAddon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemAddon.setId(UUID.randomUUID());

        // Create the MenuItemAddon
        MenuItemAddonDTO menuItemAddonDTO = menuItemAddonMapper.toDto(menuItemAddon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuItemAddonDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MenuItemAddon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMenuItemAddon() {
        // Initialize the database
        menuItemAddon.setId(UUID.randomUUID());
        insertedMenuItemAddon = menuItemAddonRepository.save(menuItemAddon).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the menuItemAddon
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, menuItemAddon.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return menuItemAddonRepository.count().block();
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

    protected MenuItemAddon getPersistedMenuItemAddon(MenuItemAddon menuItemAddon) {
        return menuItemAddonRepository.findById(menuItemAddon.getId()).block();
    }

    protected void assertPersistedMenuItemAddonToMatchAllProperties(MenuItemAddon expectedMenuItemAddon) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuItemAddonAllPropertiesEquals(expectedMenuItemAddon, getPersistedMenuItemAddon(expectedMenuItemAddon));
        assertMenuItemAddonUpdatableFieldsEquals(expectedMenuItemAddon, getPersistedMenuItemAddon(expectedMenuItemAddon));
    }

    protected void assertPersistedMenuItemAddonToMatchUpdatableProperties(MenuItemAddon expectedMenuItemAddon) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuItemAddonAllUpdatablePropertiesEquals(expectedMenuItemAddon, getPersistedMenuItemAddon(expectedMenuItemAddon));
        assertMenuItemAddonUpdatableFieldsEquals(expectedMenuItemAddon, getPersistedMenuItemAddon(expectedMenuItemAddon));
    }
}
