package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.AppNavigationMenuItemRepository;
import com.atparui.rmsservice.service.AppNavigationMenuItemService;
import com.atparui.rmsservice.service.dto.AppNavigationMenuItemDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.AppNavigationMenuItem}.
 */
@RestController
@RequestMapping("/api/app-navigation-menu-items")
public class AppNavigationMenuItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppNavigationMenuItemResource.class);

    private static final String ENTITY_NAME = "rmsserviceAppNavigationMenuItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppNavigationMenuItemService appNavigationMenuItemService;

    private final AppNavigationMenuItemRepository appNavigationMenuItemRepository;

    public AppNavigationMenuItemResource(
        AppNavigationMenuItemService appNavigationMenuItemService,
        AppNavigationMenuItemRepository appNavigationMenuItemRepository
    ) {
        this.appNavigationMenuItemService = appNavigationMenuItemService;
        this.appNavigationMenuItemRepository = appNavigationMenuItemRepository;
    }

    /**
     * {@code POST  /app-navigation-menu-items} : Create a new appNavigationMenuItem.
     *
     * @param appNavigationMenuItemDTO the appNavigationMenuItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appNavigationMenuItemDTO, or with status {@code 400 (Bad Request)} if the appNavigationMenuItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AppNavigationMenuItemDTO>> createAppNavigationMenuItem(
        @Valid @RequestBody AppNavigationMenuItemDTO appNavigationMenuItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save AppNavigationMenuItem : {}", appNavigationMenuItemDTO);
        if (appNavigationMenuItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new appNavigationMenuItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        appNavigationMenuItemDTO.setId(UUID.randomUUID());
        return appNavigationMenuItemService
            .save(appNavigationMenuItemDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/app-navigation-menu-items/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /app-navigation-menu-items/:id} : Updates an existing appNavigationMenuItem.
     *
     * @param id the id of the appNavigationMenuItemDTO to save.
     * @param appNavigationMenuItemDTO the appNavigationMenuItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appNavigationMenuItemDTO,
     * or with status {@code 400 (Bad Request)} if the appNavigationMenuItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appNavigationMenuItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AppNavigationMenuItemDTO>> updateAppNavigationMenuItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody AppNavigationMenuItemDTO appNavigationMenuItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AppNavigationMenuItem : {}, {}", id, appNavigationMenuItemDTO);
        if (appNavigationMenuItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appNavigationMenuItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appNavigationMenuItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return appNavigationMenuItemService
                    .update(appNavigationMenuItemDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /app-navigation-menu-items/:id} : Partial updates given fields of an existing appNavigationMenuItem, field will ignore if it is null
     *
     * @param id the id of the appNavigationMenuItemDTO to save.
     * @param appNavigationMenuItemDTO the appNavigationMenuItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appNavigationMenuItemDTO,
     * or with status {@code 400 (Bad Request)} if the appNavigationMenuItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appNavigationMenuItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appNavigationMenuItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AppNavigationMenuItemDTO>> partialUpdateAppNavigationMenuItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody AppNavigationMenuItemDTO appNavigationMenuItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AppNavigationMenuItem partially : {}, {}", id, appNavigationMenuItemDTO);
        if (appNavigationMenuItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appNavigationMenuItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appNavigationMenuItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AppNavigationMenuItemDTO> result = appNavigationMenuItemService.partialUpdate(appNavigationMenuItemDTO);

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
     * {@code GET  /app-navigation-menu-items} : get all the appNavigationMenuItems.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appNavigationMenuItems in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<AppNavigationMenuItemDTO>>> getAllAppNavigationMenuItems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of AppNavigationMenuItems");
        return appNavigationMenuItemService
            .countAll()
            .zipWith(appNavigationMenuItemService.findAll(pageable).collectList())
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
     * {@code GET  /app-navigation-menu-items/:id} : get the "id" appNavigationMenuItem.
     *
     * @param id the id of the appNavigationMenuItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appNavigationMenuItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AppNavigationMenuItemDTO>> getAppNavigationMenuItem(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get AppNavigationMenuItem : {}", id);
        Mono<AppNavigationMenuItemDTO> appNavigationMenuItemDTO = appNavigationMenuItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appNavigationMenuItemDTO);
    }

    /**
     * {@code DELETE  /app-navigation-menu-items/:id} : delete the "id" appNavigationMenuItem.
     *
     * @param id the id of the appNavigationMenuItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAppNavigationMenuItem(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete AppNavigationMenuItem : {}", id);
        return appNavigationMenuItemService
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
