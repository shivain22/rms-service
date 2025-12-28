package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.AppNavigationMenuRoleRepository;
import com.atparui.rmsservice.service.AppNavigationMenuRoleService;
import com.atparui.rmsservice.service.dto.AppNavigationMenuRoleDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.AppNavigationMenuRole}.
 */
@RestController
@RequestMapping("/api/app-navigation-menu-roles")
public class AppNavigationMenuRoleResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppNavigationMenuRoleResource.class);

    private static final String ENTITY_NAME = "rmsserviceAppNavigationMenuRole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppNavigationMenuRoleService appNavigationMenuRoleService;

    private final AppNavigationMenuRoleRepository appNavigationMenuRoleRepository;

    public AppNavigationMenuRoleResource(
        AppNavigationMenuRoleService appNavigationMenuRoleService,
        AppNavigationMenuRoleRepository appNavigationMenuRoleRepository
    ) {
        this.appNavigationMenuRoleService = appNavigationMenuRoleService;
        this.appNavigationMenuRoleRepository = appNavigationMenuRoleRepository;
    }

    /**
     * {@code POST  /app-navigation-menu-roles} : Create a new appNavigationMenuRole.
     *
     * @param appNavigationMenuRoleDTO the appNavigationMenuRoleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appNavigationMenuRoleDTO, or with status {@code 400 (Bad Request)} if the appNavigationMenuRole has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AppNavigationMenuRoleDTO>> createAppNavigationMenuRole(
        @Valid @RequestBody AppNavigationMenuRoleDTO appNavigationMenuRoleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save AppNavigationMenuRole : {}", appNavigationMenuRoleDTO);
        if (appNavigationMenuRoleDTO.getId() != null) {
            throw new BadRequestAlertException("A new appNavigationMenuRole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        appNavigationMenuRoleDTO.setId(UUID.randomUUID());
        return appNavigationMenuRoleService
            .save(appNavigationMenuRoleDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/app-navigation-menu-roles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /app-navigation-menu-roles/:id} : Updates an existing appNavigationMenuRole.
     *
     * @param id the id of the appNavigationMenuRoleDTO to save.
     * @param appNavigationMenuRoleDTO the appNavigationMenuRoleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appNavigationMenuRoleDTO,
     * or with status {@code 400 (Bad Request)} if the appNavigationMenuRoleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appNavigationMenuRoleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AppNavigationMenuRoleDTO>> updateAppNavigationMenuRole(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody AppNavigationMenuRoleDTO appNavigationMenuRoleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AppNavigationMenuRole : {}, {}", id, appNavigationMenuRoleDTO);
        if (appNavigationMenuRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appNavigationMenuRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appNavigationMenuRoleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return appNavigationMenuRoleService
                    .update(appNavigationMenuRoleDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /app-navigation-menu-roles/:id} : Partial updates given fields of an existing appNavigationMenuRole, field will ignore if it is null
     *
     * @param id the id of the appNavigationMenuRoleDTO to save.
     * @param appNavigationMenuRoleDTO the appNavigationMenuRoleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appNavigationMenuRoleDTO,
     * or with status {@code 400 (Bad Request)} if the appNavigationMenuRoleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appNavigationMenuRoleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appNavigationMenuRoleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AppNavigationMenuRoleDTO>> partialUpdateAppNavigationMenuRole(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody AppNavigationMenuRoleDTO appNavigationMenuRoleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AppNavigationMenuRole partially : {}, {}", id, appNavigationMenuRoleDTO);
        if (appNavigationMenuRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appNavigationMenuRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appNavigationMenuRoleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AppNavigationMenuRoleDTO> result = appNavigationMenuRoleService.partialUpdate(appNavigationMenuRoleDTO);

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
     * {@code GET  /app-navigation-menu-roles} : get all the appNavigationMenuRoles.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appNavigationMenuRoles in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<AppNavigationMenuRoleDTO>>> getAllAppNavigationMenuRoles(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of AppNavigationMenuRoles");
        return appNavigationMenuRoleService
            .countAll()
            .zipWith(appNavigationMenuRoleService.findAll(pageable).collectList())
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
     * {@code GET  /app-navigation-menu-roles/:id} : get the "id" appNavigationMenuRole.
     *
     * @param id the id of the appNavigationMenuRoleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appNavigationMenuRoleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AppNavigationMenuRoleDTO>> getAppNavigationMenuRole(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get AppNavigationMenuRole : {}", id);
        Mono<AppNavigationMenuRoleDTO> appNavigationMenuRoleDTO = appNavigationMenuRoleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appNavigationMenuRoleDTO);
    }

    /**
     * {@code DELETE  /app-navigation-menu-roles/:id} : delete the "id" appNavigationMenuRole.
     *
     * @param id the id of the appNavigationMenuRoleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAppNavigationMenuRole(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete AppNavigationMenuRole : {}", id);
        return appNavigationMenuRoleService
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
