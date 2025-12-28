package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.ShiftRepository;
import com.atparui.rmsservice.service.ShiftService;
import com.atparui.rmsservice.service.dto.ShiftDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.atparui.rmsservice.domain.Shift}.
 */
@RestController
@RequestMapping("/api/shifts")
public class ShiftResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShiftResource.class);

    private static final String ENTITY_NAME = "rmsserviceShift";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShiftService shiftService;

    private final ShiftRepository shiftRepository;

    public ShiftResource(ShiftService shiftService, ShiftRepository shiftRepository) {
        this.shiftService = shiftService;
        this.shiftRepository = shiftRepository;
    }

    /**
     * {@code POST  /shifts} : Create a new shift.
     *
     * @param shiftDTO the shiftDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shiftDTO, or with status {@code 400 (Bad Request)} if the shift has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ShiftDTO>> createShift(@Valid @RequestBody ShiftDTO shiftDTO) throws URISyntaxException {
        LOG.debug("REST request to save Shift : {}", shiftDTO);
        if (shiftDTO.getId() != null) {
            throw new BadRequestAlertException("A new shift cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shiftDTO.setId(UUID.randomUUID());
        return shiftService
            .save(shiftDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/shifts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /shifts/:id} : Updates an existing shift.
     *
     * @param id the id of the shiftDTO to save.
     * @param shiftDTO the shiftDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shiftDTO,
     * or with status {@code 400 (Bad Request)} if the shiftDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shiftDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ShiftDTO>> updateShift(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody ShiftDTO shiftDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Shift : {}, {}", id, shiftDTO);
        if (shiftDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shiftDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shiftRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return shiftService
                    .update(shiftDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /shifts/:id} : Partial updates given fields of an existing shift, field will ignore if it is null
     *
     * @param id the id of the shiftDTO to save.
     * @param shiftDTO the shiftDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shiftDTO,
     * or with status {@code 400 (Bad Request)} if the shiftDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shiftDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shiftDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ShiftDTO>> partialUpdateShift(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody ShiftDTO shiftDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Shift partially : {}, {}", id, shiftDTO);
        if (shiftDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shiftDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shiftRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ShiftDTO> result = shiftService.partialUpdate(shiftDTO);

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
     * {@code GET  /shifts} : get all the shifts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shifts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ShiftDTO>>> getAllShifts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Shifts");
        return shiftService
            .countAll()
            .zipWith(shiftService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /shifts/:id} : get the "id" shift.
     *
     * @param id the id of the shiftDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shiftDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ShiftDTO>> getShift(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get Shift : {}", id);
        Mono<ShiftDTO> shiftDTO = shiftService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shiftDTO);
    }

    /**
     * {@code DELETE  /shifts/:id} : delete the "id" shift.
     *
     * @param id the id of the shiftDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteShift(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete Shift : {}", id);
        return shiftService
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
