package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.RestaurantAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.Restaurant;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.RestaurantRepository;
import com.atparui.rmsservice.repository.search.RestaurantSearchRepository;
import com.atparui.rmsservice.service.dto.RestaurantDTO;
import com.atparui.rmsservice.service.mapper.RestaurantMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link RestaurantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RestaurantResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_URL = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_URL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TIMEZONE = "AAAAAAAAAA";
    private static final String UPDATED_TIMEZONE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/restaurants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/restaurants/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Autowired
    private RestaurantSearchRepository restaurantSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Restaurant restaurant;

    private Restaurant insertedRestaurant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurant createEntity() {
        return new Restaurant()
            .id(UUID.randomUUID())
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .description(DEFAULT_DESCRIPTION)
            .logoUrl(DEFAULT_LOGO_URL)
            .contactEmail(DEFAULT_CONTACT_EMAIL)
            .contactPhone(DEFAULT_CONTACT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .state(DEFAULT_STATE)
            .country(DEFAULT_COUNTRY)
            .postalCode(DEFAULT_POSTAL_CODE)
            .timezone(DEFAULT_TIMEZONE)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurant createUpdatedEntity() {
        return new Restaurant()
            .id(UUID.randomUUID())
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .logoUrl(UPDATED_LOGO_URL)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .timezone(UPDATED_TIMEZONE)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Restaurant.class).block();
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
        restaurant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRestaurant != null) {
            restaurantRepository.delete(insertedRestaurant).block();
            restaurantSearchRepository.delete(insertedRestaurant).block();
            insertedRestaurant = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRestaurant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        restaurant.setId(null);
        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);
        var returnedRestaurantDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RestaurantDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Restaurant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRestaurant = restaurantMapper.toEntity(returnedRestaurantDTO);
        assertRestaurantUpdatableFieldsEquals(returnedRestaurant, getPersistedRestaurant(returnedRestaurant));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedRestaurant = returnedRestaurant;
    }

    @Test
    void createRestaurantWithExistingId() throws Exception {
        // Create the Restaurant with an existing ID
        insertedRestaurant = restaurantRepository.save(restaurant).block();
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        // set the field null
        restaurant.setName(null);

        // Create the Restaurant, which fails.
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        // set the field null
        restaurant.setCode(null);

        // Create the Restaurant, which fails.
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllRestaurants() {
        // Initialize the database
        restaurant.setId(UUID.randomUUID());
        insertedRestaurant = restaurantRepository.save(restaurant).block();

        // Get all the restaurantList
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
            .value(hasItem(restaurant.getId().toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].logoUrl")
            .value(hasItem(DEFAULT_LOGO_URL))
            .jsonPath("$.[*].contactEmail")
            .value(hasItem(DEFAULT_CONTACT_EMAIL))
            .jsonPath("$.[*].contactPhone")
            .value(hasItem(DEFAULT_CONTACT_PHONE))
            .jsonPath("$.[*].addressLine1")
            .value(hasItem(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.[*].addressLine2")
            .value(hasItem(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].state")
            .value(hasItem(DEFAULT_STATE))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].postalCode")
            .value(hasItem(DEFAULT_POSTAL_CODE))
            .jsonPath("$.[*].timezone")
            .value(hasItem(DEFAULT_TIMEZONE))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getRestaurant() {
        // Initialize the database
        restaurant.setId(UUID.randomUUID());
        insertedRestaurant = restaurantRepository.save(restaurant).block();

        // Get the restaurant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, restaurant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(restaurant.getId().toString()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.logoUrl")
            .value(is(DEFAULT_LOGO_URL))
            .jsonPath("$.contactEmail")
            .value(is(DEFAULT_CONTACT_EMAIL))
            .jsonPath("$.contactPhone")
            .value(is(DEFAULT_CONTACT_PHONE))
            .jsonPath("$.addressLine1")
            .value(is(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.addressLine2")
            .value(is(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.state")
            .value(is(DEFAULT_STATE))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY))
            .jsonPath("$.postalCode")
            .value(is(DEFAULT_POSTAL_CODE))
            .jsonPath("$.timezone")
            .value(is(DEFAULT_TIMEZONE))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingRestaurant() {
        // Get the restaurant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRestaurant() throws Exception {
        // Initialize the database
        restaurant.setId(UUID.randomUUID());
        insertedRestaurant = restaurantRepository.save(restaurant).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurantSearchRepository.save(restaurant).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());

        // Update the restaurant
        Restaurant updatedRestaurant = restaurantRepository.findById(restaurant.getId()).block();
        updatedRestaurant
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .logoUrl(UPDATED_LOGO_URL)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .timezone(UPDATED_TIMEZONE)
            .isActive(UPDATED_IS_ACTIVE);
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(updatedRestaurant);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Restaurant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRestaurantToMatchAllProperties(updatedRestaurant);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Restaurant> restaurantSearchList = Streamable.of(restaurantSearchRepository.findAll().collectList().block()).toList();
                Restaurant testRestaurantSearch = restaurantSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertRestaurantAllPropertiesEquals(testRestaurantSearch, updatedRestaurant);
                assertRestaurantUpdatableFieldsEquals(testRestaurantSearch, updatedRestaurant);
            });
    }

    @Test
    void putNonExistingRestaurant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        restaurant.setId(UUID.randomUUID());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchRestaurant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        restaurant.setId(UUID.randomUUID());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamRestaurant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        restaurant.setId(UUID.randomUUID());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Restaurant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurant.setId(UUID.randomUUID());
        insertedRestaurant = restaurantRepository.save(restaurant).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurant using partial update
        Restaurant partialUpdatedRestaurant = new Restaurant();
        partialUpdatedRestaurant.setId(restaurant.getId());

        partialUpdatedRestaurant
            .code(UPDATED_CODE)
            .logoUrl(UPDATED_LOGO_URL)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .postalCode(UPDATED_POSTAL_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRestaurant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Restaurant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRestaurant, restaurant),
            getPersistedRestaurant(restaurant)
        );
    }

    @Test
    void fullUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurant.setId(UUID.randomUUID());
        insertedRestaurant = restaurantRepository.save(restaurant).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurant using partial update
        Restaurant partialUpdatedRestaurant = new Restaurant();
        partialUpdatedRestaurant.setId(restaurant.getId());

        partialUpdatedRestaurant
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .logoUrl(UPDATED_LOGO_URL)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .timezone(UPDATED_TIMEZONE)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRestaurant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Restaurant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurantUpdatableFieldsEquals(partialUpdatedRestaurant, getPersistedRestaurant(partialUpdatedRestaurant));
    }

    @Test
    void patchNonExistingRestaurant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        restaurant.setId(UUID.randomUUID());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, restaurantDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchRestaurant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        restaurant.setId(UUID.randomUUID());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamRestaurant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        restaurant.setId(UUID.randomUUID());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Restaurant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteRestaurant() {
        // Initialize the database
        restaurant.setId(UUID.randomUUID());
        insertedRestaurant = restaurantRepository.save(restaurant).block();
        restaurantRepository.save(restaurant).block();
        restaurantSearchRepository.save(restaurant).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the restaurant
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, restaurant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(restaurantSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchRestaurant() {
        // Initialize the database
        restaurant.setId(UUID.randomUUID());
        insertedRestaurant = restaurantRepository.save(restaurant).block();
        restaurantSearchRepository.save(restaurant).block();

        // Search the restaurant
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + restaurant.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(restaurant.getId().toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].logoUrl")
            .value(hasItem(DEFAULT_LOGO_URL))
            .jsonPath("$.[*].contactEmail")
            .value(hasItem(DEFAULT_CONTACT_EMAIL))
            .jsonPath("$.[*].contactPhone")
            .value(hasItem(DEFAULT_CONTACT_PHONE))
            .jsonPath("$.[*].addressLine1")
            .value(hasItem(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.[*].addressLine2")
            .value(hasItem(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].state")
            .value(hasItem(DEFAULT_STATE))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].postalCode")
            .value(hasItem(DEFAULT_POSTAL_CODE))
            .jsonPath("$.[*].timezone")
            .value(hasItem(DEFAULT_TIMEZONE))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    protected long getRepositoryCount() {
        return restaurantRepository.count().block();
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

    protected Restaurant getPersistedRestaurant(Restaurant restaurant) {
        return restaurantRepository.findById(restaurant.getId()).block();
    }

    protected void assertPersistedRestaurantToMatchAllProperties(Restaurant expectedRestaurant) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRestaurantAllPropertiesEquals(expectedRestaurant, getPersistedRestaurant(expectedRestaurant));
        assertRestaurantUpdatableFieldsEquals(expectedRestaurant, getPersistedRestaurant(expectedRestaurant));
    }

    protected void assertPersistedRestaurantToMatchUpdatableProperties(Restaurant expectedRestaurant) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRestaurantAllUpdatablePropertiesEquals(expectedRestaurant, getPersistedRestaurant(expectedRestaurant));
        assertRestaurantUpdatableFieldsEquals(expectedRestaurant, getPersistedRestaurant(expectedRestaurant));
    }
}
