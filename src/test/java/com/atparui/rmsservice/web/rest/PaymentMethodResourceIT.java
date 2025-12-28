package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.PaymentMethodAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.PaymentMethod;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.PaymentMethodRepository;
import com.atparui.rmsservice.service.dto.PaymentMethodDTO;
import com.atparui.rmsservice.service.mapper.PaymentMethodMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link PaymentMethodResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaymentMethodResourceIT {

    private static final String DEFAULT_METHOD_CODE = "AAAAAAAAAA";
    private static final String UPDATED_METHOD_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_METHOD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_METHOD_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/payment-methods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PaymentMethod paymentMethod;

    private PaymentMethod insertedPaymentMethod;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethod createEntity() {
        return new PaymentMethod()
            .id(UUID.randomUUID())
            .methodCode(DEFAULT_METHOD_CODE)
            .methodName(DEFAULT_METHOD_NAME)
            .description(DEFAULT_DESCRIPTION)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethod createUpdatedEntity() {
        return new PaymentMethod()
            .id(UUID.randomUUID())
            .methodCode(UPDATED_METHOD_CODE)
            .methodName(UPDATED_METHOD_NAME)
            .description(UPDATED_DESCRIPTION)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PaymentMethod.class).block();
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
        paymentMethod = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaymentMethod != null) {
            paymentMethodRepository.delete(insertedPaymentMethod).block();
            insertedPaymentMethod = null;
        }
        deleteEntities(em);
    }

    @Test
    void createPaymentMethod() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        paymentMethod.setId(null);
        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);
        var returnedPaymentMethodDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PaymentMethodDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the PaymentMethod in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaymentMethod = paymentMethodMapper.toEntity(returnedPaymentMethodDTO);
        assertPaymentMethodUpdatableFieldsEquals(returnedPaymentMethod, getPersistedPaymentMethod(returnedPaymentMethod));

        insertedPaymentMethod = returnedPaymentMethod;
    }

    @Test
    void createPaymentMethodWithExistingId() throws Exception {
        // Create the PaymentMethod with an existing ID
        insertedPaymentMethod = paymentMethodRepository.save(paymentMethod).block();
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkMethodCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentMethod.setMethodCode(null);

        // Create the PaymentMethod, which fails.
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkMethodNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentMethod.setMethodName(null);

        // Create the PaymentMethod, which fails.
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPaymentMethodsAsStream() {
        // Initialize the database
        paymentMethod.setId(UUID.randomUUID());
        paymentMethodRepository.save(paymentMethod).block();

        List<PaymentMethod> paymentMethodList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PaymentMethodDTO.class)
            .getResponseBody()
            .map(paymentMethodMapper::toEntity)
            .filter(paymentMethod::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(paymentMethodList).isNotNull();
        assertThat(paymentMethodList).hasSize(1);
        PaymentMethod testPaymentMethod = paymentMethodList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertPaymentMethodAllPropertiesEquals(paymentMethod, testPaymentMethod);
        assertPaymentMethodUpdatableFieldsEquals(paymentMethod, testPaymentMethod);
    }

    @Test
    void getAllPaymentMethods() {
        // Initialize the database
        paymentMethod.setId(UUID.randomUUID());
        insertedPaymentMethod = paymentMethodRepository.save(paymentMethod).block();

        // Get all the paymentMethodList
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
            .value(hasItem(paymentMethod.getId().toString()))
            .jsonPath("$.[*].methodCode")
            .value(hasItem(DEFAULT_METHOD_CODE))
            .jsonPath("$.[*].methodName")
            .value(hasItem(DEFAULT_METHOD_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getPaymentMethod() {
        // Initialize the database
        paymentMethod.setId(UUID.randomUUID());
        insertedPaymentMethod = paymentMethodRepository.save(paymentMethod).block();

        // Get the paymentMethod
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, paymentMethod.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(paymentMethod.getId().toString()))
            .jsonPath("$.methodCode")
            .value(is(DEFAULT_METHOD_CODE))
            .jsonPath("$.methodName")
            .value(is(DEFAULT_METHOD_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingPaymentMethod() {
        // Get the paymentMethod
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPaymentMethod() throws Exception {
        // Initialize the database
        paymentMethod.setId(UUID.randomUUID());
        insertedPaymentMethod = paymentMethodRepository.save(paymentMethod).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.findById(paymentMethod.getId()).block();
        updatedPaymentMethod
            .methodCode(UPDATED_METHOD_CODE)
            .methodName(UPDATED_METHOD_NAME)
            .description(UPDATED_DESCRIPTION)
            .isActive(UPDATED_IS_ACTIVE);
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(updatedPaymentMethod);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentMethodDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentMethodToMatchAllProperties(updatedPaymentMethod);
    }

    @Test
    void putNonExistingPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(UUID.randomUUID());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentMethodDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(UUID.randomUUID());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(UUID.randomUUID());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaymentMethodWithPatch() throws Exception {
        // Initialize the database
        paymentMethod.setId(UUID.randomUUID());
        insertedPaymentMethod = paymentMethodRepository.save(paymentMethod).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod using partial update
        PaymentMethod partialUpdatedPaymentMethod = new PaymentMethod();
        partialUpdatedPaymentMethod.setId(paymentMethod.getId());

        partialUpdatedPaymentMethod
            .methodCode(UPDATED_METHOD_CODE)
            .methodName(UPDATED_METHOD_NAME)
            .description(UPDATED_DESCRIPTION)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPaymentMethod.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPaymentMethod))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentMethodUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaymentMethod, paymentMethod),
            getPersistedPaymentMethod(paymentMethod)
        );
    }

    @Test
    void fullUpdatePaymentMethodWithPatch() throws Exception {
        // Initialize the database
        paymentMethod.setId(UUID.randomUUID());
        insertedPaymentMethod = paymentMethodRepository.save(paymentMethod).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod using partial update
        PaymentMethod partialUpdatedPaymentMethod = new PaymentMethod();
        partialUpdatedPaymentMethod.setId(paymentMethod.getId());

        partialUpdatedPaymentMethod
            .methodCode(UPDATED_METHOD_CODE)
            .methodName(UPDATED_METHOD_NAME)
            .description(UPDATED_DESCRIPTION)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPaymentMethod.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPaymentMethod))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentMethodUpdatableFieldsEquals(partialUpdatedPaymentMethod, getPersistedPaymentMethod(partialUpdatedPaymentMethod));
    }

    @Test
    void patchNonExistingPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(UUID.randomUUID());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paymentMethodDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(UUID.randomUUID());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(UUID.randomUUID());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(paymentMethodDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePaymentMethod() {
        // Initialize the database
        paymentMethod.setId(UUID.randomUUID());
        insertedPaymentMethod = paymentMethodRepository.save(paymentMethod).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paymentMethod
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, paymentMethod.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentMethodRepository.count().block();
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

    protected PaymentMethod getPersistedPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.findById(paymentMethod.getId()).block();
    }

    protected void assertPersistedPaymentMethodToMatchAllProperties(PaymentMethod expectedPaymentMethod) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPaymentMethodAllPropertiesEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
        assertPaymentMethodUpdatableFieldsEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
    }

    protected void assertPersistedPaymentMethodToMatchUpdatableProperties(PaymentMethod expectedPaymentMethod) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPaymentMethodAllUpdatablePropertiesEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
        assertPaymentMethodUpdatableFieldsEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
    }
}
