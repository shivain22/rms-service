package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.MenuItemRepository;
import com.atparui.rmsservice.service.MenuItemService;
import com.atparui.rmsservice.service.dto.MenuItemDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.MenuItem}.
 */
@RestController
@RequestMapping("/api/menu-items")
public class MenuItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemResource.class);

    private static final String ENTITY_NAME = "rmsserviceMenuItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuItemService menuItemService;

    private final MenuItemRepository menuItemRepository;

    public MenuItemResource(MenuItemService menuItemService, MenuItemRepository menuItemRepository) {
        this.menuItemService = menuItemService;
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * {@code POST  /menu-items} : Create a new menuItem.
     *
     * @param menuItemDTO the menuItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menuItemDTO, or with status {@code 400 (Bad Request)} if the menuItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MenuItemDTO>> createMenuItem(@Valid @RequestBody MenuItemDTO menuItemDTO) throws URISyntaxException {
        LOG.debug("REST request to save MenuItem : {}", menuItemDTO);
        if (menuItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new menuItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        menuItemDTO.setId(UUID.randomUUID());
        return menuItemService
            .save(menuItemDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/menu-items/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /menu-items/:id} : Updates an existing menuItem.
     *
     * @param id the id of the menuItemDTO to save.
     * @param menuItemDTO the menuItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menuItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MenuItemDTO>> updateMenuItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody MenuItemDTO menuItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MenuItem : {}, {}", id, menuItemDTO);
        if (menuItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return menuItemService
                    .update(menuItemDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /menu-items/:id} : Partial updates given fields of an existing menuItem, field will ignore if it is null
     *
     * @param id the id of the menuItemDTO to save.
     * @param menuItemDTO the menuItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the menuItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the menuItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MenuItemDTO>> partialUpdateMenuItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody MenuItemDTO menuItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MenuItem partially : {}, {}", id, menuItemDTO);
        if (menuItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MenuItemDTO> result = menuItemService.partialUpdate(menuItemDTO);

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
     * {@code GET  /menu-items} : get all the menuItems.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menuItems in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MenuItemDTO>>> getAllMenuItems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of MenuItems");
        return menuItemService
            .countAll()
            .zipWith(menuItemService.findAll(pageable).collectList())
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
     * {@code GET  /menu-items/:id} : get the "id" menuItem.
     *
     * @param id the id of the menuItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menuItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MenuItemDTO>> getMenuItem(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get MenuItem : {}", id);
        Mono<MenuItemDTO> menuItemDTO = menuItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(menuItemDTO);
    }

    /**
     * {@code DELETE  /menu-items/:id} : delete the "id" menuItem.
     *
     * @param id the id of the menuItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMenuItem(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete MenuItem : {}", id);
        return menuItemService
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
     * {@code SEARCH  /menu-items/_search?query=:query} : search for the menuItem corresponding
     * to the query.
     *
     * @param query the query of the menuItem search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<MenuItemDTO>>> searchMenuItems(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of MenuItems for query {}", query);
        return menuItemService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(menuItemService.search(query, pageable)));
    }

    // jhipster-needle-rest-add-get-method - JHipster will add get methods here

    /**
     * {@code GET /api/menu-items/branch/{branchId}/available} : Get available menu items
     *
     * @param branchId the branch ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and list of menu items
     */
    @GetMapping("/branch/{branchId}/available")
    public Mono<ResponseEntity<List<MenuItemDTO>>> getAvailableMenuItems(@PathVariable UUID branchId) {
        LOG.debug("REST request to get available menu items for branch : {}", branchId);
        return menuItemService.findAvailableByBranchId(branchId).collectList().map(result -> ResponseEntity.ok().body(result));
    }

    /**
     * {@code GET /api/menu-items/category/{categoryId}} : Get menu items by category
     *
     * @param categoryId the menu category ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and list of menu items
     */
    @GetMapping("/category/{categoryId}")
    public Mono<ResponseEntity<List<MenuItemDTO>>> getMenuItemsByCategory(@PathVariable UUID categoryId) {
        LOG.debug("REST request to get menu items by category : {}", categoryId);
        return menuItemService.findByCategoryId(categoryId).collectList().map(result -> ResponseEntity.ok().body(result));
    }

    /**
     * {@code GET /api/menu-items/branch/{branchId}/filtered} : Get filtered menu items
     *
     * @param branchId the branch ID
     * @param itemType the item type (EATABLE, BEVERAGE)
     * @param cuisineType the cuisine type
     * @param isVegetarian vegetarian filter
     * @param isAlcoholic alcoholic filter
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and list of menu items
     */
    @GetMapping("/branch/{branchId}/filtered")
    public Mono<ResponseEntity<List<MenuItemDTO>>> getFilteredMenuItems(
        @PathVariable UUID branchId,
        @RequestParam(required = false) String itemType,
        @RequestParam(required = false) String cuisineType,
        @RequestParam(required = false) Boolean isVegetarian,
        @RequestParam(required = false) Boolean isAlcoholic
    ) {
        LOG.debug("REST request to get filtered menu items for branch : {}", branchId);
        return menuItemService
            .findFiltered(branchId, itemType, cuisineType, isVegetarian, isAlcoholic)
            .collectList()
            .map(result -> ResponseEntity.ok().body(result));
    }

    // jhipster-needle-rest-add-put-method - JHipster will add put methods here

    /**
     * {@code PUT /api/menu-items/{id}/availability} : Update menu item availability
     *
     * @param id the id of the menu item
     * @param request the availability request
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and updated menu item DTO
     */
    @PutMapping("/{id}/availability")
    public Mono<ResponseEntity<MenuItemDTO>> updateAvailability(
        @PathVariable UUID id,
        @RequestBody java.util.Map<String, Boolean> request
    ) {
        Boolean isAvailable = request.get("isAvailable");
        LOG.debug("REST request to update menu item availability : {} - {}", id, isAvailable);
        return menuItemService.updateAvailability(id, isAvailable).map(result -> ResponseEntity.ok().body(result));
    }
}
