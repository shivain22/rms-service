package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.OrderItemAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.OrderItem;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.OrderItemRepository;
import com.atparui.rmsservice.service.dto.OrderItemDTO;
import com.atparui.rmsservice.service.mapper.OrderItemMapper;
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
 * Integration tests for the {@link OrderItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrderItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_ITEM_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_ITEM_TOTAL = new BigDecimal(2);

    private static final String DEFAULT_SPECIAL_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_SPECIAL_INSTRUCTIONS = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OrderItem orderItem;

    private OrderItem insertedOrderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createEntity() {
        return new OrderItem()
            .id(UUID.randomUUID())
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .itemTotal(DEFAULT_ITEM_TOTAL)
            .specialInstructions(DEFAULT_SPECIAL_INSTRUCTIONS)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createUpdatedEntity() {
        return new OrderItem()
            .id(UUID.randomUUID())
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .itemTotal(UPDATED_ITEM_TOTAL)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .status(UPDATED_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OrderItem.class).block();
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
        orderItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrderItem != null) {
            orderItemRepository.delete(insertedOrderItem).block();
            insertedOrderItem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        orderItem.setId(null);
        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);
        var returnedOrderItemDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OrderItemDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the OrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrderItem = orderItemMapper.toEntity(returnedOrderItemDTO);
        assertOrderItemUpdatableFieldsEquals(returnedOrderItem, getPersistedOrderItem(returnedOrderItem));

        insertedOrderItem = returnedOrderItem;
    }

    @Test
    void createOrderItemWithExistingId() throws Exception {
        // Create the OrderItem with an existing ID
        insertedOrderItem = orderItemRepository.save(orderItem).block();
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderItem.setQuantity(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderItem.setUnitPrice(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkItemTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderItem.setItemTotal(null);

        // Create the OrderItem, which fails.
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrderItemsAsStream() {
        // Initialize the database
        orderItem.setId(UUID.randomUUID());
        orderItemRepository.save(orderItem).block();

        List<OrderItem> orderItemList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrderItemDTO.class)
            .getResponseBody()
            .map(orderItemMapper::toEntity)
            .filter(orderItem::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(orderItemList).isNotNull();
        assertThat(orderItemList).hasSize(1);
        OrderItem testOrderItem = orderItemList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertOrderItemAllPropertiesEquals(orderItem, testOrderItem);
        assertOrderItemUpdatableFieldsEquals(orderItem, testOrderItem);
    }

    @Test
    void getAllOrderItems() {
        // Initialize the database
        orderItem.setId(UUID.randomUUID());
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        // Get all the orderItemList
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
            .value(hasItem(orderItem.getId().toString()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].unitPrice")
            .value(hasItem(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.[*].itemTotal")
            .value(hasItem(sameNumber(DEFAULT_ITEM_TOTAL)))
            .jsonPath("$.[*].specialInstructions")
            .value(hasItem(DEFAULT_SPECIAL_INSTRUCTIONS))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS));
    }

    @Test
    void getOrderItem() {
        // Initialize the database
        orderItem.setId(UUID.randomUUID());
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        // Get the orderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orderItem.getId().toString()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.unitPrice")
            .value(is(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.itemTotal")
            .value(is(sameNumber(DEFAULT_ITEM_TOTAL)))
            .jsonPath("$.specialInstructions")
            .value(is(DEFAULT_SPECIAL_INSTRUCTIONS))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS));
    }

    @Test
    void getNonExistingOrderItem() {
        // Get the orderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrderItem() throws Exception {
        // Initialize the database
        orderItem.setId(UUID.randomUUID());
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem
        OrderItem updatedOrderItem = orderItemRepository.findById(orderItem.getId()).block();
        updatedOrderItem
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .itemTotal(UPDATED_ITEM_TOTAL)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .status(UPDATED_STATUS);
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(updatedOrderItem);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderItemToMatchAllProperties(updatedOrderItem);
    }

    @Test
    void putNonExistingOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        orderItem.setId(UUID.randomUUID());
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem
            .quantity(UPDATED_QUANTITY)
            .itemTotal(UPDATED_ITEM_TOTAL)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderItem, orderItem),
            getPersistedOrderItem(orderItem)
        );
    }

    @Test
    void fullUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        orderItem.setId(UUID.randomUUID());
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .itemTotal(UPDATED_ITEM_TOTAL)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemUpdatableFieldsEquals(partialUpdatedOrderItem, getPersistedOrderItem(partialUpdatedOrderItem));
    }

    @Test
    void patchNonExistingOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orderItemDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(UUID.randomUUID());

        // Create the OrderItem
        OrderItemDTO orderItemDTO = orderItemMapper.toDto(orderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrderItem() {
        // Initialize the database
        orderItem.setId(UUID.randomUUID());
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderItemRepository.count().block();
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

    protected OrderItem getPersistedOrderItem(OrderItem orderItem) {
        return orderItemRepository.findById(orderItem.getId()).block();
    }

    protected void assertPersistedOrderItemToMatchAllProperties(OrderItem expectedOrderItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderItemAllPropertiesEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
        assertOrderItemUpdatableFieldsEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
    }

    protected void assertPersistedOrderItemToMatchUpdatableProperties(OrderItem expectedOrderItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderItemAllUpdatablePropertiesEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
        assertOrderItemUpdatableFieldsEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
    }
}
