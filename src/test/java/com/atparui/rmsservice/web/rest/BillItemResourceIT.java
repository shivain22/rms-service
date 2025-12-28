package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.BillItemAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.BillItem;
import com.atparui.rmsservice.repository.BillItemRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.service.dto.BillItemDTO;
import com.atparui.rmsservice.service.mapper.BillItemMapper;
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
 * Integration tests for the {@link BillItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BillItemResourceIT {

    private static final String DEFAULT_ITEM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_ITEM_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_ITEM_TOTAL = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/bill-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BillItemRepository billItemRepository;

    @Autowired
    private BillItemMapper billItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private BillItem billItem;

    private BillItem insertedBillItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillItem createEntity() {
        return new BillItem()
            .id(UUID.randomUUID())
            .itemName(DEFAULT_ITEM_NAME)
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .itemTotal(DEFAULT_ITEM_TOTAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillItem createUpdatedEntity() {
        return new BillItem()
            .id(UUID.randomUUID())
            .itemName(UPDATED_ITEM_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .itemTotal(UPDATED_ITEM_TOTAL);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(BillItem.class).block();
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
        billItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBillItem != null) {
            billItemRepository.delete(insertedBillItem).block();
            insertedBillItem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBillItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        billItem.setId(null);
        // Create the BillItem
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);
        var returnedBillItemDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BillItemDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the BillItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBillItem = billItemMapper.toEntity(returnedBillItemDTO);
        assertBillItemUpdatableFieldsEquals(returnedBillItem, getPersistedBillItem(returnedBillItem));

        insertedBillItem = returnedBillItem;
    }

    @Test
    void createBillItemWithExistingId() throws Exception {
        // Create the BillItem with an existing ID
        insertedBillItem = billItemRepository.save(billItem).block();
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkItemNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billItem.setItemName(null);

        // Create the BillItem, which fails.
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billItem.setQuantity(null);

        // Create the BillItem, which fails.
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billItem.setUnitPrice(null);

        // Create the BillItem, which fails.
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkItemTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        billItem.setItemTotal(null);

        // Create the BillItem, which fails.
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllBillItemsAsStream() {
        // Initialize the database
        billItem.setId(UUID.randomUUID());
        billItemRepository.save(billItem).block();

        List<BillItem> billItemList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(BillItemDTO.class)
            .getResponseBody()
            .map(billItemMapper::toEntity)
            .filter(billItem::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(billItemList).isNotNull();
        assertThat(billItemList).hasSize(1);
        BillItem testBillItem = billItemList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertBillItemAllPropertiesEquals(billItem, testBillItem);
        assertBillItemUpdatableFieldsEquals(billItem, testBillItem);
    }

    @Test
    void getAllBillItems() {
        // Initialize the database
        billItem.setId(UUID.randomUUID());
        insertedBillItem = billItemRepository.save(billItem).block();

        // Get all the billItemList
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
            .value(hasItem(billItem.getId().toString()))
            .jsonPath("$.[*].itemName")
            .value(hasItem(DEFAULT_ITEM_NAME))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].unitPrice")
            .value(hasItem(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.[*].itemTotal")
            .value(hasItem(sameNumber(DEFAULT_ITEM_TOTAL)));
    }

    @Test
    void getBillItem() {
        // Initialize the database
        billItem.setId(UUID.randomUUID());
        insertedBillItem = billItemRepository.save(billItem).block();

        // Get the billItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, billItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(billItem.getId().toString()))
            .jsonPath("$.itemName")
            .value(is(DEFAULT_ITEM_NAME))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.unitPrice")
            .value(is(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.itemTotal")
            .value(is(sameNumber(DEFAULT_ITEM_TOTAL)));
    }

    @Test
    void getNonExistingBillItem() {
        // Get the billItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBillItem() throws Exception {
        // Initialize the database
        billItem.setId(UUID.randomUUID());
        insertedBillItem = billItemRepository.save(billItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the billItem
        BillItem updatedBillItem = billItemRepository.findById(billItem.getId()).block();
        updatedBillItem.itemName(UPDATED_ITEM_NAME).quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).itemTotal(UPDATED_ITEM_TOTAL);
        BillItemDTO billItemDTO = billItemMapper.toDto(updatedBillItem);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, billItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BillItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBillItemToMatchAllProperties(updatedBillItem);
    }

    @Test
    void putNonExistingBillItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billItem.setId(UUID.randomUUID());

        // Create the BillItem
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, billItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBillItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billItem.setId(UUID.randomUUID());

        // Create the BillItem
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBillItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billItem.setId(UUID.randomUUID());

        // Create the BillItem
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BillItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBillItemWithPatch() throws Exception {
        // Initialize the database
        billItem.setId(UUID.randomUUID());
        insertedBillItem = billItemRepository.save(billItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the billItem using partial update
        BillItem partialUpdatedBillItem = new BillItem();
        partialUpdatedBillItem.setId(billItem.getId());

        partialUpdatedBillItem
            .itemName(UPDATED_ITEM_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .itemTotal(UPDATED_ITEM_TOTAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBillItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBillItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BillItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillItemUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBillItem, billItem), getPersistedBillItem(billItem));
    }

    @Test
    void fullUpdateBillItemWithPatch() throws Exception {
        // Initialize the database
        billItem.setId(UUID.randomUUID());
        insertedBillItem = billItemRepository.save(billItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the billItem using partial update
        BillItem partialUpdatedBillItem = new BillItem();
        partialUpdatedBillItem.setId(billItem.getId());

        partialUpdatedBillItem
            .itemName(UPDATED_ITEM_NAME)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .itemTotal(UPDATED_ITEM_TOTAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBillItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBillItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BillItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBillItemUpdatableFieldsEquals(partialUpdatedBillItem, getPersistedBillItem(partialUpdatedBillItem));
    }

    @Test
    void patchNonExistingBillItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billItem.setId(UUID.randomUUID());

        // Create the BillItem
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, billItemDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBillItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billItem.setId(UUID.randomUUID());

        // Create the BillItem
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BillItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBillItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        billItem.setId(UUID.randomUUID());

        // Create the BillItem
        BillItemDTO billItemDTO = billItemMapper.toDto(billItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(billItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BillItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBillItem() {
        // Initialize the database
        billItem.setId(UUID.randomUUID());
        insertedBillItem = billItemRepository.save(billItem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the billItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, billItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return billItemRepository.count().block();
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

    protected BillItem getPersistedBillItem(BillItem billItem) {
        return billItemRepository.findById(billItem.getId()).block();
    }

    protected void assertPersistedBillItemToMatchAllProperties(BillItem expectedBillItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBillItemAllPropertiesEquals(expectedBillItem, getPersistedBillItem(expectedBillItem));
        assertBillItemUpdatableFieldsEquals(expectedBillItem, getPersistedBillItem(expectedBillItem));
    }

    protected void assertPersistedBillItemToMatchUpdatableProperties(BillItem expectedBillItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBillItemAllUpdatablePropertiesEquals(expectedBillItem, getPersistedBillItem(expectedBillItem));
        assertBillItemUpdatableFieldsEquals(expectedBillItem, getPersistedBillItem(expectedBillItem));
    }
}
