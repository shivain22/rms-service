package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.OrderStatusHistoryAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.OrderStatusHistory;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.OrderStatusHistoryRepository;
import com.atparui.rmsservice.service.dto.OrderStatusHistoryDTO;
import com.atparui.rmsservice.service.mapper.OrderStatusHistoryMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link OrderStatusHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrderStatusHistoryResourceIT {

    private static final String DEFAULT_PREVIOUS_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_PREVIOUS_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_NEW_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_NEW_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CHANGED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHANGED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CHANGED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CHANGED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/order-status-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private OrderStatusHistoryMapper orderStatusHistoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OrderStatusHistory orderStatusHistory;

    private OrderStatusHistory insertedOrderStatusHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderStatusHistory createEntity() {
        return new OrderStatusHistory()
            .id(UUID.randomUUID())
            .previousStatus(DEFAULT_PREVIOUS_STATUS)
            .newStatus(DEFAULT_NEW_STATUS)
            .changedAt(DEFAULT_CHANGED_AT)
            .changedBy(DEFAULT_CHANGED_BY)
            .notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderStatusHistory createUpdatedEntity() {
        return new OrderStatusHistory()
            .id(UUID.randomUUID())
            .previousStatus(UPDATED_PREVIOUS_STATUS)
            .newStatus(UPDATED_NEW_STATUS)
            .changedAt(UPDATED_CHANGED_AT)
            .changedBy(UPDATED_CHANGED_BY)
            .notes(UPDATED_NOTES);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OrderStatusHistory.class).block();
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
        orderStatusHistory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrderStatusHistory != null) {
            orderStatusHistoryRepository.delete(insertedOrderStatusHistory).block();
            insertedOrderStatusHistory = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOrderStatusHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        orderStatusHistory.setId(null);
        // Create the OrderStatusHistory
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);
        var returnedOrderStatusHistoryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OrderStatusHistoryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the OrderStatusHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrderStatusHistory = orderStatusHistoryMapper.toEntity(returnedOrderStatusHistoryDTO);
        assertOrderStatusHistoryUpdatableFieldsEquals(
            returnedOrderStatusHistory,
            getPersistedOrderStatusHistory(returnedOrderStatusHistory)
        );

        insertedOrderStatusHistory = returnedOrderStatusHistory;
    }

    @Test
    void createOrderStatusHistoryWithExistingId() throws Exception {
        // Create the OrderStatusHistory with an existing ID
        insertedOrderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory).block();
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNewStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderStatusHistory.setNewStatus(null);

        // Create the OrderStatusHistory, which fails.
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkChangedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderStatusHistory.setChangedAt(null);

        // Create the OrderStatusHistory, which fails.
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrderStatusHistoriesAsStream() {
        // Initialize the database
        orderStatusHistory.setId(UUID.randomUUID());
        orderStatusHistoryRepository.save(orderStatusHistory).block();

        List<OrderStatusHistory> orderStatusHistoryList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrderStatusHistoryDTO.class)
            .getResponseBody()
            .map(orderStatusHistoryMapper::toEntity)
            .filter(orderStatusHistory::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(orderStatusHistoryList).isNotNull();
        assertThat(orderStatusHistoryList).hasSize(1);
        OrderStatusHistory testOrderStatusHistory = orderStatusHistoryList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertOrderStatusHistoryAllPropertiesEquals(orderStatusHistory, testOrderStatusHistory);
        assertOrderStatusHistoryUpdatableFieldsEquals(orderStatusHistory, testOrderStatusHistory);
    }

    @Test
    void getAllOrderStatusHistories() {
        // Initialize the database
        orderStatusHistory.setId(UUID.randomUUID());
        insertedOrderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory).block();

        // Get all the orderStatusHistoryList
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
            .value(hasItem(orderStatusHistory.getId().toString()))
            .jsonPath("$.[*].previousStatus")
            .value(hasItem(DEFAULT_PREVIOUS_STATUS))
            .jsonPath("$.[*].newStatus")
            .value(hasItem(DEFAULT_NEW_STATUS))
            .jsonPath("$.[*].changedAt")
            .value(hasItem(DEFAULT_CHANGED_AT.toString()))
            .jsonPath("$.[*].changedBy")
            .value(hasItem(DEFAULT_CHANGED_BY))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES));
    }

    @Test
    void getOrderStatusHistory() {
        // Initialize the database
        orderStatusHistory.setId(UUID.randomUUID());
        insertedOrderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory).block();

        // Get the orderStatusHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orderStatusHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orderStatusHistory.getId().toString()))
            .jsonPath("$.previousStatus")
            .value(is(DEFAULT_PREVIOUS_STATUS))
            .jsonPath("$.newStatus")
            .value(is(DEFAULT_NEW_STATUS))
            .jsonPath("$.changedAt")
            .value(is(DEFAULT_CHANGED_AT.toString()))
            .jsonPath("$.changedBy")
            .value(is(DEFAULT_CHANGED_BY))
            .jsonPath("$.notes")
            .value(is(DEFAULT_NOTES));
    }

    @Test
    void getNonExistingOrderStatusHistory() {
        // Get the orderStatusHistory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrderStatusHistory() throws Exception {
        // Initialize the database
        orderStatusHistory.setId(UUID.randomUUID());
        insertedOrderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderStatusHistory
        OrderStatusHistory updatedOrderStatusHistory = orderStatusHistoryRepository.findById(orderStatusHistory.getId()).block();
        updatedOrderStatusHistory
            .previousStatus(UPDATED_PREVIOUS_STATUS)
            .newStatus(UPDATED_NEW_STATUS)
            .changedAt(UPDATED_CHANGED_AT)
            .changedBy(UPDATED_CHANGED_BY)
            .notes(UPDATED_NOTES);
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(updatedOrderStatusHistory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderStatusHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderStatusHistoryToMatchAllProperties(updatedOrderStatusHistory);
    }

    @Test
    void putNonExistingOrderStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatusHistory.setId(UUID.randomUUID());

        // Create the OrderStatusHistory
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderStatusHistoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrderStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatusHistory.setId(UUID.randomUUID());

        // Create the OrderStatusHistory
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrderStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatusHistory.setId(UUID.randomUUID());

        // Create the OrderStatusHistory
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrderStatusHistoryWithPatch() throws Exception {
        // Initialize the database
        orderStatusHistory.setId(UUID.randomUUID());
        insertedOrderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderStatusHistory using partial update
        OrderStatusHistory partialUpdatedOrderStatusHistory = new OrderStatusHistory();
        partialUpdatedOrderStatusHistory.setId(orderStatusHistory.getId());

        partialUpdatedOrderStatusHistory
            .newStatus(UPDATED_NEW_STATUS)
            .changedAt(UPDATED_CHANGED_AT)
            .changedBy(UPDATED_CHANGED_BY)
            .notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderStatusHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrderStatusHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderStatusHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderStatusHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderStatusHistory, orderStatusHistory),
            getPersistedOrderStatusHistory(orderStatusHistory)
        );
    }

    @Test
    void fullUpdateOrderStatusHistoryWithPatch() throws Exception {
        // Initialize the database
        orderStatusHistory.setId(UUID.randomUUID());
        insertedOrderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderStatusHistory using partial update
        OrderStatusHistory partialUpdatedOrderStatusHistory = new OrderStatusHistory();
        partialUpdatedOrderStatusHistory.setId(orderStatusHistory.getId());

        partialUpdatedOrderStatusHistory
            .previousStatus(UPDATED_PREVIOUS_STATUS)
            .newStatus(UPDATED_NEW_STATUS)
            .changedAt(UPDATED_CHANGED_AT)
            .changedBy(UPDATED_CHANGED_BY)
            .notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderStatusHistory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrderStatusHistory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderStatusHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderStatusHistoryUpdatableFieldsEquals(
            partialUpdatedOrderStatusHistory,
            getPersistedOrderStatusHistory(partialUpdatedOrderStatusHistory)
        );
    }

    @Test
    void patchNonExistingOrderStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatusHistory.setId(UUID.randomUUID());

        // Create the OrderStatusHistory
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orderStatusHistoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrderStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatusHistory.setId(UUID.randomUUID());

        // Create the OrderStatusHistory
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrderStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderStatusHistory.setId(UUID.randomUUID());

        // Create the OrderStatusHistory
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderStatusHistoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrderStatusHistory() {
        // Initialize the database
        orderStatusHistory.setId(UUID.randomUUID());
        insertedOrderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderStatusHistory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orderStatusHistory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderStatusHistoryRepository.count().block();
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

    protected OrderStatusHistory getPersistedOrderStatusHistory(OrderStatusHistory orderStatusHistory) {
        return orderStatusHistoryRepository.findById(orderStatusHistory.getId()).block();
    }

    protected void assertPersistedOrderStatusHistoryToMatchAllProperties(OrderStatusHistory expectedOrderStatusHistory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderStatusHistoryAllPropertiesEquals(expectedOrderStatusHistory, getPersistedOrderStatusHistory(expectedOrderStatusHistory));
        assertOrderStatusHistoryUpdatableFieldsEquals(
            expectedOrderStatusHistory,
            getPersistedOrderStatusHistory(expectedOrderStatusHistory)
        );
    }

    protected void assertPersistedOrderStatusHistoryToMatchUpdatableProperties(OrderStatusHistory expectedOrderStatusHistory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderStatusHistoryAllUpdatablePropertiesEquals(expectedOrderStatusHistory, getPersistedOrderStatusHistory(expectedOrderStatusHistory));
        assertOrderStatusHistoryUpdatableFieldsEquals(
            expectedOrderStatusHistory,
            getPersistedOrderStatusHistory(expectedOrderStatusHistory)
        );
    }
}
