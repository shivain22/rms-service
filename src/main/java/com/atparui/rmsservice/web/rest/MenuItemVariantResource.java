package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.MenuItemVariantRepository;
import com.atparui.rmsservice.service.MenuItemVariantService;
import com.atparui.rmsservice.service.dto.MenuItemVariantDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.MenuItemVariant}.
 */
@RestController
@RequestMapping("/api/menu-item-variants")
public class MenuItemVariantResource {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemVariantResource.class);

    private static final String ENTITY_NAME = "rmsserviceMenuItemVariant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuItemVariantService menuItemVariantService;

    private final MenuItemVariantRepository menuItemVariantRepository;

    public MenuItemVariantResource(MenuItemVariantService menuItemVariantService, MenuItemVariantRepository menuItemVariantRepository) {
        this.menuItemVariantService = menuItemVariantService;
        this.menuItemVariantRepository = menuItemVariantRepository;
    }

    /**
     * {@code POST  /menu-item-variants} : Create a new menuItemVariant.
     *
     * @param menuItemVariantDTO the menuItemVariantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menuItemVariantDTO, or with status {@code 400 (Bad Request)} if the menuItemVariant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MenuItemVariantDTO>> createMenuItemVariant(@Valid @RequestBody MenuItemVariantDTO menuItemVariantDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MenuItemVariant : {}", menuItemVariantDTO);
        if (menuItemVariantDTO.getId() != null) {
            throw new BadRequestAlertException("A new menuItemVariant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        menuItemVariantDTO.setId(UUID.randomUUID());
        return menuItemVariantService
            .save(menuItemVariantDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/menu-item-variants/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /menu-item-variants/:id} : Updates an existing menuItemVariant.
     *
     * @param id the id of the menuItemVariantDTO to save.
     * @param menuItemVariantDTO the menuItemVariantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemVariantDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemVariantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menuItemVariantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MenuItemVariantDTO>> updateMenuItemVariant(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody MenuItemVariantDTO menuItemVariantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MenuItemVariant : {}, {}", id, menuItemVariantDTO);
        if (menuItemVariantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemVariantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuItemVariantRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return menuItemVariantService
                    .update(menuItemVariantDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /menu-item-variants/:id} : Partial updates given fields of an existing menuItemVariant, field will ignore if it is null
     *
     * @param id the id of the menuItemVariantDTO to save.
     * @param menuItemVariantDTO the menuItemVariantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuItemVariantDTO,
     * or with status {@code 400 (Bad Request)} if the menuItemVariantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the menuItemVariantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the menuItemVariantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MenuItemVariantDTO>> partialUpdateMenuItemVariant(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody MenuItemVariantDTO menuItemVariantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MenuItemVariant partially : {}, {}", id, menuItemVariantDTO);
        if (menuItemVariantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuItemVariantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuItemVariantRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MenuItemVariantDTO> result = menuItemVariantService.partialUpdate(menuItemVariantDTO);

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
     * {@code GET  /menu-item-variants} : get all the menuItemVariants.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menuItemVariants in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<MenuItemVariantDTO>> getAllMenuItemVariants() {
        LOG.debug("REST request to get all MenuItemVariants");
        return menuItemVariantService.findAll().collectList();
    }

    /**
     * {@code GET  /menu-item-variants} : get all the menuItemVariants as a stream.
     * @return the {@link Flux} of menuItemVariants.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MenuItemVariantDTO> getAllMenuItemVariantsAsStream() {
        LOG.debug("REST request to get all MenuItemVariants as a stream");
        return menuItemVariantService.findAll();
    }

    /**
     * {@code GET  /menu-item-variants/:id} : get the "id" menuItemVariant.
     *
     * @param id the id of the menuItemVariantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menuItemVariantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MenuItemVariantDTO>> getMenuItemVariant(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get MenuItemVariant : {}", id);
        Mono<MenuItemVariantDTO> menuItemVariantDTO = menuItemVariantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(menuItemVariantDTO);
    }

    /**
     * {@code DELETE  /menu-item-variants/:id} : delete the "id" menuItemVariant.
     *
     * @param id the id of the menuItemVariantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMenuItemVariant(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete MenuItemVariant : {}", id);
        return menuItemVariantService
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
