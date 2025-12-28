package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.BillTaxAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.BillTax;
import com.atparui.rmsservice.repository.BillTaxRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.service.dto.BillTaxDTO;
import com.atparui.rmsservice.service.mapper.BillTaxMapper;
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
 * Integration tests for the {@link BillTaxResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BillTaxResourceIT {

    private static final String DEFAULT_TAX_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TAX_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAX_RATE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TAXABLE_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAXABLE_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TAX_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAX_AMOUNT = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/bill-taxes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BillTaxRepository billTaxRepository;

    @Autowired
    private BillTaxMapper billTaxMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private BillTax billTax;

    private BillTax insertedBillTax;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillTax createEntity() {
        return new BillTax()
            .id(UUID.randomUUID())
            .taxName(DEFAULT_TAX_NAME)
            .taxRate(DEFAULT_TAX_RATE)
            .taxableAmount(DEFAULT_TAXABLE_AMOUNT)
            .taxAmount(DEFAULT_TAX_AMOUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillTax createUpdatedEntity() {
        return new BillTax()
            .id(UUID.randomUUID())
            .taxName(UPDATED_TAX_NAME)
            .taxRate(UPDATED_TAX_RATE)
            .taxableAmount(UPDATED_TAXABLE_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(BillTax.class).block();
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
        billTax = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBillTax != null) {
            billTaxRepository.delete(insertedBillTax).block();
            insertedBillTax = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBillTax() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        billTax.setId(null);
        // Create the BillTax
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);
        var returnedBillTaxDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BillTaxDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the BillTax in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBillTax = billTaxMapper.toEntity(returnedBillTaxDTO);
        assertBillTaxUpdatableFieldsEquals(returnedBillTax, getPersistedBillTax(returnedBillTax));

        insertedBillTax = returnedBillTax;
    }

    @Test
    void createBillTaxWithExistingId() throws Exception {
        // Create the BillTax with an existing ID
        insertedBillTax = billTaxRepository.save(billTax).block();
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillTax in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTaxNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billTax.setTaxName(null);

        // Create the BillTax, which fails.
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTaxRateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billTax.setTaxRate(null);

        // Create the BillTax, which fails.
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTaxableAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billTax.setTaxableAmount(null);

        // Create the BillTax, which fails.
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTaxAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billTax.setTaxAmount(null);

        // Create the BillTax, which fails.
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllBillTaxesAsStream() {
        // Initialize the database
        billTax.setId(UUID.randomUUID());
        billTaxRepository.save(billTax).block();

        List<BillTax> billTaxList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(BillTaxDTO.class)
            .getResponseBody()
            .map(billTaxMapper::toEntity)
            .filter(billTax::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(billTaxList).isNotNull();
        assertThat(billTaxList).hasSize(1);
        BillTax testBillTax = billTaxList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertBillTaxAllPropertiesEquals(billTax, testBillTax);
        assertBillTaxUpdatableFieldsEquals(billTax, testBillTax);
    }

    @Test
    void getAllBillTaxes() {
        // Initialize the database
        billTax.setId(UUID.randomUUID());
        insertedBillTax = billTaxRepository.save(billTax).block();

        // Get all the billTaxList
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
            .value(hasItem(billTax.getId().toString()))
            .jsonPath("$.[*].taxName")
            .value(hasItem(DEFAULT_TAX_NAME))
            .jsonPath("$.[*].taxRate")
            .value(hasItem(sameNumber(DEFAULT_TAX_RATE)))
            .jsonPath("$.[*].taxableAmount")
            .value(hasItem(sameNumber(DEFAULT_TAXABLE_AMOUNT)))
            .jsonPath("$.[*].taxAmount")
            .value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT)));
    }

    @Test
    void getBillTax() {
        // Initialize the database
        billTax.setId(UUID.randomUUID());
        insertedBillTax = billTaxRepository.save(billTax).block();

        // Get the billTax
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, billTax.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(billTax.getId().toString()))
            .jsonPath("$.taxName")
            .value(is(DEFAULT_TAX_NAME))
            .jsonPath("$.taxRate")
            .value(is(sameNumber(DEFAULT_TAX_RATE)))
            .jsonPath("$.taxableAmount")
            .value(is(sameNumber(DEFAULT_TAXABLE_AMOUNT)))
            .jsonPath("$.taxAmount")
            .value(is(sameNumber(DEFAULT_TAX_AMOUNT)));
    }

    @Test
    void getNonExistingBillTax() {
        // Get the billTax
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBillTax() throws Exception {
        // Initialize the database
        billTax.setId(UUID.randomUUID());
        insertedBillTax = billTaxRepository.save(billTax).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the billTax
        BillTax updatedBillTax = billTaxRepository.findById(billTax.getId()).block();
        updatedBillTax
            .taxName(UPDATED_TAX_NAME)
            .taxRate(UPDATED_TAX_RATE)
            .taxableAmount(UPDATED_TAXABLE_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT);
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(updatedBillTax);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, billTaxDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BillTax in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBillTaxToMatchAllProperties(updatedBillTax);
    }

    @Test
    void putNonExistingBillTax() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billTax.setId(UUID.randomUUID());

        // Create the BillTax
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, billTaxDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillTax in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBillTax() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billTax.setId(UUID.randomUUID());

        // Create the BillTax
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillTax in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBillTax() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billTax.setId(UUID.randomUUID());

        // Create the BillTax
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BillTax in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBillTaxWithPatch() throws Exception {
        // Initialize the database
        billTax.setId(UUID.randomUUID());
        insertedBillTax = billTaxRepository.save(billTax).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the billTax using partial update
        BillTax partialUpdatedBillTax = new BillTax();
        partialUpdatedBillTax.setId(billTax.getId());

        partialUpdatedBillTax.taxName(UPDATED_TAX_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBillTax.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBillTax))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BillTax in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillTaxUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBillTax, billTax), getPersistedBillTax(billTax));
    }

    @Test
    void fullUpdateBillTaxWithPatch() throws Exception {
        // Initialize the database
        billTax.setId(UUID.randomUUID());
        insertedBillTax = billTaxRepository.save(billTax).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the billTax using partial update
        BillTax partialUpdatedBillTax = new BillTax();
        partialUpdatedBillTax.setId(billTax.getId());

        partialUpdatedBillTax
            .taxName(UPDATED_TAX_NAME)
            .taxRate(UPDATED_TAX_RATE)
            .taxableAmount(UPDATED_TAXABLE_AMOUNT)
            .taxAmount(UPDATED_TAX_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBillTax.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBillTax))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BillTax in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillTaxUpdatableFieldsEquals(partialUpdatedBillTax, getPersistedBillTax(partialUpdatedBillTax));
    }

    @Test
    void patchNonExistingBillTax() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billTax.setId(UUID.randomUUID());

        // Create the BillTax
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, billTaxDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillTax in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBillTax() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billTax.setId(UUID.randomUUID());

        // Create the BillTax
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillTax in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBillTax() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billTax.setId(UUID.randomUUID());

        // Create the BillTax
        BillTaxDTO billTaxDTO = billTaxMapper.toDto(billTax);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billTaxDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BillTax in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBillTax() {
        // Initialize the database
        billTax.setId(UUID.randomUUID());
        insertedBillTax = billTaxRepository.save(billTax).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the billTax
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, billTax.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return billTaxRepository.count().block();
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

    protected BillTax getPersistedBillTax(BillTax billTax) {
        return billTaxRepository.findById(billTax.getId()).block();
    }

    protected void assertPersistedBillTaxToMatchAllProperties(BillTax expectedBillTax) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBillTaxAllPropertiesEquals(expectedBillTax, getPersistedBillTax(expectedBillTax));
        assertBillTaxUpdatableFieldsEquals(expectedBillTax, getPersistedBillTax(expectedBillTax));
    }

    protected void assertPersistedBillTaxToMatchUpdatableProperties(BillTax expectedBillTax) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBillTaxAllUpdatablePropertiesEquals(expectedBillTax, getPersistedBillTax(expectedBillTax));
        assertBillTaxUpdatableFieldsEquals(expectedBillTax, getPersistedBillTax(expectedBillTax));
    }
}
