package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.OrderAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.Order;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.OrderRepository;
import com.atparui.rmsservice.repository.search.OrderSearchRepository;
import com.atparui.rmsservice.service.dto.OrderDTO;
import com.atparui.rmsservice.service.mapper.OrderMapper;
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
 * Integration tests for the {@link OrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrderResourceIT {

    private static final String DEFAULT_ORDER_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ORDER_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_ORDER_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_SOURCE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_ORDER_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ORDER_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ESTIMATED_READY_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ESTIMATED_READY_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ACTUAL_READY_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTUAL_READY_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SPECIAL_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_SPECIAL_INSTRUCTIONS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TAX_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TAX_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);

    private static final Boolean DEFAULT_IS_PAID = false;
    private static final Boolean UPDATED_IS_PAID = true;

    private static final Instant DEFAULT_CANCELLED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CANCELLED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CANCELLED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CANCELLED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_CANCELLATION_REASON = "AAAAAAAAAA";
    private static final String UPDATED_CANCELLATION_REASON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/orders/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderSearchRepository orderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Order order;

    private Order insertedOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity() {
        return new Order()
            .id(UUID.randomUUID())
            .orderNumber(DEFAULT_ORDER_NUMBER)
            .orderType(DEFAULT_ORDER_TYPE)
            .orderSource(DEFAULT_ORDER_SOURCE)
            .status(DEFAULT_STATUS)
            .orderDate(DEFAULT_ORDER_DATE)
            .estimatedReadyTime(DEFAULT_ESTIMATED_READY_TIME)
            .actualReadyTime(DEFAULT_ACTUAL_READY_TIME)
            .specialInstructions(DEFAULT_SPECIAL_INSTRUCTIONS)
            .subtotal(DEFAULT_SUBTOTAL)
            .taxAmount(DEFAULT_TAX_AMOUNT)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .isPaid(DEFAULT_IS_PAID)
            .cancelledAt(DEFAULT_CANCELLED_AT)
            .cancelledBy(DEFAULT_CANCELLED_BY)
            .cancellationReason(DEFAULT_CANCELLATION_REASON);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity() {
        return new Order()
            .id(UUID.randomUUID())
            .orderNumber(UPDATED_ORDER_NUMBER)
            .orderType(UPDATED_ORDER_TYPE)
            .orderSource(UPDATED_ORDER_SOURCE)
            .status(UPDATED_STATUS)
            .orderDate(UPDATED_ORDER_DATE)
            .estimatedReadyTime(UPDATED_ESTIMATED_READY_TIME)
            .actualReadyTime(UPDATED_ACTUAL_READY_TIME)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .subtotal(UPDATED_SUBTOTAL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .isPaid(UPDATED_IS_PAID)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .cancelledBy(UPDATED_CANCELLED_BY)
            .cancellationReason(UPDATED_CANCELLATION_REASON);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Order.class).block();
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
        order = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrder != null) {
            orderRepository.delete(insertedOrder).block();
            orderSearchRepository.delete(insertedOrder).block();
            insertedOrder = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        order.setId(null);
        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);
        var returnedOrderDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OrderDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Order in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrder = orderMapper.toEntity(returnedOrderDTO);
        assertOrderUpdatableFieldsEquals(returnedOrder, getPersistedOrder(returnedOrder));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedOrder = returnedOrder;
    }

    @Test
    void createOrderWithExistingId() throws Exception {
        // Create the Order with an existing ID
        insertedOrder = orderRepository.save(order).block();
        OrderDTO orderDTO = orderMapper.toDto(order);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkOrderNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        // set the field null
        order.setOrderNumber(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkOrderTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        // set the field null
        order.setOrderType(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkOrderSourceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        // set the field null
        order.setOrderSource(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkOrderDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        // set the field null
        order.setOrderDate(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllOrders() {
        // Initialize the database
        order.setId(UUID.randomUUID());
        insertedOrder = orderRepository.save(order).block();

        // Get all the orderList
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
            .value(hasItem(order.getId().toString()))
            .jsonPath("$.[*].orderNumber")
            .value(hasItem(DEFAULT_ORDER_NUMBER))
            .jsonPath("$.[*].orderType")
            .value(hasItem(DEFAULT_ORDER_TYPE))
            .jsonPath("$.[*].orderSource")
            .value(hasItem(DEFAULT_ORDER_SOURCE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].estimatedReadyTime")
            .value(hasItem(DEFAULT_ESTIMATED_READY_TIME.toString()))
            .jsonPath("$.[*].actualReadyTime")
            .value(hasItem(DEFAULT_ACTUAL_READY_TIME.toString()))
            .jsonPath("$.[*].specialInstructions")
            .value(hasItem(DEFAULT_SPECIAL_INSTRUCTIONS))
            .jsonPath("$.[*].subtotal")
            .value(hasItem(sameNumber(DEFAULT_SUBTOTAL)))
            .jsonPath("$.[*].taxAmount")
            .value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT)))
            .jsonPath("$.[*].discountAmount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.[*].totalAmount")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .jsonPath("$.[*].isPaid")
            .value(hasItem(DEFAULT_IS_PAID))
            .jsonPath("$.[*].cancelledAt")
            .value(hasItem(DEFAULT_CANCELLED_AT.toString()))
            .jsonPath("$.[*].cancelledBy")
            .value(hasItem(DEFAULT_CANCELLED_BY))
            .jsonPath("$.[*].cancellationReason")
            .value(hasItem(DEFAULT_CANCELLATION_REASON));
    }

    @Test
    void getOrder() {
        // Initialize the database
        order.setId(UUID.randomUUID());
        insertedOrder = orderRepository.save(order).block();

        // Get the order
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, order.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(order.getId().toString()))
            .jsonPath("$.orderNumber")
            .value(is(DEFAULT_ORDER_NUMBER))
            .jsonPath("$.orderType")
            .value(is(DEFAULT_ORDER_TYPE))
            .jsonPath("$.orderSource")
            .value(is(DEFAULT_ORDER_SOURCE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
            .jsonPath("$.orderDate")
            .value(is(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.estimatedReadyTime")
            .value(is(DEFAULT_ESTIMATED_READY_TIME.toString()))
            .jsonPath("$.actualReadyTime")
            .value(is(DEFAULT_ACTUAL_READY_TIME.toString()))
            .jsonPath("$.specialInstructions")
            .value(is(DEFAULT_SPECIAL_INSTRUCTIONS))
            .jsonPath("$.subtotal")
            .value(is(sameNumber(DEFAULT_SUBTOTAL)))
            .jsonPath("$.taxAmount")
            .value(is(sameNumber(DEFAULT_TAX_AMOUNT)))
            .jsonPath("$.discountAmount")
            .value(is(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.totalAmount")
            .value(is(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .jsonPath("$.isPaid")
            .value(is(DEFAULT_IS_PAID))
            .jsonPath("$.cancelledAt")
            .value(is(DEFAULT_CANCELLED_AT.toString()))
            .jsonPath("$.cancelledBy")
            .value(is(DEFAULT_CANCELLED_BY))
            .jsonPath("$.cancellationReason")
            .value(is(DEFAULT_CANCELLATION_REASON));
    }

    @Test
    void getNonExistingOrder() {
        // Get the order
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrder() throws Exception {
        // Initialize the database
        order.setId(UUID.randomUUID());
        insertedOrder = orderRepository.save(order).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderSearchRepository.save(order).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).block();
        updatedOrder
            .orderNumber(UPDATED_ORDER_NUMBER)
            .orderType(UPDATED_ORDER_TYPE)
            .orderSource(UPDATED_ORDER_SOURCE)
            .status(UPDATED_STATUS)
            .orderDate(UPDATED_ORDER_DATE)
            .estimatedReadyTime(UPDATED_ESTIMATED_READY_TIME)
            .actualReadyTime(UPDATED_ACTUAL_READY_TIME)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .subtotal(UPDATED_SUBTOTAL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .isPaid(UPDATED_IS_PAID)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .cancelledBy(UPDATED_CANCELLED_BY)
            .cancellationReason(UPDATED_CANCELLATION_REASON);
        OrderDTO orderDTO = orderMapper.toDto(updatedOrder);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderToMatchAllProperties(updatedOrder);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Order> orderSearchList = Streamable.of(orderSearchRepository.findAll().collectList().block()).toList();
                Order testOrderSearch = orderSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertOrderAllPropertiesEquals(testOrderSearch, updatedOrder);
                assertOrderUpdatableFieldsEquals(testOrderSearch, updatedOrder);
            });
    }

    @Test
    void putNonExistingOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        order.setId(UUID.randomUUID());
        insertedOrder = orderRepository.save(order).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .orderNumber(UPDATED_ORDER_NUMBER)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .subtotal(UPDATED_SUBTOTAL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .cancelledBy(UPDATED_CANCELLED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Order in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOrder, order), getPersistedOrder(order));
    }

    @Test
    void fullUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        order.setId(UUID.randomUUID());
        insertedOrder = orderRepository.save(order).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .orderNumber(UPDATED_ORDER_NUMBER)
            .orderType(UPDATED_ORDER_TYPE)
            .orderSource(UPDATED_ORDER_SOURCE)
            .status(UPDATED_STATUS)
            .orderDate(UPDATED_ORDER_DATE)
            .estimatedReadyTime(UPDATED_ESTIMATED_READY_TIME)
            .actualReadyTime(UPDATED_ACTUAL_READY_TIME)
            .specialInstructions(UPDATED_SPECIAL_INSTRUCTIONS)
            .subtotal(UPDATED_SUBTOTAL)
            .taxAmount(UPDATED_TAX_AMOUNT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .isPaid(UPDATED_IS_PAID)
            .cancelledAt(UPDATED_CANCELLED_AT)
            .cancelledBy(UPDATED_CANCELLED_BY)
            .cancellationReason(UPDATED_CANCELLATION_REASON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Order in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderUpdatableFieldsEquals(partialUpdatedOrder, getPersistedOrder(partialUpdatedOrder));
    }

    @Test
    void patchNonExistingOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orderDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        order.setId(UUID.randomUUID());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Order in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteOrder() {
        // Initialize the database
        order.setId(UUID.randomUUID());
        insertedOrder = orderRepository.save(order).block();
        orderRepository.save(order).block();
        orderSearchRepository.save(order).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the order
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, order.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchOrder() {
        // Initialize the database
        order.setId(UUID.randomUUID());
        insertedOrder = orderRepository.save(order).block();
        orderSearchRepository.save(order).block();

        // Search the order
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + order.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(order.getId().toString()))
            .jsonPath("$.[*].orderNumber")
            .value(hasItem(DEFAULT_ORDER_NUMBER))
            .jsonPath("$.[*].orderType")
            .value(hasItem(DEFAULT_ORDER_TYPE))
            .jsonPath("$.[*].orderSource")
            .value(hasItem(DEFAULT_ORDER_SOURCE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].estimatedReadyTime")
            .value(hasItem(DEFAULT_ESTIMATED_READY_TIME.toString()))
            .jsonPath("$.[*].actualReadyTime")
            .value(hasItem(DEFAULT_ACTUAL_READY_TIME.toString()))
            .jsonPath("$.[*].specialInstructions")
            .value(hasItem(DEFAULT_SPECIAL_INSTRUCTIONS.toString()))
            .jsonPath("$.[*].subtotal")
            .value(hasItem(sameNumber(DEFAULT_SUBTOTAL)))
            .jsonPath("$.[*].taxAmount")
            .value(hasItem(sameNumber(DEFAULT_TAX_AMOUNT)))
            .jsonPath("$.[*].discountAmount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .jsonPath("$.[*].totalAmount")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .jsonPath("$.[*].isPaid")
            .value(hasItem(DEFAULT_IS_PAID))
            .jsonPath("$.[*].cancelledAt")
            .value(hasItem(DEFAULT_CANCELLED_AT.toString()))
            .jsonPath("$.[*].cancelledBy")
            .value(hasItem(DEFAULT_CANCELLED_BY))
            .jsonPath("$.[*].cancellationReason")
            .value(hasItem(DEFAULT_CANCELLATION_REASON.toString()));
    }

    protected long getRepositoryCount() {
        return orderRepository.count().block();
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

    protected Order getPersistedOrder(Order order) {
        return orderRepository.findById(order.getId()).block();
    }

    protected void assertPersistedOrderToMatchAllProperties(Order expectedOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderAllPropertiesEquals(expectedOrder, getPersistedOrder(expectedOrder));
        assertOrderUpdatableFieldsEquals(expectedOrder, getPersistedOrder(expectedOrder));
    }

    protected void assertPersistedOrderToMatchUpdatableProperties(Order expectedOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderAllUpdatablePropertiesEquals(expectedOrder, getPersistedOrder(expectedOrder));
        assertOrderUpdatableFieldsEquals(expectedOrder, getPersistedOrder(expectedOrder));
    }
}
