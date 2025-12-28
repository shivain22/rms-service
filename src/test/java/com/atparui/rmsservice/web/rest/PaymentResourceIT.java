package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.PaymentAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.Payment;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.PaymentRepository;
import com.atparui.rmsservice.repository.search.PaymentSearchRepository;
import com.atparui.rmsservice.service.dto.PaymentDTO;
import com.atparui.rmsservice.service.mapper.PaymentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaymentResourceIT {

    private static final String DEFAULT_PAYMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_NUMBER = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final Instant DEFAULT_PAYMENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PAYMENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_GATEWAY_RESPONSE = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_GATEWAY_RESPONSE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_PROCESSED_BY = "AAAAAAAAAA";
    private static final String UPDATED_PROCESSED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Instant DEFAULT_REFUNDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REFUNDED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REFUNDED_BY = "AAAAAAAAAA";
    private static final String UPDATED_REFUNDED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_REFUND_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REFUND_REASON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/payments/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentSearchRepository paymentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Payment payment;

    private Payment insertedPayment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity() {
        return new Payment()
            .id(UUID.randomUUID())
            .paymentNumber(DEFAULT_PAYMENT_NUMBER)
            .amount(DEFAULT_AMOUNT)
            .paymentDate(DEFAULT_PAYMENT_DATE)
            .transactionId(DEFAULT_TRANSACTION_ID)
            .paymentGatewayResponse(DEFAULT_PAYMENT_GATEWAY_RESPONSE)
            .status(DEFAULT_STATUS)
            .processedBy(DEFAULT_PROCESSED_BY)
            .notes(DEFAULT_NOTES)
            .refundedAt(DEFAULT_REFUNDED_AT)
            .refundedBy(DEFAULT_REFUNDED_BY)
            .refundReason(DEFAULT_REFUND_REASON);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity() {
        return new Payment()
            .id(UUID.randomUUID())
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .amount(UPDATED_AMOUNT)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .transactionId(UPDATED_TRANSACTION_ID)
            .paymentGatewayResponse(UPDATED_PAYMENT_GATEWAY_RESPONSE)
            .status(UPDATED_STATUS)
            .processedBy(UPDATED_PROCESSED_BY)
            .notes(UPDATED_NOTES)
            .refundedAt(UPDATED_REFUNDED_AT)
            .refundedBy(UPDATED_REFUNDED_BY)
            .refundReason(UPDATED_REFUND_REASON);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Payment.class).block();
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
        payment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPayment != null) {
            paymentRepository.delete(insertedPayment).block();
            paymentSearchRepository.delete(insertedPayment).block();
            insertedPayment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createPayment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        payment.setId(null);
        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);
        var returnedPaymentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PaymentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Payment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPayment = paymentMapper.toEntity(returnedPaymentDTO);
        assertPaymentUpdatableFieldsEquals(returnedPayment, getPersistedPayment(returnedPayment));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPayment = returnedPayment;
    }

    @Test
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        insertedPayment = paymentRepository.save(payment).block();
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPaymentNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        // set the field null
        payment.setPaymentNumber(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        // set the field null
        payment.setAmount(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPaymentDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        // set the field null
        payment.setPaymentDate(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPayments() {
        // Initialize the database
        payment.setId(UUID.randomUUID());
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList
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
            .value(hasItem(payment.getId().toString()))
            .jsonPath("$.[*].paymentNumber")
            .value(hasItem(DEFAULT_PAYMENT_NUMBER))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].paymentDate")
            .value(hasItem(DEFAULT_PAYMENT_DATE.toString()))
            .jsonPath("$.[*].transactionId")
            .value(hasItem(DEFAULT_TRANSACTION_ID))
            .jsonPath("$.[*].paymentGatewayResponse")
            .value(hasItem(DEFAULT_PAYMENT_GATEWAY_RESPONSE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].processedBy")
            .value(hasItem(DEFAULT_PROCESSED_BY))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES))
            .jsonPath("$.[*].refundedAt")
            .value(hasItem(DEFAULT_REFUNDED_AT.toString()))
            .jsonPath("$.[*].refundedBy")
            .value(hasItem(DEFAULT_REFUNDED_BY))
            .jsonPath("$.[*].refundReason")
            .value(hasItem(DEFAULT_REFUND_REASON));
    }

    @Test
    void getPayment() {
        // Initialize the database
        payment.setId(UUID.randomUUID());
        insertedPayment = paymentRepository.save(payment).block();

        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(payment.getId().toString()))
            .jsonPath("$.paymentNumber")
            .value(is(DEFAULT_PAYMENT_NUMBER))
            .jsonPath("$.amount")
            .value(is(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.paymentDate")
            .value(is(DEFAULT_PAYMENT_DATE.toString()))
            .jsonPath("$.transactionId")
            .value(is(DEFAULT_TRANSACTION_ID))
            .jsonPath("$.paymentGatewayResponse")
            .value(is(DEFAULT_PAYMENT_GATEWAY_RESPONSE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
            .jsonPath("$.processedBy")
            .value(is(DEFAULT_PROCESSED_BY))
            .jsonPath("$.notes")
            .value(is(DEFAULT_NOTES))
            .jsonPath("$.refundedAt")
            .value(is(DEFAULT_REFUNDED_AT.toString()))
            .jsonPath("$.refundedBy")
            .value(is(DEFAULT_REFUNDED_BY))
            .jsonPath("$.refundReason")
            .value(is(DEFAULT_REFUND_REASON));
    }

    @Test
    void getNonExistingPayment() {
        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPayment() throws Exception {
        // Initialize the database
        payment.setId(UUID.randomUUID());
        insertedPayment = paymentRepository.save(payment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentSearchRepository.save(payment).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).block();
        updatedPayment
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .amount(UPDATED_AMOUNT)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .transactionId(UPDATED_TRANSACTION_ID)
            .paymentGatewayResponse(UPDATED_PAYMENT_GATEWAY_RESPONSE)
            .status(UPDATED_STATUS)
            .processedBy(UPDATED_PROCESSED_BY)
            .notes(UPDATED_NOTES)
            .refundedAt(UPDATED_REFUNDED_AT)
            .refundedBy(UPDATED_REFUNDED_BY)
            .refundReason(UPDATED_REFUND_REASON);
        PaymentDTO paymentDTO = paymentMapper.toDto(updatedPayment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentToMatchAllProperties(updatedPayment);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Payment> paymentSearchList = Streamable.of(paymentSearchRepository.findAll().collectList().block()).toList();
                Payment testPaymentSearch = paymentSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertPaymentAllPropertiesEquals(testPaymentSearch, updatedPayment);
                assertPaymentUpdatableFieldsEquals(testPaymentSearch, updatedPayment);
            });
    }

    @Test
    void putNonExistingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        payment.setId(UUID.randomUUID());
        insertedPayment = paymentRepository.save(payment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .paymentDate(UPDATED_PAYMENT_DATE)
            .transactionId(UPDATED_TRANSACTION_ID)
            .notes(UPDATED_NOTES)
            .refundedAt(UPDATED_REFUNDED_AT)
            .refundedBy(UPDATED_REFUNDED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPayment, payment), getPersistedPayment(payment));
    }

    @Test
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        payment.setId(UUID.randomUUID());
        insertedPayment = paymentRepository.save(payment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .amount(UPDATED_AMOUNT)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .transactionId(UPDATED_TRANSACTION_ID)
            .paymentGatewayResponse(UPDATED_PAYMENT_GATEWAY_RESPONSE)
            .status(UPDATED_STATUS)
            .processedBy(UPDATED_PROCESSED_BY)
            .notes(UPDATED_NOTES)
            .refundedAt(UPDATED_REFUNDED_AT)
            .refundedBy(UPDATED_REFUNDED_BY)
            .refundReason(UPDATED_REFUND_REASON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentUpdatableFieldsEquals(partialUpdatedPayment, getPersistedPayment(partialUpdatedPayment));
    }

    @Test
    void patchNonExistingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePayment() {
        // Initialize the database
        payment.setId(UUID.randomUUID());
        insertedPayment = paymentRepository.save(payment).block();
        paymentRepository.save(payment).block();
        paymentSearchRepository.save(payment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the payment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPayment() {
        // Initialize the database
        payment.setId(UUID.randomUUID());
        insertedPayment = paymentRepository.save(payment).block();
        paymentSearchRepository.save(payment).block();

        // Search the payment
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + payment.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(payment.getId().toString()))
            .jsonPath("$.[*].paymentNumber")
            .value(hasItem(DEFAULT_PAYMENT_NUMBER))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].paymentDate")
            .value(hasItem(DEFAULT_PAYMENT_DATE.toString()))
            .jsonPath("$.[*].transactionId")
            .value(hasItem(DEFAULT_TRANSACTION_ID))
            .jsonPath("$.[*].paymentGatewayResponse")
            .value(hasItem(DEFAULT_PAYMENT_GATEWAY_RESPONSE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].processedBy")
            .value(hasItem(DEFAULT_PROCESSED_BY))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES.toString()))
            .jsonPath("$.[*].refundedAt")
            .value(hasItem(DEFAULT_REFUNDED_AT.toString()))
            .jsonPath("$.[*].refundedBy")
            .value(hasItem(DEFAULT_REFUNDED_BY))
            .jsonPath("$.[*].refundReason")
            .value(hasItem(DEFAULT_REFUND_REASON.toString()));
    }

    protected long getRepositoryCount() {
        return paymentRepository.count().block();
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

    protected Payment getPersistedPayment(Payment payment) {
        return paymentRepository.findById(payment.getId()).block();
    }

    protected void assertPersistedPaymentToMatchAllProperties(Payment expectedPayment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPaymentAllPropertiesEquals(expectedPayment, getPersistedPayment(expectedPayment));
        assertPaymentUpdatableFieldsEquals(expectedPayment, getPersistedPayment(expectedPayment));
    }

    protected void assertPersistedPaymentToMatchUpdatableProperties(Payment expectedPayment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPaymentAllUpdatablePropertiesEquals(expectedPayment, getPersistedPayment(expectedPayment));
        assertPaymentUpdatableFieldsEquals(expectedPayment, getPersistedPayment(expectedPayment));
    }
}
