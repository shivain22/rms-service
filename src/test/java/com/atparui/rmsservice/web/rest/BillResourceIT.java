package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.BillAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.Bill;
import com.atparui.rmsservice.repository.BillRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.search.BillSearchRepository;
import com.atparui.rmsservice.service.dto.BillDTO;
import com.atparui.rmsservice.service.mapper.BillMapper;
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
 * Integration tests for the {@link BillResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BillResourceIT {

    private static final String DEFAULT_BILL_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_BILL_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_BILL_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BILL_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TAX_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAX_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_SERVICE_CHARGE = new BigDecimal(1);
    private static final BigDecimal UPDATED_SERVICE_CHARGE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_AMOUNT_PAID = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_PAID = new BigDecimal(2);

    private static final BigDecimal DEFAULT_AMOUNT_DUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_DUE = new BigDecimal(2);

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_GENERATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_GENERATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bills";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/bills/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private BillSearchRepository billSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Bill bill;

    private Bill insertedBill;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bill createEntity() {
        return new Bill()
            .id(UUID.randomUUID())
            .billNumber(DEFAULT_BILL_NUMBER)
            .billDate(DEFAULT_BILL_DATE)
            .subtotal(DEFAULT_SUBTOTAL)
            .taxAmount(DEFAULT_TAX_AMOUNT)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .serviceCharge(DEFAULT_SERVICE_CHARGE)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .amountPaid(DEFAULT_AMOUNT_PAID)
            .amountDue(DEFAULT_AMOUNT_DUE)
            .status(DEFAULT_STATUS)
            .generatedBy(DEFAULT_GENERATED_BY)
            .notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bill createUpdatedEntity() {
        return new Bill()
            .id(UUID.randomUUID())
            .billNumber(UPDATED_BILL_NUMBER)
            .billDate(UPDATED_BILL_DATE)
            .subtotal(UPDATED_SUBTOTAL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .serviceCharge(UPDATED_SERVICE_CHARGE)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .amountDue(UPDATED_AMOUNT_DUE)
            .status(UPDATED_STATUS)
            .generatedBy(UPDATED_GENERATED_BY)
            .notes(UPDATED_NOTES);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Bill.class).block();
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
        bill = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBill != null) {
            billRepository.delete(insertedBill).block();
            billSearchRepository.delete(insertedBill).block();
            insertedBill = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBill() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        bill.setId(null);
        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);
        var returnedBillDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BillDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Bill in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBill = billMapper.toEntity(returnedBillDTO);
        assertBillUpdatableFieldsEquals(returnedBill, getPersistedBill(returnedBill));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedBill = returnedBill;
    }

    @Test
    void createBillWithExistingId() throws Exception {
        // Create the Bill with an existing ID
        insertedBill = billRepository.save(bill).block();
        BillDTO billDTO = billMapper.toDto(bill);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkBillNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        // set the field null
        bill.setBillNumber(null);

        // Create the Bill, which fails.
        BillDTO billDTO = billMapper.toDto(bill);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkBillDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        // set the field null
        bill.setBillDate(null);

        // Create the Bill, which fails.
        BillDTO billDTO = billMapper.toDto(bill);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSubtotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        // set the field null
        bill.setSubtotal(null);

        // Create the Bill, which fails.
        BillDTO billDTO = billMapper.toDto(bill);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        // set the field null
        bill.setTotalAmount(null);

        // Create the Bill, which fails.
        BillDTO billDTO = billMapper.toDto(bill);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkAmountDueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        // set the field null
        bill.setAmountDue(null);

        // Create the Bill, which fails.
        BillDTO billDTO = billMapper.toDto(bill);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllBills() {
        // Initialize the database
        bill.setId(UUID.randomUUID());
        insertedBill = billRepository.save(bill).block();

        // Get all the billList
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
            .value(hasItem(bill.getId().toString()))
            .jsonPath("$.[*].billNumber")
            .value(hasItem(DEFAULT_BILL_NUMBER))
            .jsonPath("$.[*].billDate")
            .value(hasItem(DEFAULT_BILL_DATE.toString()))
            .jsonPath("$.[*].subtotal")
            .value(hasItem(sameNumber(DEFAULT_SUBTOTAL)))
            .jsonPath("$.[*].taxAmount")
            .value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT)))
            .jsonPath("$.[*].discountAmount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.[*].serviceCharge")
            .value(hasItem(sameNumber(DEFAULT_SERVICE_CHARGE)))
            .jsonPath("$.[*].totalAmount")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .jsonPath("$.[*].amountPaid")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID)))
            .jsonPath("$.[*].amountDue")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT_DUE)))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].generatedBy")
            .value(hasItem(DEFAULT_GENERATED_BY))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES));
    }

    @Test
    void getBill() {
        // Initialize the database
        bill.setId(UUID.randomUUID());
        insertedBill = billRepository.save(bill).block();

        // Get the bill
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, bill.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(bill.getId().toString()))
            .jsonPath("$.billNumber")
            .value(is(DEFAULT_BILL_NUMBER))
            .jsonPath("$.billDate")
            .value(is(DEFAULT_BILL_DATE.toString()))
            .jsonPath("$.subtotal")
            .value(is(sameNumber(DEFAULT_SUBTOTAL)))
            .jsonPath("$.taxAmount")
            .value(is(sameNumber(DEFAULT_TAX_AMOUNT)))
            .jsonPath("$.discountAmount")
            .value(is(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.serviceCharge")
            .value(is(sameNumber(DEFAULT_SERVICE_CHARGE)))
            .jsonPath("$.totalAmount")
            .value(is(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .jsonPath("$.amountPaid")
            .value(is(sameNumber(DEFAULT_AMOUNT_PAID)))
            .jsonPath("$.amountDue")
            .value(is(sameNumber(DEFAULT_AMOUNT_DUE)))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
            .jsonPath("$.generatedBy")
            .value(is(DEFAULT_GENERATED_BY))
            .jsonPath("$.notes")
            .value(is(DEFAULT_NOTES));
    }

    @Test
    void getNonExistingBill() {
        // Get the bill
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBill() throws Exception {
        // Initialize the database
        bill.setId(UUID.randomUUID());
        insertedBill = billRepository.save(bill).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        billSearchRepository.save(bill).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());

        // Update the bill
        Bill updatedBill = billRepository.findById(bill.getId()).block();
        updatedBill
            .billNumber(UPDATED_BILL_NUMBER)
            .billDate(UPDATED_BILL_DATE)
            .subtotal(UPDATED_SUBTOTAL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .serviceCharge(UPDATED_SERVICE_CHARGE)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .amountDue(UPDATED_AMOUNT_DUE)
            .status(UPDATED_STATUS)
            .generatedBy(UPDATED_GENERATED_BY)
            .notes(UPDATED_NOTES);
        BillDTO billDTO = billMapper.toDto(updatedBill);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, billDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBillToMatchAllProperties(updatedBill);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Bill> billSearchList = Streamable.of(billSearchRepository.findAll().collectList().block()).toList();
                Bill testBillSearch = billSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertBillAllPropertiesEquals(testBillSearch, updatedBill);
                assertBillUpdatableFieldsEquals(testBillSearch, updatedBill);
            });
    }

    @Test
    void putNonExistingBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        bill.setId(UUID.randomUUID());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, billDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        bill.setId(UUID.randomUUID());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        bill.setId(UUID.randomUUID());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateBillWithPatch() throws Exception {
        // Initialize the database
        bill.setId(UUID.randomUUID());
        insertedBill = billRepository.save(bill).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bill using partial update
        Bill partialUpdatedBill = new Bill();
        partialUpdatedBill.setId(bill.getId());

        partialUpdatedBill
            .billNumber(UPDATED_BILL_NUMBER)
            .billDate(UPDATED_BILL_DATE)
            .serviceCharge(UPDATED_SERVICE_CHARGE)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .amountDue(UPDATED_AMOUNT_DUE)
            .generatedBy(UPDATED_GENERATED_BY)
            .notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBill.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBill))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bill in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBill, bill), getPersistedBill(bill));
    }

    @Test
    void fullUpdateBillWithPatch() throws Exception {
        // Initialize the database
        bill.setId(UUID.randomUUID());
        insertedBill = billRepository.save(bill).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bill using partial update
        Bill partialUpdatedBill = new Bill();
        partialUpdatedBill.setId(bill.getId());

        partialUpdatedBill
            .billNumber(UPDATED_BILL_NUMBER)
            .billDate(UPDATED_BILL_DATE)
            .subtotal(UPDATED_SUBTOTAL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .serviceCharge(UPDATED_SERVICE_CHARGE)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .amountDue(UPDATED_AMOUNT_DUE)
            .status(UPDATED_STATUS)
            .generatedBy(UPDATED_GENERATED_BY)
            .notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBill.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBill))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bill in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillUpdatableFieldsEquals(partialUpdatedBill, getPersistedBill(partialUpdatedBill));
    }

    @Test
    void patchNonExistingBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        bill.setId(UUID.randomUUID());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, billDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        bill.setId(UUID.randomUUID());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamBill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        bill.setId(UUID.randomUUID());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteBill() {
        // Initialize the database
        bill.setId(UUID.randomUUID());
        insertedBill = billRepository.save(bill).block();
        billRepository.save(bill).block();
        billSearchRepository.save(bill).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the bill
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, bill.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(billSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchBill() {
        // Initialize the database
        bill.setId(UUID.randomUUID());
        insertedBill = billRepository.save(bill).block();
        billSearchRepository.save(bill).block();

        // Search the bill
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + bill.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(bill.getId().toString()))
            .jsonPath("$.[*].billNumber")
            .value(hasItem(DEFAULT_BILL_NUMBER))
            .jsonPath("$.[*].billDate")
            .value(hasItem(DEFAULT_BILL_DATE.toString()))
            .jsonPath("$.[*].subtotal")
            .value(hasItem(sameNumber(DEFAULT_SUBTOTAL)))
            .jsonPath("$.[*].taxAmount")
            .value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT)))
            .jsonPath("$.[*].discountAmount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.[*].serviceCharge")
            .value(hasItem(sameNumber(DEFAULT_SERVICE_CHARGE)))
            .jsonPath("$.[*].totalAmount")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .jsonPath("$.[*].amountPaid")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID)))
            .jsonPath("$.[*].amountDue")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT_DUE)))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].generatedBy")
            .value(hasItem(DEFAULT_GENERATED_BY))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES.toString()));
    }

    protected long getRepositoryCount() {
        return billRepository.count().block();
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

    protected Bill getPersistedBill(Bill bill) {
        return billRepository.findById(bill.getId()).block();
    }

    protected void assertPersistedBillToMatchAllProperties(Bill expectedBill) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBillAllPropertiesEquals(expectedBill, getPersistedBill(expectedBill));
        assertBillUpdatableFieldsEquals(expectedBill, getPersistedBill(expectedBill));
    }

    protected void assertPersistedBillToMatchUpdatableProperties(Bill expectedBill) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBillAllUpdatablePropertiesEquals(expectedBill, getPersistedBill(expectedBill));
        assertBillUpdatableFieldsEquals(expectedBill, getPersistedBill(expectedBill));
    }
}
