package com.atparui.rmsservice.web.rest;

import static com.atparui.rmsservice.domain.ShiftAsserts.*;
import static com.atparui.rmsservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.atparui.rmsservice.IntegrationTest;
import com.atparui.rmsservice.domain.Shift;
import com.atparui.rmsservice.repository.EntityManager;
import com.atparui.rmsservice.repository.ShiftRepository;
import com.atparui.rmsservice.service.dto.ShiftDTO;
import com.atparui.rmsservice.service.mapper.ShiftMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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
 * Integration tests for the {@link ShiftResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ShiftResourceIT {

    private static final String DEFAULT_SHIFT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHIFT_NAME = "BBBBBBBBBB";

    private static final LocalTime DEFAULT_START_TIME = LocalTime.NOON;
    private static final LocalTime UPDATED_START_TIME = LocalTime.MAX.withNano(0);

    private static final LocalTime DEFAULT_END_TIME = LocalTime.NOON;
    private static final LocalTime UPDATED_END_TIME = LocalTime.MAX.withNano(0);

    private static final LocalDate DEFAULT_SHIFT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SHIFT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/shifts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftMapper shiftMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Shift shift;

    private Shift insertedShift;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shift createEntity() {
        return new Shift()
            .id(UUID.randomUUID())
            .shiftName(DEFAULT_SHIFT_NAME)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .shiftDate(DEFAULT_SHIFT_DATE)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shift createUpdatedEntity() {
        return new Shift()
            .id(UUID.randomUUID())
            .shiftName(UPDATED_SHIFT_NAME)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .shiftDate(UPDATED_SHIFT_DATE)
            .isActive(UPDATED_IS_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Shift.class).block();
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
        shift = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedShift != null) {
            shiftRepository.delete(insertedShift).block();
            insertedShift = null;
        }
        deleteEntities(em);
    }

    @Test
    void createShift() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        shift.setId(null);
        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);
        var returnedShiftDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ShiftDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Shift in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShift = shiftMapper.toEntity(returnedShiftDTO);
        assertShiftUpdatableFieldsEquals(returnedShift, getPersistedShift(returnedShift));

        insertedShift = returnedShift;
    }

    @Test
    void createShiftWithExistingId() throws Exception {
        // Create the Shift with an existing ID
        insertedShift = shiftRepository.save(shift).block();
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shift in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkShiftNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shift.setShiftName(null);

        // Create the Shift, which fails.
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStartTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shift.setStartTime(null);

        // Create the Shift, which fails.
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEndTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shift.setEndTime(null);

        // Create the Shift, which fails.
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkShiftDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shift.setShiftDate(null);

        // Create the Shift, which fails.
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllShifts() {
        // Initialize the database
        shift.setId(UUID.randomUUID());
        insertedShift = shiftRepository.save(shift).block();

        // Get all the shiftList
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
            .value(hasItem(shift.getId().toString()))
            .jsonPath("$.[*].shiftName")
            .value(hasItem(DEFAULT_SHIFT_NAME))
            .jsonPath("$.[*].startTime")
            .value(hasItem(DEFAULT_START_TIME.toString()))
            .jsonPath("$.[*].endTime")
            .value(hasItem(DEFAULT_END_TIME.toString()))
            .jsonPath("$.[*].shiftDate")
            .value(hasItem(DEFAULT_SHIFT_DATE.toString()))
            .jsonPath("$.[*].isActive")
            .value(hasItem(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getShift() {
        // Initialize the database
        shift.setId(UUID.randomUUID());
        insertedShift = shiftRepository.save(shift).block();

        // Get the shift
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, shift.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(shift.getId().toString()))
            .jsonPath("$.shiftName")
            .value(is(DEFAULT_SHIFT_NAME))
            .jsonPath("$.startTime")
            .value(is(DEFAULT_START_TIME.toString()))
            .jsonPath("$.endTime")
            .value(is(DEFAULT_END_TIME.toString()))
            .jsonPath("$.shiftDate")
            .value(is(DEFAULT_SHIFT_DATE.toString()))
            .jsonPath("$.isActive")
            .value(is(DEFAULT_IS_ACTIVE));
    }

    @Test
    void getNonExistingShift() {
        // Get the shift
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingShift() throws Exception {
        // Initialize the database
        shift.setId(UUID.randomUUID());
        insertedShift = shiftRepository.save(shift).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shift
        Shift updatedShift = shiftRepository.findById(shift.getId()).block();
        updatedShift
            .shiftName(UPDATED_SHIFT_NAME)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .shiftDate(UPDATED_SHIFT_DATE)
            .isActive(UPDATED_IS_ACTIVE);
        ShiftDTO shiftDTO = shiftMapper.toDto(updatedShift);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shiftDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shift in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShiftToMatchAllProperties(updatedShift);
    }

    @Test
    void putNonExistingShift() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shift.setId(UUID.randomUUID());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shiftDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shift in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchShift() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shift.setId(UUID.randomUUID());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shift in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamShift() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shift.setId(UUID.randomUUID());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Shift in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateShiftWithPatch() throws Exception {
        // Initialize the database
        shift.setId(UUID.randomUUID());
        insertedShift = shiftRepository.save(shift).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shift using partial update
        Shift partialUpdatedShift = new Shift();
        partialUpdatedShift.setId(shift.getId());

        partialUpdatedShift
            .shiftName(UPDATED_SHIFT_NAME)
            .endTime(UPDATED_END_TIME)
            .shiftDate(UPDATED_SHIFT_DATE)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShift.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShift))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shift in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShiftUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedShift, shift), getPersistedShift(shift));
    }

    @Test
    void fullUpdateShiftWithPatch() throws Exception {
        // Initialize the database
        shift.setId(UUID.randomUUID());
        insertedShift = shiftRepository.save(shift).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shift using partial update
        Shift partialUpdatedShift = new Shift();
        partialUpdatedShift.setId(shift.getId());

        partialUpdatedShift
            .shiftName(UPDATED_SHIFT_NAME)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .shiftDate(UPDATED_SHIFT_DATE)
            .isActive(UPDATED_IS_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShift.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShift))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shift in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShiftUpdatableFieldsEquals(partialUpdatedShift, getPersistedShift(partialUpdatedShift));
    }

    @Test
    void patchNonExistingShift() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shift.setId(UUID.randomUUID());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, shiftDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shift in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchShift() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shift.setId(UUID.randomUUID());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shift in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamShift() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shift.setId(UUID.randomUUID());

        // Create the Shift
        ShiftDTO shiftDTO = shiftMapper.toDto(shift);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shiftDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Shift in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteShift() {
        // Initialize the database
        shift.setId(UUID.randomUUID());
        insertedShift = shiftRepository.save(shift).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shift
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, shift.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shiftRepository.count().block();
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

    protected Shift getPersistedShift(Shift shift) {
        return shiftRepository.findById(shift.getId()).block();
    }

    protected void assertPersistedShiftToMatchAllProperties(Shift expectedShift) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShiftAllPropertiesEquals(expectedShift, getPersistedShift(expectedShift));
        assertShiftUpdatableFieldsEquals(expectedShift, getPersistedShift(expectedShift));
    }

    protected void assertPersistedShiftToMatchUpdatableProperties(Shift expectedShift) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShiftAllUpdatablePropertiesEquals(expectedShift, getPersistedShift(expectedShift));
        assertShiftUpdatableFieldsEquals(expectedShift, getPersistedShift(expectedShift));
    }
}
