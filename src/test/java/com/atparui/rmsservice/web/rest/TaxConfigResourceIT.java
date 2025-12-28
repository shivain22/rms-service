package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.TaxConfigAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.TaxConfig;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.TaxConfigRepository;
import com.atparui.rmsservice.service.dto.TaxConfigDTO;
import com.atparui.rmsservice.service.mapper.TaxConfigMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link TaxConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TaxConfigResourceIT {

    private static final String DEFAULT_TAX_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TAX_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TAX_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TAX_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAX_RATE = new BigDecimal(2);

    private static final String DEFAULT_TAX_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TAX_TYPE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_APPLICABLE_TO_FOOD = false;
    private static final Boolean UPDATED_IS_APPLICABLE_TO_FOOD = true;

    private static final Boolean DEFAULT_IS_APPLICABLE_TO_BEVERAGE = false;
    private static final Boolean UPDATED_IS_APPLICABLE_TO_BEVERAGE = true;

    private static final Boolean DEFAULT_IS_APPLICABLE_TO_ALCOHOL = false;
    private static final Boolean UPDATED_IS_APPLICABLE_TO_ALCOHOL = true;

    private static final LocalDate DEFAULT_EFFECTIVE_FROM = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EFFECTIVE_FROM = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_EFFECTIVE_TO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EFFECTIVE_TO = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/tax-configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaxConfigRepository taxConfigRepository;

    @Autowired
    private TaxConfigMapper taxConfigMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TaxConfig taxConfig;

    private TaxConfig insertedTaxConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaxConfig createEntity() {
        return new TaxConfig()
            .id(UUID.randomUUID())
            .taxName(DEFAULT_TAX_NAME)
            .taxCode(DEFAULT_TAX_CODE)
            .taxRate(DEFAULT_TAX_RATE)
            .taxType(DEFAULT_TAX_TYPE)
            .isApplicableToFood(DEFAULT_IS_APPLICABLE_TO_FOOD)
            .isApplicableToBeverage(DEFAULT_IS_APPLICABLE_TO_BEVERAGE)
            .isApplicableToAlcohol(DEFAULT_IS_APPLICABLE_TO_ALCOHOL)
            .effectiveFrom(DEFAULT_EFFECTIVE_FROM)
            .effectiveTo(DEFAULT_EFFECTIVE_TO)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaxConfig createUpdatedEntity() {
        return new TaxConfig()
            .id(UUID.randomUUID())
            .taxName(UPDATED_TAX_NAME)
            .taxCode(UPDATED_TAX_CODE)
            .taxRate(UPDATED_TAX_RATE)
            .taxType(UPDATED_TAX_TYPE)
            .isApplicableToFood(UPDATED_IS_APPLICABLE_TO_FOOD)
            .isApplicableToBeverage(UPDATED_IS_APPLICABLE_TO_BEVERAGE)
            .isApplicableToAlcohol(UPDATED_IS_APPLICABLE_TO_ALCOHOL)
            .effectiveFrom(UPDATED_EFFECTIVE_FROM)
            .effectiveTo(UPDATED_EFFECTIVE_TO)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TaxConfig.class).block();
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
        taxConfig = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTaxConfig != null) {
            taxConfigRepository.delete(insertedTaxConfig).block();
            insertedTaxConfig = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTaxConfig() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        taxConfig.setId(null);
        // Create the TaxConfig
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);
        var returnedTaxConfigDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TaxConfigDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TaxConfig in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTaxConfig = taxConfigMapper.toEntity(returnedTaxConfigDTO);
        assertTaxConfigUpdatableFieldsEquals(returnedTaxConfig, getPersistedTaxConfig(returnedTaxConfig));

        insertedTaxConfig = returnedTaxConfig;
    }

    @Test
    void createTaxConfigWithExistingId() throws Exception {
        // Create the TaxConfig with an existing ID
        insertedTaxConfig = taxConfigRepository.save(taxConfig).block();
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTaxNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taxConfig.setTaxName(null);

        // Create the TaxConfig, which fails.
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTaxCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taxConfig.setTaxCode(null);

        // Create the TaxConfig, which fails.
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTaxRateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taxConfig.setTaxRate(null);

        // Create the TaxConfig, which fails.
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTaxTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taxConfig.setTaxType(null);

        // Create the TaxConfig, which fails.
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEffectiveFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taxConfig.setEffectiveFrom(null);

        // Create the TaxConfig, which fails.
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTaxConfigsAsStream() {
        // Initialize the database
        taxConfig.setId(UUID.randomUUID());
        taxConfigRepository.save(taxConfig).block();

        List<TaxConfig> taxConfigList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TaxConfigDTO.class)
            .getResponseBody()
            .map(taxConfigMapper::toEntity)
            .filter(taxConfig::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(taxConfigList).isNotNull();
        assertThat(taxConfigList).hasSize(1);
        TaxConfig testTaxConfig = taxConfigList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertTaxConfigAllPropertiesEquals(taxConfig, testTaxConfig);
        assertTaxConfigUpdatableFieldsEquals(taxConfig, testTaxConfig);
    }

    @Test
    void getAllTaxConfigs() {
        // Initialize the database
        taxConfig.setId(UUID.randomUUID());
        insertedTaxConfig = taxConfigRepository.save(taxConfig).block();

        // Get all the taxConfigList
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
            .value(hasItem(taxConfig.getId().toString()))
            .jsonPath("$.[*].taxName")
            .value(hasItem(DEFAULT_TAX_NAME))
            .jsonPath("$.[*].taxCode")
            .value(hasItem(DEFAULT_TAX_CODE))
            .jsonPath("$.[*].taxRate")
            .value(hasItem(sameNumber(DEFAULT_TAX_RATE)))
            .jsonPath("$.[*].taxType")
            .value(hasItem(DEFAULT_TAX_TYPE))
            .jsonPath("$.[*].isApplicableToFood")
            .value(hasItem(DEFAULT_IS_APPLICABLE_TO_FOOD))
            .jsonPath("$.[*].isApplicableToBeverage")
            .value(hasItem(DEFAULT_IS_APPLICABLE_TO_BEVERAGE))
            .jsonPath("$.[*].isApplicableToAlcohol")
            .value(hasItem(DEFAULT_IS_APPLICABLE_TO_ALCOHOL))
            .jsonPath("$.[*].effectiveFrom")
            .value(hasItem(DEFAULT_EFFECTIVE_FROM.toString()))
            .jsonPath("$.[*].effectiveTo")
            .value(hasItem(DEFAULT_EFFECTIVE_TO.toString()))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getTaxConfig() {
        // Initialize the database
        taxConfig.setId(UUID.randomUUID());
        insertedTaxConfig = taxConfigRepository.save(taxConfig).block();

        // Get the taxConfig
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, taxConfig.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(taxConfig.getId().toString()))
            .jsonPath("$.taxName")
            .value(is(DEFAULT_TAX_NAME))
            .jsonPath("$.taxCode")
            .value(is(DEFAULT_TAX_CODE))
            .jsonPath("$.taxRate")
            .value(is(sameNumber(DEFAULT_TAX_RATE)))
            .jsonPath("$.taxType")
            .value(is(DEFAULT_TAX_TYPE))
            .jsonPath("$.isApplicableToFood")
            .value(is(DEFAULT_IS_APPLICABLE_TO_FOOD))
            .jsonPath("$.isApplicableToBeverage")
            .value(is(DEFAULT_IS_APPLICABLE_TO_BEVERAGE))
            .jsonPath("$.isApplicableToAlcohol")
            .value(is(DEFAULT_IS_APPLICABLE_TO_ALCOHOL))
            .jsonPath("$.effectiveFrom")
            .value(is(DEFAULT_EFFECTIVE_FROM.toString()))
            .jsonPath("$.effectiveTo")
            .value(is(DEFAULT_EFFECTIVE_TO.toString()))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingTaxConfig() {
        // Get the taxConfig
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTaxConfig() throws Exception {
        // Initialize the database
        taxConfig.setId(UUID.randomUUID());
        insertedTaxConfig = taxConfigRepository.save(taxConfig).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taxConfig
        TaxConfig updatedTaxConfig = taxConfigRepository.findById(taxConfig.getId()).block();
        updatedTaxConfig
            .taxName(UPDATED_TAX_NAME)
            .taxCode(UPDATED_TAX_CODE)
            .taxRate(UPDATED_TAX_RATE)
            .taxType(UPDATED_TAX_TYPE)
            .isApplicableToFood(UPDATED_IS_APPLICABLE_TO_FOOD)
            .isApplicableToBeverage(UPDATED_IS_APPLICABLE_TO_BEVERAGE)
            .isApplicableToAlcohol(UPDATED_IS_APPLICABLE_TO_ALCOHOL)
            .effectiveFrom(UPDATED_EFFECTIVE_FROM)
            .effectiveTo(UPDATED_EFFECTIVE_TO)
            .isActive(UPDATED_IS_ACTIVE);
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(updatedTaxConfig);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taxConfigDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TaxConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaxConfigToMatchAllProperties(updatedTaxConfig);
    }

    @Test
    void putNonExistingTaxConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taxConfig.setId(UUID.randomUUID());

        // Create the TaxConfig
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, taxConfigDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTaxConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taxConfig.setId(UUID.randomUUID());

        // Create the TaxConfig
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTaxConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taxConfig.setId(UUID.randomUUID());

        // Create the TaxConfig
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TaxConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTaxConfigWithPatch() throws Exception {
        // Initialize the database
        taxConfig.setId(UUID.randomUUID());
        insertedTaxConfig = taxConfigRepository.save(taxConfig).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taxConfig using partial update
        TaxConfig partialUpdatedTaxConfig = new TaxConfig();
        partialUpdatedTaxConfig.setId(taxConfig.getId());

        partialUpdatedTaxConfig
            .taxRate(UPDATED_TAX_RATE)
            .isApplicableToFood(UPDATED_IS_APPLICABLE_TO_FOOD)
            .effectiveTo(UPDATED_EFFECTIVE_TO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTaxConfig.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTaxConfig))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TaxConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaxConfigUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTaxConfig, taxConfig),
            getPersistedTaxConfig(taxConfig)
        );
    }

    @Test
    void fullUpdateTaxConfigWithPatch() throws Exception {
        // Initialize the database
        taxConfig.setId(UUID.randomUUID());
        insertedTaxConfig = taxConfigRepository.save(taxConfig).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taxConfig using partial update
        TaxConfig partialUpdatedTaxConfig = new TaxConfig();
        partialUpdatedTaxConfig.setId(taxConfig.getId());

        partialUpdatedTaxConfig
            .taxName(UPDATED_TAX_NAME)
            .taxCode(UPDATED_TAX_CODE)
            .taxRate(UPDATED_TAX_RATE)
            .taxType(UPDATED_TAX_TYPE)
            .isApplicableToFood(UPDATED_IS_APPLICABLE_TO_FOOD)
            .isApplicableToBeverage(UPDATED_IS_APPLICABLE_TO_BEVERAGE)
            .isApplicableToAlcohol(UPDATED_IS_APPLICABLE_TO_ALCOHOL)
            .effectiveFrom(UPDATED_EFFECTIVE_FROM)
            .effectiveTo(UPDATED_EFFECTIVE_TO)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTaxConfig.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTaxConfig))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TaxConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaxConfigUpdatableFieldsEquals(partialUpdatedTaxConfig, getPersistedTaxConfig(partialUpdatedTaxConfig));
    }

    @Test
    void patchNonExistingTaxConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taxConfig.setId(UUID.randomUUID());

        // Create the TaxConfig
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, taxConfigDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTaxConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taxConfig.setId(UUID.randomUUID());

        // Create the TaxConfig
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TaxConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTaxConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taxConfig.setId(UUID.randomUUID());

        // Create the TaxConfig
        TaxConfigDTO taxConfigDTO = taxConfigMapper.toDto(taxConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(taxConfigDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TaxConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTaxConfig() {
        // Initialize the database
        taxConfig.setId(UUID.randomUUID());
        insertedTaxConfig = taxConfigRepository.save(taxConfig).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taxConfig
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, taxConfig.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taxConfigRepository.count().block();
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

    protected TaxConfig getPersistedTaxConfig(TaxConfig taxConfig) {
        return taxConfigRepository.findById(taxConfig.getId()).block();
    }

    protected void assertPersistedTaxConfigToMatchAllProperties(TaxConfig expectedTaxConfig) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTaxConfigAllPropertiesEquals(expectedTaxConfig, getPersistedTaxConfig(expectedTaxConfig));
        assertTaxConfigUpdatableFieldsEquals(expectedTaxConfig, getPersistedTaxConfig(expectedTaxConfig));
    }

    protected void assertPersistedTaxConfigToMatchUpdatableProperties(TaxConfig expectedTaxConfig) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTaxConfigAllUpdatablePropertiesEquals(expectedTaxConfig, getPersistedTaxConfig(expectedTaxConfig));
        assertTaxConfigUpdatableFieldsEquals(expectedTaxConfig, getPersistedTaxConfig(expectedTaxConfig));
    }
}
