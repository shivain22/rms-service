package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.BranchAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.atparui.rmsservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.Branch;
import com.atparui.rmsservice.repository.BranchRepository;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.search.BranchSearchRepository;
import com.atparui.rmsservice.service.dto.BranchDTO;
import com.atparui.rmsservice.service.mapper.BranchMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalTime;
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
 * Integration tests for the {@link BranchResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BranchResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

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

    private static final BigDecimal DEFAULT_LATITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LATITUDE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_LONGITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LONGITUDE = new BigDecimal(2);

    private static final LocalTime DEFAULT_OPENING_TIME = LocalTime.NOON;
    private static final LocalTime UPDATED_OPENING_TIME = LocalTime.MAX.withNano(0);

    private static final LocalTime DEFAULT_CLOSING_TIME = LocalTime.NOON;
    private static final LocalTime UPDATED_CLOSING_TIME = LocalTime.MAX.withNano(0);

    private static final String DEFAULT_TIMEZONE = "AAAAAAAAAA";
    private static final String UPDATED_TIMEZONE = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAX_CAPACITY = 1;
    private static final Integer UPDATED_MAX_CAPACITY = 2;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/branches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/branches/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchMapper branchMapper;

    @Autowired
    private BranchSearchRepository branchSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Branch branch;

    private Branch insertedBranch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Branch createEntity() {
        return new Branch()
            .id(UUID.randomUUID())
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .description(DEFAULT_DESCRIPTION)
            .contactEmail(DEFAULT_CONTACT_EMAIL)
            .contactPhone(DEFAULT_CONTACT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .state(DEFAULT_STATE)
            .country(DEFAULT_COUNTRY)
            .postalCode(DEFAULT_POSTAL_CODE)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .openingTime(DEFAULT_OPENING_TIME)
            .closingTime(DEFAULT_CLOSING_TIME)
            .timezone(DEFAULT_TIMEZONE)
            .maxCapacity(DEFAULT_MAX_CAPACITY)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Branch createUpdatedEntity() {
        return new Branch()
            .id(UUID.randomUUID())
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .openingTime(UPDATED_OPENING_TIME)
            .closingTime(UPDATED_CLOSING_TIME)
            .timezone(UPDATED_TIMEZONE)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Branch.class).block();
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
        branch = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBranch != null) {
            branchRepository.delete(insertedBranch).block();
            branchSearchRepository.delete(insertedBranch).block();
            insertedBranch = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBranch() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        branch.setId(null);
        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);
        var returnedBranchDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BranchDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Branch in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBranch = branchMapper.toEntity(returnedBranchDTO);
        assertBranchUpdatableFieldsEquals(returnedBranch, getPersistedBranch(returnedBranch));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedBranch = returnedBranch;
    }

    @Test
    void createBranchWithExistingId() throws Exception {
        // Create the Branch with an existing ID
        insertedBranch = branchRepository.save(branch).block();
        BranchDTO branchDTO = branchMapper.toDto(branch);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        // set the field null
        branch.setName(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        // set the field null
        branch.setCode(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllBranches() {
        // Initialize the database
        branch.setId(UUID.randomUUID());
        insertedBranch = branchRepository.save(branch).block();

        // Get all the branchList
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
            .value(hasItem(branch.getId().toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
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
            .jsonPath("$.[*].latitude")
            .value(hasItem(sameNumber(DEFAULT_LATITUDE)))
            .jsonPath("$.[*].longitude")
            .value(hasItem(sameNumber(DEFAULT_LONGITUDE)))
            .jsonPath("$.[*].openingTime")
            .value(hasItem(DEFAULT_OPENING_TIME.toString()))
            .jsonPath("$.[*].closingTime")
            .value(hasItem(DEFAULT_CLOSING_TIME.toString()))
            .jsonPath("$.[*].timezone")
            .value(hasItem(DEFAULT_TIMEZONE))
            .jsonPath("$.[*].maxCapacity")
            .value(hasItem(DEFAULT_MAX_CAPACITY))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getBranch() {
        // Initialize the database
        branch.setId(UUID.randomUUID());
        insertedBranch = branchRepository.save(branch).block();

        // Get the branch
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, branch.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(branch.getId().toString()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
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
            .jsonPath("$.latitude")
            .value(is(sameNumber(DEFAULT_LATITUDE)))
            .jsonPath("$.longitude")
            .value(is(sameNumber(DEFAULT_LONGITUDE)))
            .jsonPath("$.openingTime")
            .value(is(DEFAULT_OPENING_TIME.toString()))
            .jsonPath("$.closingTime")
            .value(is(DEFAULT_CLOSING_TIME.toString()))
            .jsonPath("$.timezone")
            .value(is(DEFAULT_TIMEZONE))
            .jsonPath("$.maxCapacity")
            .value(is(DEFAULT_MAX_CAPACITY))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingBranch() {
        // Get the branch
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBranch() throws Exception {
        // Initialize the database
        branch.setId(UUID.randomUUID());
        insertedBranch = branchRepository.save(branch).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        branchSearchRepository.save(branch).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());

        // Update the branch
        Branch updatedBranch = branchRepository.findById(branch.getId()).block();
        updatedBranch
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .openingTime(UPDATED_OPENING_TIME)
            .closingTime(UPDATED_CLOSING_TIME)
            .timezone(UPDATED_TIMEZONE)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .isActive(UPDATED_IS_ACTIVE);
        BranchDTO branchDTO = branchMapper.toDto(updatedBranch);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, branchDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBranchToMatchAllProperties(updatedBranch);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Branch> branchSearchList = Streamable.of(branchSearchRepository.findAll().collectList().block()).toList();
                Branch testBranchSearch = branchSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertBranchAllPropertiesEquals(testBranchSearch, updatedBranch);
                assertBranchUpdatableFieldsEquals(testBranchSearch, updatedBranch);
            });
    }

    @Test
    void putNonExistingBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        branch.setId(UUID.randomUUID());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, branchDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        branch.setId(UUID.randomUUID());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        branch.setId(UUID.randomUUID());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateBranchWithPatch() throws Exception {
        // Initialize the database
        branch.setId(UUID.randomUUID());
        insertedBranch = branchRepository.save(branch).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branch using partial update
        Branch partialUpdatedBranch = new Branch();
        partialUpdatedBranch.setId(branch.getId());

        partialUpdatedBranch
            .code(UPDATED_CODE)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .longitude(UPDATED_LONGITUDE)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBranch.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBranch))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Branch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBranchUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBranch, branch), getPersistedBranch(branch));
    }

    @Test
    void fullUpdateBranchWithPatch() throws Exception {
        // Initialize the database
        branch.setId(UUID.randomUUID());
        insertedBranch = branchRepository.save(branch).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branch using partial update
        Branch partialUpdatedBranch = new Branch();
        partialUpdatedBranch.setId(branch.getId());

        partialUpdatedBranch
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactPhone(UPDATED_CONTACT_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .openingTime(UPDATED_OPENING_TIME)
            .closingTime(UPDATED_CLOSING_TIME)
            .timezone(UPDATED_TIMEZONE)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBranch.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBranch))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Branch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBranchUpdatableFieldsEquals(partialUpdatedBranch, getPersistedBranch(partialUpdatedBranch));
    }

    @Test
    void patchNonExistingBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        branch.setId(UUID.randomUUID());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, branchDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        branch.setId(UUID.randomUUID());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        branch.setId(UUID.randomUUID());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(branchDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteBranch() {
        // Initialize the database
        branch.setId(UUID.randomUUID());
        insertedBranch = branchRepository.save(branch).block();
        branchRepository.save(branch).block();
        branchSearchRepository.save(branch).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the branch
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, branch.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(branchSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchBranch() {
        // Initialize the database
        branch.setId(UUID.randomUUID());
        insertedBranch = branchRepository.save(branch).block();
        branchSearchRepository.save(branch).block();

        // Search the branch
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + branch.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(branch.getId().toString()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
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
            .jsonPath("$.[*].latitude")
            .value(hasItem(sameNumber(DEFAULT_LATITUDE)))
            .jsonPath("$.[*].longitude")
            .value(hasItem(sameNumber(DEFAULT_LONGITUDE)))
            .jsonPath("$.[*].openingTime")
            .value(hasItem(DEFAULT_OPENING_TIME.toString()))
            .jsonPath("$.[*].closingTime")
            .value(hasItem(DEFAULT_CLOSING_TIME.toString()))
            .jsonPath("$.[*].timezone")
            .value(hasItem(DEFAULT_TIMEZONE))
            .jsonPath("$.[*].maxCapacity")
            .value(hasItem(DEFAULT_MAX_CAPACITY))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    protected long getRepositoryCount() {
        return branchRepository.count().block();
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

    protected Branch getPersistedBranch(Branch branch) {
        return branchRepository.findById(branch.getId()).block();
    }

    protected void assertPersistedBranchToMatchAllProperties(Branch expectedBranch) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBranchAllPropertiesEquals(expectedBranch, getPersistedBranch(expectedBranch));
        assertBranchUpdatableFieldsEquals(expectedBranch, getPersistedBranch(expectedBranch));
    }

    protected void assertPersistedBranchToMatchUpdatableProperties(Branch expectedBranch) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBranchAllUpdatablePropertiesEquals(expectedBranch, getPersistedBranch(expectedBranch));
        assertBranchUpdatableFieldsEquals(expectedBranch, getPersistedBranch(expectedBranch));
    }
}
