package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.OrderItemCustomizationAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.OrderItemCustomization;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.OrderItemCustomizationRepository;
import com.atparui.rmsservice.service.dto.OrderItemCustomizationDTO;
import com.atparui.rmsservice.service.mapper.OrderItemCustomizationMapper;
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
 * Integration tests for the {@link OrderItemCustomizationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrderItemCustomizationResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/order-item-customizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderItemCustomizationRepository orderItemCustomizationRepository;

    @Autowired
    private OrderItemCustomizationMapper orderItemCustomizationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OrderItemCustomization orderItemCustomization;

    private OrderItemCustomization insertedOrderItemCustomization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItemCustomization createEntity() {
        return new OrderItemCustomization()
            .id(UUID.randomUUID())
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .totalPrice(DEFAULT_TOTAL_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItemCustomization createUpdatedEntity() {
        return new OrderItemCustomization()
            .id(UUID.randomUUID())
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .totalPrice(UPDATED_TOTAL_PRICE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OrderItemCustomization.class).block();
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
        orderItemCustomization = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrderItemCustomization != null) {
            orderItemCustomizationRepository.delete(insertedOrderItemCustomization).block();
            insertedOrderItemCustomization = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOrderItemCustomization() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        orderItemCustomization.setId(null);
        // Create the OrderItemCustomization
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);
        var returnedOrderItemCustomizationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OrderItemCustomizationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the OrderItemCustomization in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrderItemCustomization = orderItemCustomizationMapper.toEntity(returnedOrderItemCustomizationDTO);
        assertOrderItemCustomizationUpdatableFieldsEquals(
            returnedOrderItemCustomization,
            getPersistedOrderItemCustomization(returnedOrderItemCustomization)
        );

        insertedOrderItemCustomization = returnedOrderItemCustomization;
    }

    @Test
    void createOrderItemCustomizationWithExistingId() throws Exception {
        // Create the OrderItemCustomization with an existing ID
        insertedOrderItemCustomization = orderItemCustomizationRepository.save(orderItemCustomization).block();
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItemCustomization in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderItemCustomization.setUnitPrice(null);

        // Create the OrderItemCustomization, which fails.
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTotalPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderItemCustomization.setTotalPrice(null);

        // Create the OrderItemCustomization, which fails.
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrderItemCustomizationsAsStream() {
        // Initialize the database
        orderItemCustomization.setId(UUID.randomUUID());
        orderItemCustomizationRepository.save(orderItemCustomization).block();

        List<OrderItemCustomization> orderItemCustomizationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrderItemCustomizationDTO.class)
            .getResponseBody()
            .map(orderItemCustomizationMapper::toEntity)
            .filter(orderItemCustomization::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(orderItemCustomizationList).isNotNull();
        assertThat(orderItemCustomizationList).hasSize(1);
        OrderItemCustomization testOrderItemCustomization = orderItemCustomizationList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertOrderItemCustomizationAllPropertiesEquals(orderItemCustomization, testOrderItemCustomization);
        assertOrderItemCustomizationUpdatableFieldsEquals(orderItemCustomization, testOrderItemCustomization);
    }

    @Test
    void getAllOrderItemCustomizations() {
        // Initialize the database
        orderItemCustomization.setId(UUID.randomUUID());
        insertedOrderItemCustomization = orderItemCustomizationRepository.save(orderItemCustomization).block();

        // Get all the orderItemCustomizationList
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
            .value(hasItem(orderItemCustomization.getId().toString()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].unitPrice")
            .value(hasItem(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    @Test
    void getOrderItemCustomization() {
        // Initialize the database
        orderItemCustomization.setId(UUID.randomUUID());
        insertedOrderItemCustomization = orderItemCustomizationRepository.save(orderItemCustomization).block();

        // Get the orderItemCustomization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orderItemCustomization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orderItemCustomization.getId().toString()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.unitPrice")
            .value(is(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.totalPrice")
            .value(is(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    @Test
    void getNonExistingOrderItemCustomization() {
        // Get the orderItemCustomization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrderItemCustomization() throws Exception {
        // Initialize the database
        orderItemCustomization.setId(UUID.randomUUID());
        insertedOrderItemCustomization = orderItemCustomizationRepository.save(orderItemCustomization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItemCustomization
        OrderItemCustomization updatedOrderItemCustomization = orderItemCustomizationRepository
            .findById(orderItemCustomization.getId())
            .block();
        updatedOrderItemCustomization.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).totalPrice(UPDATED_TOTAL_PRICE);
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(updatedOrderItemCustomization);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderItemCustomizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItemCustomization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderItemCustomizationToMatchAllProperties(updatedOrderItemCustomization);
    }

    @Test
    void putNonExistingOrderItemCustomization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemCustomization.setId(UUID.randomUUID());

        // Create the OrderItemCustomization
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderItemCustomizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItemCustomization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrderItemCustomization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemCustomization.setId(UUID.randomUUID());

        // Create the OrderItemCustomization
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItemCustomization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrderItemCustomization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemCustomization.setId(UUID.randomUUID());

        // Create the OrderItemCustomization
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderItemCustomization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrderItemCustomizationWithPatch() throws Exception {
        // Initialize the database
        orderItemCustomization.setId(UUID.randomUUID());
        insertedOrderItemCustomization = orderItemCustomizationRepository.save(orderItemCustomization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItemCustomization using partial update
        OrderItemCustomization partialUpdatedOrderItemCustomization = new OrderItemCustomization();
        partialUpdatedOrderItemCustomization.setId(orderItemCustomization.getId());

        partialUpdatedOrderItemCustomization.unitPrice(UPDATED_UNIT_PRICE).totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderItemCustomization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrderItemCustomization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItemCustomization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemCustomizationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderItemCustomization, orderItemCustomization),
            getPersistedOrderItemCustomization(orderItemCustomization)
        );
    }

    @Test
    void fullUpdateOrderItemCustomizationWithPatch() throws Exception {
        // Initialize the database
        orderItemCustomization.setId(UUID.randomUUID());
        insertedOrderItemCustomization = orderItemCustomizationRepository.save(orderItemCustomization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItemCustomization using partial update
        OrderItemCustomization partialUpdatedOrderItemCustomization = new OrderItemCustomization();
        partialUpdatedOrderItemCustomization.setId(orderItemCustomization.getId());

        partialUpdatedOrderItemCustomization.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderItemCustomization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrderItemCustomization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItemCustomization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemCustomizationUpdatableFieldsEquals(
            partialUpdatedOrderItemCustomization,
            getPersistedOrderItemCustomization(partialUpdatedOrderItemCustomization)
        );
    }

    @Test
    void patchNonExistingOrderItemCustomization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemCustomization.setId(UUID.randomUUID());

        // Create the OrderItemCustomization
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orderItemCustomizationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItemCustomization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrderItemCustomization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemCustomization.setId(UUID.randomUUID());

        // Create the OrderItemCustomization
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItemCustomization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrderItemCustomization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItemCustomization.setId(UUID.randomUUID());

        // Create the OrderItemCustomization
        OrderItemCustomizationDTO orderItemCustomizationDTO = orderItemCustomizationMapper.toDto(orderItemCustomization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderItemCustomizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderItemCustomization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrderItemCustomization() {
        // Initialize the database
        orderItemCustomization.setId(UUID.randomUUID());
        insertedOrderItemCustomization = orderItemCustomizationRepository.save(orderItemCustomization).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderItemCustomization
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orderItemCustomization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderItemCustomizationRepository.count().block();
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

    protected OrderItemCustomization getPersistedOrderItemCustomization(OrderItemCustomization orderItemCustomization) {
        return orderItemCustomizationRepository.findById(orderItemCustomization.getId()).block();
    }

    protected void assertPersistedOrderItemCustomizationToMatchAllProperties(OrderItemCustomization expectedOrderItemCustomization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderItemCustomizationAllPropertiesEquals(expectedOrderItemCustomization, getPersistedOrderItemCustomization(expectedOrderItemCustomization));
        assertOrderItemCustomizationUpdatableFieldsEquals(
            expectedOrderItemCustomization,
            getPersistedOrderItemCustomization(expectedOrderItemCustomization)
        );
    }

    protected void assertPersistedOrderItemCustomizationToMatchUpdatableProperties(OrderItemCustomization expectedOrderItemCustomization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderItemCustomizationAllUpdatablePropertiesEquals(expectedOrderItemCustomization, getPersistedOrderItemCustomization(expectedOrderItemCustomization));
        assertOrderItemCustomizationUpdatableFieldsEquals(
            expectedOrderItemCustomization,
            getPersistedOrderItemCustomization(expectedOrderItemCustomization)
        );
    }
}
