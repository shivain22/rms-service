package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.DiscountAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.Discount;
import com.atparui.rmsservice.repository.DiscountRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.service.dto.DiscountDTO;
import com.atparui.rmsservice.service.mapper.DiscountMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link DiscountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DiscountResourceIT {

    private static final String DEFAULT_DISCOUNT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_DISCOUNT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DISCOUNT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISCOUNT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DISCOUNT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_DISCOUNT_TYPE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_DISCOUNT_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_VALUE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_MIN_ORDER_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MIN_ORDER_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_MAX_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MAX_DISCOUNT_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_APPLICABLE_TO = "AAAAAAAAAA";
    private static final String UPDATED_APPLICABLE_TO = "BBBBBBBBBB";

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_VALID_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_TO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_MAX_USES = 1;
    private static final Integer UPDATED_MAX_USES = 2;

    private static final Integer DEFAULT_CURRENT_USES = 1;
    private static final Integer UPDATED_CURRENT_USES = 2;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/discounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountMapper discountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Discount discount;

    private Discount insertedDiscount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discount createEntity() {
        return new Discount()
            .id(UUID.randomUUID())
            .discountCode(DEFAULT_DISCOUNT_CODE)
            .discountName(DEFAULT_DISCOUNT_NAME)
            .discountType(DEFAULT_DISCOUNT_TYPE)
            .discountValue(DEFAULT_DISCOUNT_VALUE)
            .minOrderAmount(DEFAULT_MIN_ORDER_AMOUNT)
            .maxDiscountAmount(DEFAULT_MAX_DISCOUNT_AMOUNT)
            .applicableTo(DEFAULT_APPLICABLE_TO)
            .validFrom(DEFAULT_VALID_FROM)
            .validTo(DEFAULT_VALID_TO)
            .maxUses(DEFAULT_MAX_USES)
            .currentUses(DEFAULT_CURRENT_USES)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discount createUpdatedEntity() {
        return new Discount()
            .id(UUID.randomUUID())
            .discountCode(UPDATED_DISCOUNT_CODE)
            .discountName(UPDATED_DISCOUNT_NAME)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .minOrderAmount(UPDATED_MIN_ORDER_AMOUNT)
            .maxDiscountAmount(UPDATED_MAX_DISCOUNT_AMOUNT)
            .applicableTo(UPDATED_APPLICABLE_TO)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .maxUses(UPDATED_MAX_USES)
            .currentUses(UPDATED_CURRENT_USES)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Discount.class).block();
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
        discount = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDiscount != null) {
            discountRepository.delete(insertedDiscount).block();
            insertedDiscount = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDiscount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        discount.setId(null);
        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);
        var returnedDiscountDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DiscountDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Discount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDiscount = discountMapper.toEntity(returnedDiscountDTO);
        assertDiscountUpdatableFieldsEquals(returnedDiscount, getPersistedDiscount(returnedDiscount));

        insertedDiscount = returnedDiscount;
    }

    @Test
    void createDiscountWithExistingId() throws Exception {
        // Create the Discount with an existing ID
        insertedDiscount = discountRepository.save(discount).block();
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDiscountNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        discount.setDiscountName(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDiscountTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        discount.setDiscountType(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDiscountValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        discount.setDiscountValue(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        discount.setValidFrom(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDiscountsAsStream() {
        // Initialize the database
        discount.setId(UUID.randomUUID());
        discountRepository.save(discount).block();

        List<Discount> discountList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(DiscountDTO.class)
            .getResponseBody()
            .map(discountMapper::toEntity)
            .filter(discount::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(discountList).isNotNull();
        assertThat(discountList).hasSize(1);
        Discount testDiscount = discountList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertDiscountAllPropertiesEquals(discount, testDiscount);
        assertDiscountUpdatableFieldsEquals(discount, testDiscount);
    }

    @Test
    void getAllDiscounts() {
        // Initialize the database
        discount.setId(UUID.randomUUID());
        insertedDiscount = discountRepository.save(discount).block();

        // Get all the discountList
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
            .value(hasItem(discount.getId().toString()))
            .jsonPath("$.[*].discountCode")
            .value(hasItem(DEFAULT_DISCOUNT_CODE))
            .jsonPath("$.[*].discountName")
            .value(hasItem(DEFAULT_DISCOUNT_NAME))
            .jsonPath("$.[*].discountType")
            .value(hasItem(DEFAULT_DISCOUNT_TYPE))
            .jsonPath("$.[*].discountValue")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE)))
            .jsonPath("$.[*].minOrderAmount")
            .value(hasItem(sameNumber(DEFAULT_MIN_ORDER_AMOUNT)))
            .jsonPath("$.[*].maxDiscountAmount")
            .value(hasItem(sameNumber(DEFAULT_MAX_DISCOUNT_AMOUNT)))
            .jsonPath("$.[*].applicableTo")
            .value(hasItem(DEFAULT_APPLICABLE_TO))
            .jsonPath("$.[*].validFrom")
            .value(hasItem(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.[*].validTo")
            .value(hasItem(DEFAULT_VALID_TO.toString()))
            .jsonPath("$.[*].maxUses")
            .value(hasItem(DEFAULT_MAX_USES))
            .jsonPath("$.[*].currentUses")
            .value(hasItem(DEFAULT_CURRENT_USES))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getDiscount() {
        // Initialize the database
        discount.setId(UUID.randomUUID());
        insertedDiscount = discountRepository.save(discount).block();

        // Get the discount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, discount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(discount.getId().toString()))
            .jsonPath("$.discountCode")
            .value(is(DEFAULT_DISCOUNT_CODE))
            .jsonPath("$.discountName")
            .value(is(DEFAULT_DISCOUNT_NAME))
            .jsonPath("$.discountType")
            .value(is(DEFAULT_DISCOUNT_TYPE))
            .jsonPath("$.discountValue")
            .value(is(sameNumber(DEFAULT_DISCOUNT_VALUE)))
            .jsonPath("$.minOrderAmount")
            .value(is(sameNumber(DEFAULT_MIN_ORDER_AMOUNT)))
            .jsonPath("$.maxDiscountAmount")
            .value(is(sameNumber(DEFAULT_MAX_DISCOUNT_AMOUNT)))
            .jsonPath("$.applicableTo")
            .value(is(DEFAULT_APPLICABLE_TO))
            .jsonPath("$.validFrom")
            .value(is(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.validTo")
            .value(is(DEFAULT_VALID_TO.toString()))
            .jsonPath("$.maxUses")
            .value(is(DEFAULT_MAX_USES))
            .jsonPath("$.currentUses")
            .value(is(DEFAULT_CURRENT_USES))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingDiscount() {
        // Get the discount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDiscount() throws Exception {
        // Initialize the database
        discount.setId(UUID.randomUUID());
        insertedDiscount = discountRepository.save(discount).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discount
        Discount updatedDiscount = discountRepository.findById(discount.getId()).block();
        updatedDiscount
            .discountCode(UPDATED_DISCOUNT_CODE)
            .discountName(UPDATED_DISCOUNT_NAME)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .minOrderAmount(UPDATED_MIN_ORDER_AMOUNT)
            .maxDiscountAmount(UPDATED_MAX_DISCOUNT_AMOUNT)
            .applicableTo(UPDATED_APPLICABLE_TO)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .maxUses(UPDATED_MAX_USES)
            .currentUses(UPDATED_CURRENT_USES)
            .isActive(UPDATED_IS_ACTIVE);
        DiscountDTO discountDTO = discountMapper.toDto(updatedDiscount);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, discountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDiscountToMatchAllProperties(updatedDiscount);
    }

    @Test
    void putNonExistingDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(UUID.randomUUID());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, discountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(UUID.randomUUID());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(UUID.randomUUID());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDiscountWithPatch() throws Exception {
        // Initialize the database
        discount.setId(UUID.randomUUID());
        insertedDiscount = discountRepository.save(discount).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discount using partial update
        Discount partialUpdatedDiscount = new Discount();
        partialUpdatedDiscount.setId(discount.getId());

        partialUpdatedDiscount
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .maxDiscountAmount(UPDATED_MAX_DISCOUNT_AMOUNT)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .maxUses(UPDATED_MAX_USES)
            .currentUses(UPDATED_CURRENT_USES)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDiscount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDiscount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Discount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiscountUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDiscount, discount), getPersistedDiscount(discount));
    }

    @Test
    void fullUpdateDiscountWithPatch() throws Exception {
        // Initialize the database
        discount.setId(UUID.randomUUID());
        insertedDiscount = discountRepository.save(discount).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the discount using partial update
        Discount partialUpdatedDiscount = new Discount();
        partialUpdatedDiscount.setId(discount.getId());

        partialUpdatedDiscount
            .discountCode(UPDATED_DISCOUNT_CODE)
            .discountName(UPDATED_DISCOUNT_NAME)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .minOrderAmount(UPDATED_MIN_ORDER_AMOUNT)
            .maxDiscountAmount(UPDATED_MAX_DISCOUNT_AMOUNT)
            .applicableTo(UPDATED_APPLICABLE_TO)
            .validFrom(UPDATED_VALID_FROM)
            .validTo(UPDATED_VALID_TO)
            .maxUses(UPDATED_MAX_USES)
            .currentUses(UPDATED_CURRENT_USES)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDiscount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDiscount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Discount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDiscountUpdatableFieldsEquals(partialUpdatedDiscount, getPersistedDiscount(partialUpdatedDiscount));
    }

    @Test
    void patchNonExistingDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(UUID.randomUUID());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, discountDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(UUID.randomUUID());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDiscount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        discount.setId(UUID.randomUUID());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(discountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Discount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDiscount() {
        // Initialize the database
        discount.setId(UUID.randomUUID());
        insertedDiscount = discountRepository.save(discount).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the discount
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, discount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return discountRepository.count().block();
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

    protected Discount getPersistedDiscount(Discount discount) {
        return discountRepository.findById(discount.getId()).block();
    }

    protected void assertPersistedDiscountToMatchAllProperties(Discount expectedDiscount) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDiscountAllPropertiesEquals(expectedDiscount, getPersistedDiscount(expectedDiscount));
        assertDiscountUpdatableFieldsEquals(expectedDiscount, getPersistedDiscount(expectedDiscount));
    }

    protected void assertPersistedDiscountToMatchUpdatableProperties(Discount expectedDiscount) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDiscountAllUpdatablePropertiesEquals(expectedDiscount, getPersistedDiscount(expectedDiscount));
        assertDiscountUpdatableFieldsEquals(expectedDiscount, getPersistedDiscount(expectedDiscount));
    }
}
