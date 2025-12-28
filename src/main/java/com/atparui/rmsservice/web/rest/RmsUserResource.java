package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.RmsUserRepository;
import com.atparui.rmsservice.service.RmsUserService;
import com.atparui.rmsservice.service.dto.RmsUserDTO;
import com.atparui.rmsservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.atparui.rmsservice.domain.RmsUser}.
 */
@RestController
@RequestMapping("/api/rms-users")
public class RmsUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(RmsUserResource.class);

    private static final String ENTITY_NAME = "rmsserviceRmsUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RmsUserService rmsUserService;

    private final RmsUserRepository rmsUserRepository;

    public RmsUserResource(RmsUserService rmsUserService, RmsUserRepository rmsUserRepository) {
        this.rmsUserService = rmsUserService;
        this.rmsUserRepository = rmsUserRepository;
    }

    /**
     * {@code POST  /rms-users} : Create a new rmsUser.
     *
     * @param rmsUserDTO the rmsUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rmsUserDTO, or with status {@code 400 (Bad Request)} if the rmsUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RmsUserDTO>> createRmsUser(@Valid @RequestBody RmsUserDTO rmsUserDTO) throws URISyntaxException {
        LOG.debug("REST request to save RmsUser : {}", rmsUserDTO);
        if (rmsUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new rmsUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        rmsUserDTO.setId(UUID.randomUUID());
        return rmsUserService
            .save(rmsUserDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/rms-users/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /rms-users/:id} : Updates an existing rmsUser.
     *
     * @param id the id of the rmsUserDTO to save.
     * @param rmsUserDTO the rmsUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rmsUserDTO,
     * or with status {@code 400 (Bad Request)} if the rmsUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rmsUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RmsUserDTO>> updateRmsUser(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody RmsUserDTO rmsUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RmsUser : {}, {}", id, rmsUserDTO);
        if (rmsUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rmsUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rmsUserRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return rmsUserService
                    .update(rmsUserDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /rms-users/:id} : Partial updates given fields of an existing rmsUser, field will ignore if it is null
     *
     * @param id the id of the rmsUserDTO to save.
     * @param rmsUserDTO the rmsUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rmsUserDTO,
     * or with status {@code 400 (Bad Request)} if the rmsUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rmsUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rmsUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RmsUserDTO>> partialUpdateRmsUser(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody RmsUserDTO rmsUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RmsUser partially : {}, {}", id, rmsUserDTO);
        if (rmsUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rmsUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rmsUserRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RmsUserDTO> result = rmsUserService.partialUpdate(rmsUserDTO);

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
     * {@code GET  /rms-users} : get all the rmsUsers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rmsUsers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RmsUserDTO>>> getAllRmsUsers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of RmsUsers");
        return rmsUserService
            .countAll()
            .zipWith(rmsUserService.findAll(pageable).collectList())
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
     * {@code GET  /rms-users/:id} : get the "id" rmsUser.
     *
     * @param id the id of the rmsUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rmsUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RmsUserDTO>> getRmsUser(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get RmsUser : {}", id);
        Mono<RmsUserDTO> rmsUserDTO = rmsUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rmsUserDTO);
    }

    /**
     * {@code DELETE  /rms-users/:id} : delete the "id" rmsUser.
     *
     * @param id the id of the rmsUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRmsUser(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete RmsUser : {}", id);
        return rmsUserService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /rms-users/_search?query=:query} : search for the rmsUser corresponding
     * to the query.
     *
     * @param query the query of the rmsUser search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<RmsUserDTO>>> searchRmsUsers(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of RmsUsers for query {}", query);
        return rmsUserService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(rmsUserService.search(query, pageable)));
    }
}
