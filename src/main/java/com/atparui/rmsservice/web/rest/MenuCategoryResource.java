package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.MenuCategoryRepository;
import com.atparui.rmsservice.service.MenuCategoryService;
import com.atparui.rmsservice.service.dto.MenuCategoryDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.MenuCategory}.
 */
@RestController
@RequestMapping("/api/menu-categories")
public class MenuCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(MenuCategoryResource.class);

    private static final String ENTITY_NAME = "rmsserviceMenuCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuCategoryService menuCategoryService;

    private final MenuCategoryRepository menuCategoryRepository;

    public MenuCategoryResource(MenuCategoryService menuCategoryService, MenuCategoryRepository menuCategoryRepository) {
        this.menuCategoryService = menuCategoryService;
        this.menuCategoryRepository = menuCategoryRepository;
    }

    /**
     * {@code POST  /menu-categories} : Create a new menuCategory.
     *
     * @param menuCategoryDTO the menuCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menuCategoryDTO, or with status {@code 400 (Bad Request)} if the menuCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MenuCategoryDTO>> createMenuCategory(@Valid @RequestBody MenuCategoryDTO menuCategoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MenuCategory : {}", menuCategoryDTO);
        if (menuCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new menuCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        menuCategoryDTO.setId(UUID.randomUUID());
        return menuCategoryService
            .save(menuCategoryDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/menu-categories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /menu-categories/:id} : Updates an existing menuCategory.
     *
     * @param id the id of the menuCategoryDTO to save.
     * @param menuCategoryDTO the menuCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the menuCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menuCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MenuCategoryDTO>> updateMenuCategory(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody MenuCategoryDTO menuCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MenuCategory : {}, {}", id, menuCategoryDTO);
        if (menuCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuCategoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return menuCategoryService
                    .update(menuCategoryDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /menu-categories/:id} : Partial updates given fields of an existing menuCategory, field will ignore if it is null
     *
     * @param id the id of the menuCategoryDTO to save.
     * @param menuCategoryDTO the menuCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the menuCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the menuCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the menuCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MenuCategoryDTO>> partialUpdateMenuCategory(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody MenuCategoryDTO menuCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MenuCategory partially : {}, {}", id, menuCategoryDTO);
        if (menuCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuCategoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MenuCategoryDTO> result = menuCategoryService.partialUpdate(menuCategoryDTO);

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
     * {@code GET  /menu-categories} : get all the menuCategories.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menuCategories in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MenuCategoryDTO>>> getAllMenuCategories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of MenuCategories");
        return menuCategoryService
            .countAll()
            .zipWith(menuCategoryService.findAll(pageable).collectList())
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
     * {@code GET  /menu-categories/:id} : get the "id" menuCategory.
     *
     * @param id the id of the menuCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menuCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MenuCategoryDTO>> getMenuCategory(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get MenuCategory : {}", id);
        Mono<MenuCategoryDTO> menuCategoryDTO = menuCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(menuCategoryDTO);
    }

    /**
     * {@code DELETE  /menu-categories/:id} : delete the "id" menuCategory.
     *
     * @param id the id of the menuCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMenuCategory(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete MenuCategory : {}", id);
        return menuCategoryService
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
     * {@code SEARCH  /menu-categories/_search?query=:query} : search for the menuCategory corresponding
     * to the query.
     *
     * @param query the query of the menuCategory search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MenuCategoryDTO>>> searchMenuCategories(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of MenuCategories for query {}", query);
        return menuCategoryService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(menuCategoryService.search(query, pageable)));
    }
}
