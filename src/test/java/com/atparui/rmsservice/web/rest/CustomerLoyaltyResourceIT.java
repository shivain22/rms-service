package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.CustomerLoyaltyAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.CustomerLoyalty;
import com.atparui.rmsservice.repository.CustomerLoyaltyRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.service.dto.CustomerLoyaltyDTO;
import com.atparui.rmsservice.service.mapper.CustomerLoyaltyMapper;
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
 * Integration tests for the {@link CustomerLoyaltyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CustomerLoyaltyResourceIT {

    private static final BigDecimal DEFAULT_LOYALTY_POINTS = new BigDecimal(1);
    private static final BigDecimal UPDATED_LOYALTY_POINTS = new BigDecimal(2);

    private static final String DEFAULT_TIER = "AAAAAAAAAA";
    private static final String UPDATED_TIER = "BBBBBBBBBB";

    private static final Instant DEFAULT_ENROLLED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENROLLED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_POINTS_EARNED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_POINTS_EARNED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/customer-loyalties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerLoyaltyRepository customerLoyaltyRepository;

    @Autowired
    private CustomerLoyaltyMapper customerLoyaltyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CustomerLoyalty customerLoyalty;

    private CustomerLoyalty insertedCustomerLoyalty;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerLoyalty createEntity() {
        return new CustomerLoyalty()
            .id(UUID.randomUUID())
            .loyaltyPoints(DEFAULT_LOYALTY_POINTS)
            .tier(DEFAULT_TIER)
            .enrolledAt(DEFAULT_ENROLLED_AT)
            .lastPointsEarnedAt(DEFAULT_LAST_POINTS_EARNED_AT)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerLoyalty createUpdatedEntity() {
        return new CustomerLoyalty()
            .id(UUID.randomUUID())
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .tier(UPDATED_TIER)
            .enrolledAt(UPDATED_ENROLLED_AT)
            .lastPointsEarnedAt(UPDATED_LAST_POINTS_EARNED_AT)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CustomerLoyalty.class).block();
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
        customerLoyalty = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCustomerLoyalty != null) {
            customerLoyaltyRepository.delete(insertedCustomerLoyalty).block();
            insertedCustomerLoyalty = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCustomerLoyalty() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        customerLoyalty.setId(null);
        // Create the CustomerLoyalty
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(customerLoyalty);
        var returnedCustomerLoyaltyDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CustomerLoyaltyDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the CustomerLoyalty in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCustomerLoyalty = customerLoyaltyMapper.toEntity(returnedCustomerLoyaltyDTO);
        assertCustomerLoyaltyUpdatableFieldsEquals(returnedCustomerLoyalty, getPersistedCustomerLoyalty(returnedCustomerLoyalty));

        insertedCustomerLoyalty = returnedCustomerLoyalty;
    }

    @Test
    void createCustomerLoyaltyWithExistingId() throws Exception {
        // Create the CustomerLoyalty with an existing ID
        insertedCustomerLoyalty = customerLoyaltyRepository.save(customerLoyalty).block();
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(customerLoyalty);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerLoyalty in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEnrolledAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerLoyalty.setEnrolledAt(null);

        // Create the CustomerLoyalty, which fails.
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(customerLoyalty);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCustomerLoyaltiesAsStream() {
        // Initialize the database
        customerLoyalty.setId(UUID.randomUUID());
        customerLoyaltyRepository.save(customerLoyalty).block();

        List<CustomerLoyalty> customerLoyaltyList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CustomerLoyaltyDTO.class)
            .getResponseBody()
            .map(customerLoyaltyMapper::toEntity)
            .filter(customerLoyalty::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(customerLoyaltyList).isNotNull();
        assertThat(customerLoyaltyList).hasSize(1);
        CustomerLoyalty testCustomerLoyalty = customerLoyaltyList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerLoyaltyAllPropertiesEquals(customerLoyalty, testCustomerLoyalty);
        assertCustomerLoyaltyUpdatableFieldsEquals(customerLoyalty, testCustomerLoyalty);
    }

    @Test
    void getAllCustomerLoyalties() {
        // Initialize the database
        customerLoyalty.setId(UUID.randomUUID());
        insertedCustomerLoyalty = customerLoyaltyRepository.save(customerLoyalty).block();

        // Get all the customerLoyaltyList
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
            .value(hasItem(customerLoyalty.getId().toString()))
            .jsonPath("$.[*].loyaltyPoints")
            .value(hasItem(sameNumber(DEFAULT_LOYALTY_POINTS)))
            .jsonPath("$.[*].tier")
            .value(hasItem(DEFAULT_TIER))
            .jsonPath("$.[*].enrolledAt")
            .value(hasItem(DEFAULT_ENROLLED_AT.toString()))
            .jsonPath("$.[*].lastPointsEarnedAt")
            .value(hasItem(DEFAULT_LAST_POINTS_EARNED_AT.toString()))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getCustomerLoyalty() {
        // Initialize the database
        customerLoyalty.setId(UUID.randomUUID());
        insertedCustomerLoyalty = customerLoyaltyRepository.save(customerLoyalty).block();

        // Get the customerLoyalty
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, customerLoyalty.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(customerLoyalty.getId().toString()))
            .jsonPath("$.loyaltyPoints")
            .value(is(sameNumber(DEFAULT_LOYALTY_POINTS)))
            .jsonPath("$.tier")
            .value(is(DEFAULT_TIER))
            .jsonPath("$.enrolledAt")
            .value(is(DEFAULT_ENROLLED_AT.toString()))
            .jsonPath("$.lastPointsEarnedAt")
            .value(is(DEFAULT_LAST_POINTS_EARNED_AT.toString()))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingCustomerLoyalty() {
        // Get the customerLoyalty
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCustomerLoyalty() throws Exception {
        // Initialize the database
        customerLoyalty.setId(UUID.randomUUID());
        insertedCustomerLoyalty = customerLoyaltyRepository.save(customerLoyalty).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerLoyalty
        CustomerLoyalty updatedCustomerLoyalty = customerLoyaltyRepository.findById(customerLoyalty.getId()).block();
        updatedCustomerLoyalty
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .tier(UPDATED_TIER)
            .enrolledAt(UPDATED_ENROLLED_AT)
            .lastPointsEarnedAt(UPDATED_LAST_POINTS_EARNED_AT)
            .isActive(UPDATED_IS_ACTIVE);
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(updatedCustomerLoyalty);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerLoyaltyDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerLoyalty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerLoyaltyToMatchAllProperties(updatedCustomerLoyalty);
    }

    @Test
    void putNonExistingCustomerLoyalty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerLoyalty.setId(UUID.randomUUID());

        // Create the CustomerLoyalty
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(customerLoyalty);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerLoyaltyDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerLoyalty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCustomerLoyalty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerLoyalty.setId(UUID.randomUUID());

        // Create the CustomerLoyalty
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(customerLoyalty);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerLoyalty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCustomerLoyalty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerLoyalty.setId(UUID.randomUUID());

        // Create the CustomerLoyalty
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(customerLoyalty);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerLoyalty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCustomerLoyaltyWithPatch() throws Exception {
        // Initialize the database
        customerLoyalty.setId(UUID.randomUUID());
        insertedCustomerLoyalty = customerLoyaltyRepository.save(customerLoyalty).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerLoyalty using partial update
        CustomerLoyalty partialUpdatedCustomerLoyalty = new CustomerLoyalty();
        partialUpdatedCustomerLoyalty.setId(customerLoyalty.getId());

        partialUpdatedCustomerLoyalty.lastPointsEarnedAt(UPDATED_LAST_POINTS_EARNED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerLoyalty.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomerLoyalty))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerLoyalty in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerLoyaltyUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCustomerLoyalty, customerLoyalty),
            getPersistedCustomerLoyalty(customerLoyalty)
        );
    }

    @Test
    void fullUpdateCustomerLoyaltyWithPatch() throws Exception {
        // Initialize the database
        customerLoyalty.setId(UUID.randomUUID());
        insertedCustomerLoyalty = customerLoyaltyRepository.save(customerLoyalty).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerLoyalty using partial update
        CustomerLoyalty partialUpdatedCustomerLoyalty = new CustomerLoyalty();
        partialUpdatedCustomerLoyalty.setId(customerLoyalty.getId());

        partialUpdatedCustomerLoyalty
            .loyaltyPoints(UPDATED_LOYALTY_POINTS)
            .tier(UPDATED_TIER)
            .enrolledAt(UPDATED_ENROLLED_AT)
            .lastPointsEarnedAt(UPDATED_LAST_POINTS_EARNED_AT)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerLoyalty.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomerLoyalty))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerLoyalty in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerLoyaltyUpdatableFieldsEquals(
            partialUpdatedCustomerLoyalty,
            getPersistedCustomerLoyalty(partialUpdatedCustomerLoyalty)
        );
    }

    @Test
    void patchNonExistingCustomerLoyalty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerLoyalty.setId(UUID.randomUUID());

        // Create the CustomerLoyalty
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(customerLoyalty);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, customerLoyaltyDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerLoyalty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCustomerLoyalty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerLoyalty.setId(UUID.randomUUID());

        // Create the CustomerLoyalty
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(customerLoyalty);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerLoyalty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCustomerLoyalty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerLoyalty.setId(UUID.randomUUID());

        // Create the CustomerLoyalty
        CustomerLoyaltyDTO customerLoyaltyDTO = customerLoyaltyMapper.toDto(customerLoyalty);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerLoyaltyDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerLoyalty in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCustomerLoyalty() {
        // Initialize the database
        customerLoyalty.setId(UUID.randomUUID());
        insertedCustomerLoyalty = customerLoyaltyRepository.save(customerLoyalty).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customerLoyalty
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, customerLoyalty.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerLoyaltyRepository.count().block();
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

    protected CustomerLoyalty getPersistedCustomerLoyalty(CustomerLoyalty customerLoyalty) {
        return customerLoyaltyRepository.findById(customerLoyalty.getId()).block();
    }

    protected void assertPersistedCustomerLoyaltyToMatchAllProperties(CustomerLoyalty expectedCustomerLoyalty) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerLoyaltyAllPropertiesEquals(expectedCustomerLoyalty, getPersistedCustomerLoyalty(expectedCustomerLoyalty));
        assertCustomerLoyaltyUpdatableFieldsEquals(expectedCustomerLoyalty, getPersistedCustomerLoyalty(expectedCustomerLoyalty));
    }

    protected void assertPersistedCustomerLoyaltyToMatchUpdatableProperties(CustomerLoyalty expectedCustomerLoyalty) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerLoyaltyAllUpdatablePropertiesEquals(expectedCustomerLoyalty, getPersistedCustomerLoyalty(expectedCustomerLoyalty));
        assertCustomerLoyaltyUpdatableFieldsEquals(expectedCustomerLoyalty, getPersistedCustomerLoyalty(expectedCustomerLoyalty));
    }
}
