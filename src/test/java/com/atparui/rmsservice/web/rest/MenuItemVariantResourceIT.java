package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.MenuItemVariantAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.MenuItemVariant;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.MenuItemVariantRepository;
import com.atparui.rmsservice.service.dto.MenuItemVariantDTO;
import com.atparui.rmsservice.service.mapper.MenuItemVariantMapper;
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
 * Integration tests for the {@link MenuItemVariantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MenuItemVariantResourceIT {

    private static final String DEFAULT_VARIANT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_VARIANT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VARIANT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_VARIANT_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE_MODIFIER = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE_MODIFIER = new BigDecimal(2);

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/menu-item-variants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuItemVariantRepository menuItemVariantRepository;

    @Autowired
    private MenuItemVariantMapper menuItemVariantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MenuItemVariant menuItemVariant;

    private MenuItemVariant insertedMenuItemVariant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemVariant createEntity() {
        return new MenuItemVariant()
            .id(UUID.randomUUID())
            .variantName(DEFAULT_VARIANT_NAME)
            .variantCode(DEFAULT_VARIANT_CODE)
            .priceModifier(DEFAULT_PRICE_MODIFIER)
            .isDefault(DEFAULT_IS_DEFAULT)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuItemVariant createUpdatedEntity() {
        return new MenuItemVariant()
            .id(UUID.randomUUID())
            .variantName(UPDATED_VARIANT_NAME)
            .variantCode(UPDATED_VARIANT_CODE)
            .priceModifier(UPDATED_PRICE_MODIFIER)
            .isDefault(UPDATED_IS_DEFAULT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MenuItemVariant.class).block();
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
        menuItemVariant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMenuItemVariant != null) {
            menuItemVariantRepository.delete(insertedMenuItemVariant).block();
            insertedMenuItemVariant = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMenuItemVariant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        menuItemVariant.setId(null);
        // Create the MenuItemVariant
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);
        var returnedMenuItemVariantDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MenuItemVariantDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MenuItemVariant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMenuItemVariant = menuItemVariantMapper.toEntity(returnedMenuItemVariantDTO);
        assertMenuItemVariantUpdatableFieldsEquals(returnedMenuItemVariant, getPersistedMenuItemVariant(returnedMenuItemVariant));

        insertedMenuItemVariant = returnedMenuItemVariant;
    }

    @Test
    void createMenuItemVariantWithExistingId() throws Exception {
        // Create the MenuItemVariant with an existing ID
        insertedMenuItemVariant = menuItemVariantRepository.save(menuItemVariant).block();
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkVariantNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemVariant.setVariantName(null);

        // Create the MenuItemVariant, which fails.
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkVariantCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        menuItemVariant.setVariantCode(null);

        // Create the MenuItemVariant, which fails.
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMenuItemVariantsAsStream() {
        // Initialize the database
        menuItemVariant.setId(UUID.randomUUID());
        menuItemVariantRepository.save(menuItemVariant).block();

        List<MenuItemVariant> menuItemVariantList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(MenuItemVariantDTO.class)
            .getResponseBody()
            .map(menuItemVariantMapper::toEntity)
            .filter(menuItemVariant::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(menuItemVariantList).isNotNull();
        assertThat(menuItemVariantList).hasSize(1);
        MenuItemVariant testMenuItemVariant = menuItemVariantList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertMenuItemVariantAllPropertiesEquals(menuItemVariant, testMenuItemVariant);
        assertMenuItemVariantUpdatableFieldsEquals(menuItemVariant, testMenuItemVariant);
    }

    @Test
    void getAllMenuItemVariants() {
        // Initialize the database
        menuItemVariant.setId(UUID.randomUUID());
        insertedMenuItemVariant = menuItemVariantRepository.save(menuItemVariant).block();

        // Get all the menuItemVariantList
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
            .value(hasItem(menuItemVariant.getId().toString()))
            .jsonPath("$.[*].variantName")
            .value(hasItem(DEFAULT_VARIANT_NAME))
            .jsonPath("$.[*].variantCode")
            .value(hasItem(DEFAULT_VARIANT_CODE))
            .jsonPath("$.[*].priceModifier")
            .value(hasItem(sameNumber(DEFAULT_PRICE_MODIFIER)))
            .jsonPath("$.[*].isDefault")
            .value(hasItem(DEFAULT_IS_DEFAULT))
            .jsonPath("$.[*].displayOrder")
            .value(hasItem(DEFAULT_DISPLAY_ORDER))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getMenuItemVariant() {
        // Initialize the database
        menuItemVariant.setId(UUID.randomUUID());
        insertedMenuItemVariant = menuItemVariantRepository.save(menuItemVariant).block();

        // Get the menuItemVariant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, menuItemVariant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(menuItemVariant.getId().toString()))
            .jsonPath("$.variantName")
            .value(is(DEFAULT_VARIANT_NAME))
            .jsonPath("$.variantCode")
            .value(is(DEFAULT_VARIANT_CODE))
            .jsonPath("$.priceModifier")
            .value(is(sameNumber(DEFAULT_PRICE_MODIFIER)))
            .jsonPath("$.isDefault")
            .value(is(DEFAULT_IS_DEFAULT))
            .jsonPath("$.displayOrder")
            .value(is(DEFAULT_DISPLAY_ORDER))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingMenuItemVariant() {
        // Get the menuItemVariant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMenuItemVariant() throws Exception {
        // Initialize the database
        menuItemVariant.setId(UUID.randomUUID());
        insertedMenuItemVariant = menuItemVariantRepository.save(menuItemVariant).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemVariant
        MenuItemVariant updatedMenuItemVariant = menuItemVariantRepository.findById(menuItemVariant.getId()).block();
        updatedMenuItemVariant
            .variantName(UPDATED_VARIANT_NAME)
            .variantCode(UPDATED_VARIANT_CODE)
            .priceModifier(UPDATED_PRICE_MODIFIER)
            .isDefault(UPDATED_IS_DEFAULT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isActive(UPDATED_IS_ACTIVE);
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(updatedMenuItemVariant);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, menuItemVariantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuItemVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuItemVariantToMatchAllProperties(updatedMenuItemVariant);
    }

    @Test
    void putNonExistingMenuItemVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemVariant.setId(UUID.randomUUID());

        // Create the MenuItemVariant
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, menuItemVariantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMenuItemVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemVariant.setId(UUID.randomUUID());

        // Create the MenuItemVariant
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMenuItemVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemVariant.setId(UUID.randomUUID());

        // Create the MenuItemVariant
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MenuItemVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMenuItemVariantWithPatch() throws Exception {
        // Initialize the database
        menuItemVariant.setId(UUID.randomUUID());
        insertedMenuItemVariant = menuItemVariantRepository.save(menuItemVariant).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemVariant using partial update
        MenuItemVariant partialUpdatedMenuItemVariant = new MenuItemVariant();
        partialUpdatedMenuItemVariant.setId(menuItemVariant.getId());

        partialUpdatedMenuItemVariant.priceModifier(UPDATED_PRICE_MODIFIER).isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenuItemVariant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenuItemVariant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuItemVariant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemVariantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMenuItemVariant, menuItemVariant),
            getPersistedMenuItemVariant(menuItemVariant)
        );
    }

    @Test
    void fullUpdateMenuItemVariantWithPatch() throws Exception {
        // Initialize the database
        menuItemVariant.setId(UUID.randomUUID());
        insertedMenuItemVariant = menuItemVariantRepository.save(menuItemVariant).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menuItemVariant using partial update
        MenuItemVariant partialUpdatedMenuItemVariant = new MenuItemVariant();
        partialUpdatedMenuItemVariant.setId(menuItemVariant.getId());

        partialUpdatedMenuItemVariant
            .variantName(UPDATED_VARIANT_NAME)
            .variantCode(UPDATED_VARIANT_CODE)
            .priceModifier(UPDATED_PRICE_MODIFIER)
            .isDefault(UPDATED_IS_DEFAULT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenuItemVariant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenuItemVariant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MenuItemVariant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuItemVariantUpdatableFieldsEquals(
            partialUpdatedMenuItemVariant,
            getPersistedMenuItemVariant(partialUpdatedMenuItemVariant)
        );
    }

    @Test
    void patchNonExistingMenuItemVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemVariant.setId(UUID.randomUUID());

        // Create the MenuItemVariant
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, menuItemVariantDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMenuItemVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemVariant.setId(UUID.randomUUID());

        // Create the MenuItemVariant
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MenuItemVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMenuItemVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menuItemVariant.setId(UUID.randomUUID());

        // Create the MenuItemVariant
        MenuItemVariantDTO menuItemVariantDTO = menuItemVariantMapper.toDto(menuItemVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menuItemVariantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MenuItemVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMenuItemVariant() {
        // Initialize the database
        menuItemVariant.setId(UUID.randomUUID());
        insertedMenuItemVariant = menuItemVariantRepository.save(menuItemVariant).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the menuItemVariant
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, menuItemVariant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return menuItemVariantRepository.count().block();
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

    protected MenuItemVariant getPersistedMenuItemVariant(MenuItemVariant menuItemVariant) {
        return menuItemVariantRepository.findById(menuItemVariant.getId()).block();
    }

    protected void assertPersistedMenuItemVariantToMatchAllProperties(MenuItemVariant expectedMenuItemVariant) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuItemVariantAllPropertiesEquals(expectedMenuItemVariant, getPersistedMenuItemVariant(expectedMenuItemVariant));
        assertMenuItemVariantUpdatableFieldsEquals(expectedMenuItemVariant, getPersistedMenuItemVariant(expectedMenuItemVariant));
    }

    protected void assertPersistedMenuItemVariantToMatchUpdatableProperties(MenuItemVariant expectedMenuItemVariant) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuItemVariantAllUpdatablePropertiesEquals(expectedMenuItemVariant, getPersistedMenuItemVariant(expectedMenuItemVariant));
        assertMenuItemVariantUpdatableFieldsEquals(expectedMenuItemVariant, getPersistedMenuItemVariant(expectedMenuItemVariant));
    }
}
