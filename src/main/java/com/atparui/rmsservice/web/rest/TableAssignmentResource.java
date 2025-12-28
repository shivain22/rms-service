package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.TableAssignmentRepository;
import com.atparui.rmsservice.service.TableAssignmentService;
import com.atparui.rmsservice.service.dto.TableAssignmentDTO;
import com.atparui.rmsservice.service.dto.TableWaiterAssignmentDTO;
import com.atparui.rmsservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.atparui.rmsservice.domain.TableAssignment}.
 */
@RestController
@RequestMapping("/api/table-assignments")
public class TableAssignmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(TableAssignmentResource.class);

    private static final String ENTITY_NAME = "rmsserviceTableAssignment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TableAssignmentService tableAssignmentService;

    private final TableAssignmentRepository tableAssignmentRepository;

    public TableAssignmentResource(TableAssignmentService tableAssignmentService, TableAssignmentRepository tableAssignmentRepository) {
        this.tableAssignmentService = tableAssignmentService;
        this.tableAssignmentRepository = tableAssignmentRepository;
    }

    /**
     * {@code POST  /table-assignments} : Create a new tableAssignment.
     *
     * @param tableAssignmentDTO the tableAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tableAssignmentDTO, or with status {@code 400 (Bad Request)} if the tableAssignment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TableAssignmentDTO>> createTableAssignment(@Valid @RequestBody TableAssignmentDTO tableAssignmentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TableAssignment : {}", tableAssignmentDTO);
        if (tableAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new tableAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tableAssignmentDTO.setId(UUID.randomUUID());
        return tableAssignmentService
            .save(tableAssignmentDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/table-assignments/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /table-assignments/:id} : Updates an existing tableAssignment.
     *
     * @param id the id of the tableAssignmentDTO to save.
     * @param tableAssignmentDTO the tableAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tableAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the tableAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tableAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TableAssignmentDTO>> updateTableAssignment(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody TableAssignmentDTO tableAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TableAssignment : {}, {}", id, tableAssignmentDTO);
        if (tableAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tableAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tableAssignmentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tableAssignmentService
                    .update(tableAssignmentDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /table-assignments/:id} : Partial updates given fields of an existing tableAssignment, field will ignore if it is null
     *
     * @param id the id of the tableAssignmentDTO to save.
     * @param tableAssignmentDTO the tableAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tableAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the tableAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tableAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tableAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TableAssignmentDTO>> partialUpdateTableAssignment(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody TableAssignmentDTO tableAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TableAssignment partially : {}, {}", id, tableAssignmentDTO);
        if (tableAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tableAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tableAssignmentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TableAssignmentDTO> result = tableAssignmentService.partialUpdate(tableAssignmentDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /table-assignments} : get all the tableAssignments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tableAssignments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<TableAssignmentDTO>> getAllTableAssignments() {
        LOG.debug("REST request to get all TableAssignments");
        return tableAssignmentService.findAll().collectList();
    }

    /**
     * {@code GET  /table-assignments} : get all the tableAssignments as a stream.
     * @return the {@link Flux} of tableAssignments.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TableAssignmentDTO> getAllTableAssignmentsAsStream() {
        LOG.debug("REST request to get all TableAssignments as a stream");
        return tableAssignmentService.findAll();
    }

    /**
     * {@code GET  /table-assignments/:id} : get the "id" tableAssignment.
     *
     * @param id the id of the tableAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tableAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TableAssignmentDTO>> getTableAssignment(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get TableAssignment : {}", id);
        Mono<TableAssignmentDTO> tableAssignmentDTO = tableAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tableAssignmentDTO);
    }

    /**
     * {@code DELETE  /table-assignments/:id} : delete the "id" tableAssignment.
     *
     * @param id the id of the tableAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTableAssignment(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete TableAssignment : {}", id);
        return tableAssignmentService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    // jhipster-needle-rest-add-get-method - JHipster will add get methods here

    /**
     * {@code GET /api/table-assignments/date/{date}} : Get assignments for date
     *
     * @param date the assignment date
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and list of assignments
     */
    @GetMapping("/date/{date}")
    public Mono<ResponseEntity<List<TableAssignmentDTO>>> getAssignmentsForDate(
        @PathVariable @org.springframework.format.annotation.DateTimeFormat(
            iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE
        ) java.time.LocalDate date
    ) {
        LOG.debug("REST request to get assignments for date : {}", date);
        return tableAssignmentService.findByAssignmentDate(date).collectList().map(result -> ResponseEntity.ok().body(result));
    }

    // jhipster-needle-rest-add-post-method - JHipster will add post methods here

    /**
     * {@code POST /api/table-assignments/daily} : Create daily table assignments
     *
     * @param assignmentRequest the assignment request
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and list of assignments
     */
    @PostMapping("/daily")
    public Mono<ResponseEntity<List<TableAssignmentDTO>>> createDailyAssignments(
        @Valid @RequestBody com.atparui.rmsservice.service.dto.DailyTableAssignmentRequestDTO assignmentRequest
    ) {
        LOG.debug("REST request to create daily table assignments : {}", assignmentRequest);
        return tableAssignmentService
            .createDailyAssignments(assignmentRequest)
            .collectList()
            .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }

    /**
     * {@code POST /api/table-assignments/{assignmentId}/waiters} : Assign waiters to table
     *
     * @param assignmentId the table assignment ID
     * @param waiterIds list of waiter user IDs
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and list of waiter assignments
     */
    @PostMapping("/{assignmentId}/waiters")
    public Mono<ResponseEntity<List<TableWaiterAssignmentDTO>>> assignWaiters(
        @PathVariable UUID assignmentId,
        @RequestBody List<UUID> waiterIds
    ) {
        LOG.debug("REST request to assign waiters : {} - {}", assignmentId, waiterIds);
        return tableAssignmentService
            .assignWaiters(assignmentId, waiterIds)
            .collectList()
            .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }
}
