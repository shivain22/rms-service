package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.TableWaiterAssignmentRepository;
import com.atparui.rmsservice.service.TableWaiterAssignmentService;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.TableWaiterAssignment}.
 */
@RestController
@RequestMapping("/api/table-waiter-assignments")
public class TableWaiterAssignmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(TableWaiterAssignmentResource.class);

    private static final String ENTITY_NAME = "rmsserviceTableWaiterAssignment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TableWaiterAssignmentService tableWaiterAssignmentService;

    private final TableWaiterAssignmentRepository tableWaiterAssignmentRepository;

    public TableWaiterAssignmentResource(
        TableWaiterAssignmentService tableWaiterAssignmentService,
        TableWaiterAssignmentRepository tableWaiterAssignmentRepository
    ) {
        this.tableWaiterAssignmentService = tableWaiterAssignmentService;
        this.tableWaiterAssignmentRepository = tableWaiterAssignmentRepository;
    }

    /**
     * {@code POST  /table-waiter-assignments} : Create a new tableWaiterAssignment.
     *
     * @param tableWaiterAssignmentDTO the tableWaiterAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tableWaiterAssignmentDTO, or with status {@code 400 (Bad Request)} if the tableWaiterAssignment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TableWaiterAssignmentDTO>> createTableWaiterAssignment(
        @Valid @RequestBody TableWaiterAssignmentDTO tableWaiterAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save TableWaiterAssignment : {}", tableWaiterAssignmentDTO);
        if (tableWaiterAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new tableWaiterAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tableWaiterAssignmentDTO.setId(UUID.randomUUID());
        return tableWaiterAssignmentService
            .save(tableWaiterAssignmentDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/table-waiter-assignments/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /table-waiter-assignments/:id} : Updates an existing tableWaiterAssignment.
     *
     * @param id the id of the tableWaiterAssignmentDTO to save.
     * @param tableWaiterAssignmentDTO the tableWaiterAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tableWaiterAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the tableWaiterAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tableWaiterAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TableWaiterAssignmentDTO>> updateTableWaiterAssignment(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody TableWaiterAssignmentDTO tableWaiterAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TableWaiterAssignment : {}, {}", id, tableWaiterAssignmentDTO);
        if (tableWaiterAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tableWaiterAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tableWaiterAssignmentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tableWaiterAssignmentService
                    .update(tableWaiterAssignmentDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /table-waiter-assignments/:id} : Partial updates given fields of an existing tableWaiterAssignment, field will ignore if it is null
     *
     * @param id the id of the tableWaiterAssignmentDTO to save.
     * @param tableWaiterAssignmentDTO the tableWaiterAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tableWaiterAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the tableWaiterAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tableWaiterAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tableWaiterAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TableWaiterAssignmentDTO>> partialUpdateTableWaiterAssignment(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody TableWaiterAssignmentDTO tableWaiterAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TableWaiterAssignment partially : {}, {}", id, tableWaiterAssignmentDTO);
        if (tableWaiterAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tableWaiterAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tableWaiterAssignmentRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TableWaiterAssignmentDTO> result = tableWaiterAssignmentService.partialUpdate(tableWaiterAssignmentDTO);

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
     * {@code GET  /table-waiter-assignments} : get all the tableWaiterAssignments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tableWaiterAssignments in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<TableWaiterAssignmentDTO>> getAllTableWaiterAssignments() {
        LOG.debug("REST request to get all TableWaiterAssignments");
        return tableWaiterAssignmentService.findAll().collectList();
    }

    /**
     * {@code GET  /table-waiter-assignments} : get all the tableWaiterAssignments as a stream.
     * @return the {@link Flux} of tableWaiterAssignments.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TableWaiterAssignmentDTO> getAllTableWaiterAssignmentsAsStream() {
        LOG.debug("REST request to get all TableWaiterAssignments as a stream");
        return tableWaiterAssignmentService.findAll();
    }

    /**
     * {@code GET  /table-waiter-assignments/:id} : get the "id" tableWaiterAssignment.
     *
     * @param id the id of the tableWaiterAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tableWaiterAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TableWaiterAssignmentDTO>> getTableWaiterAssignment(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get TableWaiterAssignment : {}", id);
        Mono<TableWaiterAssignmentDTO> tableWaiterAssignmentDTO = tableWaiterAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tableWaiterAssignmentDTO);
    }

    /**
     * {@code DELETE  /table-waiter-assignments/:id} : delete the "id" tableWaiterAssignment.
     *
     * @param id the id of the tableWaiterAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTableWaiterAssignment(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete TableWaiterAssignment : {}", id);
        return tableWaiterAssignmentService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
