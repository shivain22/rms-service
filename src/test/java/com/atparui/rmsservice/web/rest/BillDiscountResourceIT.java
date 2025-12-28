package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.BillDiscountAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.BillDiscount;
import com.atparui.rmsservice.repository.BillDiscountRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.service.dto.BillDiscountDTO;
import com.atparui.rmsservice.service.mapper.BillDiscountMapper;
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
 * Integration tests for the {@link BillDiscountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BillDiscountResourceIT {

    private static final String DEFAULT_DISCOUNT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_DISCOUNT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DISCOUNT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_DISCOUNT_TYPE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_DISCOUNT_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_VALUE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/bill-discounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BillDiscountRepository billDiscountRepository;

    @Autowired
    private BillDiscountMapper billDiscountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private BillDiscount billDiscount;

    private BillDiscount insertedBillDiscount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillDiscount createEntity() {
        return new BillDiscount()
            .id(UUID.randomUUID())
            .discountCode(DEFAULT_DISCOUNT_CODE)
            .discountType(DEFAULT_DISCOUNT_TYPE)
            .discountValue(DEFAULT_DISCOUNT_VALUE)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillDiscount createUpdatedEntity() {
        return new BillDiscount()
            .id(UUID.randomUUID())
            .discountCode(UPDATED_DISCOUNT_CODE)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(BillDiscount.class).block();
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
        billDiscount = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBillDiscount != null) {
            billDiscountRepository.delete(insertedBillDiscount).block();
            insertedBillDiscount = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBillDiscount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        billDiscount.setId(null);
        // Create the BillDiscount
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);
        var returnedBillDiscountDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BillDiscountDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the BillDiscount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBillDiscount = billDiscountMapper.toEntity(returnedBillDiscountDTO);
        assertBillDiscountUpdatableFieldsEquals(returnedBillDiscount, getPersistedBillDiscount(returnedBillDiscount));

        insertedBillDiscount = returnedBillDiscount;
    }

    @Test
    void createBillDiscountWithExistingId() throws Exception {
        // Create the BillDiscount with an existing ID
        insertedBillDiscount = billDiscountRepository.save(billDiscount).block();
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDiscountTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billDiscount.setDiscountType(null);

        // Create the BillDiscount, which fails.
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDiscountValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billDiscount.setDiscountValue(null);

        // Create the BillDiscount, which fails.
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDiscountAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billDiscount.setDiscountAmount(null);

        // Create the BillDiscount, which fails.
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllBillDiscountsAsStream() {
        // Initialize the database
        billDiscount.setId(UUID.randomUUID());
        billDiscountRepository.save(billDiscount).block();

        List<BillDiscount> billDiscountList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(BillDiscountDTO.class)
            .getResponseBody()
            .map(billDiscountMapper::toEntity)
            .filter(billDiscount::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(billDiscountList).isNotNull();
        assertThat(billDiscountList).hasSize(1);
        BillDiscount testBillDiscount = billDiscountList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertBillDiscountAllPropertiesEquals(billDiscount, testBillDiscount);
        assertBillDiscountUpdatableFieldsEquals(billDiscount, testBillDiscount);
    }

    @Test
    void getAllBillDiscounts() {
        // Initialize the database
        billDiscount.setId(UUID.randomUUID());
        insertedBillDiscount = billDiscountRepository.save(billDiscount).block();

        // Get all the billDiscountList
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
            .value(hasItem(billDiscount.getId().toString()))
            .jsonPath("$.[*].discountCode")
            .value(hasItem(DEFAULT_DISCOUNT_CODE))
            .jsonPath("$.[*].discountType")
            .value(hasItem(DEFAULT_DISCOUNT_TYPE))
            .jsonPath("$.[*].discountValue")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE)))
            .jsonPath("$.[*].discountAmount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT)));
    }

    @Test
    void getBillDiscount() {
        // Initialize the database
        billDiscount.setId(UUID.randomUUID());
        insertedBillDiscount = billDiscountRepository.save(billDiscount).block();

        // Get the billDiscount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, billDiscount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(billDiscount.getId().toString()))
            .jsonPath("$.discountCode")
            .value(is(DEFAULT_DISCOUNT_CODE))
            .jsonPath("$.discountType")
            .value(is(DEFAULT_DISCOUNT_TYPE))
            .jsonPath("$.discountValue")
            .value(is(sameNumber(DEFAULT_DISCOUNT_VALUE)))
            .jsonPath("$.discountAmount")
            .value(is(sameNumber(DEFAULT_DISCOUNT_AMOUNT)));
    }

    @Test
    void getNonExistingBillDiscount() {
        // Get the billDiscount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBillDiscount() throws Exception {
        // Initialize the database
        billDiscount.setId(UUID.randomUUID());
        insertedBillDiscount = billDiscountRepository.save(billDiscount).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the billDiscount
        BillDiscount updatedBillDiscount = billDiscountRepository.findById(billDiscount.getId()).block();
        updatedBillDiscount
            .discountCode(UPDATED_DISCOUNT_CODE)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT);
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(updatedBillDiscount);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, billDiscountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BillDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBillDiscountToMatchAllProperties(updatedBillDiscount);
    }

    @Test
    void putNonExistingBillDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billDiscount.setId(UUID.randomUUID());

        // Create the BillDiscount
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, billDiscountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBillDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billDiscount.setId(UUID.randomUUID());

        // Create the BillDiscount
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBillDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billDiscount.setId(UUID.randomUUID());

        // Create the BillDiscount
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BillDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBillDiscountWithPatch() throws Exception {
        // Initialize the database
        billDiscount.setId(UUID.randomUUID());
        insertedBillDiscount = billDiscountRepository.save(billDiscount).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the billDiscount using partial update
        BillDiscount partialUpdatedBillDiscount = new BillDiscount();
        partialUpdatedBillDiscount.setId(billDiscount.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBillDiscount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBillDiscount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BillDiscount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillDiscountUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBillDiscount, billDiscount),
            getPersistedBillDiscount(billDiscount)
        );
    }

    @Test
    void fullUpdateBillDiscountWithPatch() throws Exception {
        // Initialize the database
        billDiscount.setId(UUID.randomUUID());
        insertedBillDiscount = billDiscountRepository.save(billDiscount).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the billDiscount using partial update
        BillDiscount partialUpdatedBillDiscount = new BillDiscount();
        partialUpdatedBillDiscount.setId(billDiscount.getId());

        partialUpdatedBillDiscount
            .discountCode(UPDATED_DISCOUNT_CODE)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBillDiscount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBillDiscount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BillDiscount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillDiscountUpdatableFieldsEquals(partialUpdatedBillDiscount, getPersistedBillDiscount(partialUpdatedBillDiscount));
    }

    @Test
    void patchNonExistingBillDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billDiscount.setId(UUID.randomUUID());

        // Create the BillDiscount
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, billDiscountDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBillDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billDiscount.setId(UUID.randomUUID());

        // Create the BillDiscount
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBillDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billDiscount.setId(UUID.randomUUID());

        // Create the BillDiscount
        BillDiscountDTO billDiscountDTO = billDiscountMapper.toDto(billDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billDiscountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BillDiscount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBillDiscount() {
        // Initialize the database
        billDiscount.setId(UUID.randomUUID());
        insertedBillDiscount = billDiscountRepository.save(billDiscount).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the billDiscount
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, billDiscount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return billDiscountRepository.count().block();
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

    protected BillDiscount getPersistedBillDiscount(BillDiscount billDiscount) {
        return billDiscountRepository.findById(billDiscount.getId()).block();
    }

    protected void assertPersistedBillDiscountToMatchAllProperties(BillDiscount expectedBillDiscount) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBillDiscountAllPropertiesEquals(expectedBillDiscount, getPersistedBillDiscount(expectedBillDiscount));
        assertBillDiscountUpdatableFieldsEquals(expectedBillDiscount, getPersistedBillDiscount(expectedBillDiscount));
    }

    protected void assertPersistedBillDiscountToMatchUpdatableProperties(BillDiscount expectedBillDiscount) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBillDiscountAllUpdatablePropertiesEquals(expectedBillDiscount, getPersistedBillDiscount(expectedBillDiscount));
        assertBillDiscountUpdatableFieldsEquals(expectedBillDiscount, getPersistedBillDiscount(expectedBillDiscount));
    }
}
