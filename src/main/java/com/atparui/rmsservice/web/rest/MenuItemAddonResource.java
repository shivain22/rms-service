package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.MenuItemAddonRepository;
import com.atparui.rmsservice.service.MenuItemAddonService;
import com.atparui.rmsservice.service.dto.MenuItemAddonDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.MenuItemAddon}.
 */
@RestController
@RequestMapping("/api/menu-item-addons")
public class MenuItemAddonResource {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemAddonResource.class);

    private static final String ENTITY_NAME = "rmsserviceMenuItemAddon";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuItemAddonService menuItemAddonService;

    private final MenuItemAddonRepository menuItemAddonRepository;

    public MenuItemAddonResource(MenuItemAddonService menuItemAddonService, MenuItemAddonRepository menuItemAddonRepository) {
        this.menuItemAddonService = menuItemAddonService;
        this.menuItemAddonRepository = menuItemAddonRepository;
    }

    /**
     * {@code POST  /menu-item-addons} : Create a new menuItemAddon.
     *
     * @param menuItemAddonDTO the menuItemAddonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menuItemAddonDTO, or with status {@code 400 (Bad Request)} if the menuItemAddon has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MenuItemAddonDTO>> createMenuItemAddon(@Valid @RequestBody MenuItemAddonDTO menuItemAddonDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MenuItemAddon : {}", menuItemAddonDTO);
        if (menuItemAddonDTO.getId() != null) {
            throw new BadRequestAlertException("A new menuItemAddon cannot already have an ID", ENTITY_NAME, "idexists");
        }
        menuItemAddonDTO.setId(UUID.randomUUID());
        return menuItemAddonService
            .save(menuItemAddonDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/menu-item-addons/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /menu-item-addons/:id} : Updates an existing menuItemAddon.
     *
     * @param id the id of the menuItemAddonDTO to save.
     * @param menuItemAddonDTO the menuItemAddonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemAddonDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemAddonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menuItemAddonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MenuItemAddonDTO>> updateMenuItemAddon(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody MenuItemAddonDTO menuItemAddonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MenuItemAddon : {}, {}", id, menuItemAddonDTO);
        if (menuItemAddonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemAddonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuItemAddonRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return menuItemAddonService
                    .update(menuItemAddonDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /menu-item-addons/:id} : Partial updates given fields of an existing menuItemAddon, field will ignore if it is null
     *
     * @param id the id of the menuItemAddonDTO to save.
     * @param menuItemAddonDTO the menuItemAddonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemAddonDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemAddonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the menuItemAddonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the menuItemAddonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MenuItemAddonDTO>> partialUpdateMenuItemAddon(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody MenuItemAddonDTO menuItemAddonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MenuItemAddon partially : {}, {}", id, menuItemAddonDTO);
        if (menuItemAddonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemAddonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuItemAddonRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MenuItemAddonDTO> result = menuItemAddonService.partialUpdate(menuItemAddonDTO);

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
     * {@code GET  /menu-item-addons} : get all the menuItemAddons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menuItemAddons in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<MenuItemAddonDTO>> getAllMenuItemAddons() {
        LOG.debug("REST request to get all MenuItemAddons");
        return menuItemAddonService.findAll().collectList();
    }

    /**
     * {@code GET  /menu-item-addons} : get all the menuItemAddons as a stream.
     * @return the {@link Flux} of menuItemAddons.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MenuItemAddonDTO> getAllMenuItemAddonsAsStream() {
        LOG.debug("REST request to get all MenuItemAddons as a stream");
        return menuItemAddonService.findAll();
    }

    /**
     * {@code GET  /menu-item-addons/:id} : get the "id" menuItemAddon.
     *
     * @param id the id of the menuItemAddonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menuItemAddonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MenuItemAddonDTO>> getMenuItemAddon(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get MenuItemAddon : {}", id);
        Mono<MenuItemAddonDTO> menuItemAddonDTO = menuItemAddonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(menuItemAddonDTO);
    }

    /**
     * {@code DELETE  /menu-item-addons/:id} : delete the "id" menuItemAddon.
     *
     * @param id the id of the menuItemAddonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMenuItemAddon(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete MenuItemAddon : {}", id);
        return menuItemAddonService
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
