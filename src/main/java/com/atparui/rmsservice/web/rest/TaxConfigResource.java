package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.TaxConfigRepository;
import com.atparui.rmsservice.service.TaxConfigService;
import com.atparui.rmsservice.service.dto.TaxConfigDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.TaxConfig}.
 */
@RestController
@RequestMapping("/api/tax-configs")
public class TaxConfigResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaxConfigResource.class);

    private static final String ENTITY_NAME = "rmsserviceTaxConfig";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaxConfigService taxConfigService;

    private final TaxConfigRepository taxConfigRepository;

    public TaxConfigResource(TaxConfigService taxConfigService, TaxConfigRepository taxConfigRepository) {
        this.taxConfigService = taxConfigService;
        this.taxConfigRepository = taxConfigRepository;
    }

    /**
     * {@code POST  /tax-configs} : Create a new taxConfig.
     *
     * @param taxConfigDTO the taxConfigDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taxConfigDTO, or with status {@code 400 (Bad Request)} if the taxConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TaxConfigDTO>> createTaxConfig(@Valid @RequestBody TaxConfigDTO taxConfigDTO) throws URISyntaxException {
        LOG.debug("REST request to save TaxConfig : {}", taxConfigDTO);
        if (taxConfigDTO.getId() != null) {
            throw new BadRequestAlertException("A new taxConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        taxConfigDTO.setId(UUID.randomUUID());
        return taxConfigService
            .save(taxConfigDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/tax-configs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tax-configs/:id} : Updates an existing taxConfig.
     *
     * @param id the id of the taxConfigDTO to save.
     * @param taxConfigDTO the taxConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxConfigDTO,
     * or with status {@code 400 (Bad Request)} if the taxConfigDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taxConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaxConfigDTO>> updateTaxConfig(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody TaxConfigDTO taxConfigDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TaxConfig : {}, {}", id, taxConfigDTO);
        if (taxConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxConfigDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return taxConfigRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return taxConfigService
                    .update(taxConfigDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /tax-configs/:id} : Partial updates given fields of an existing taxConfig, field will ignore if it is null
     *
     * @param id the id of the taxConfigDTO to save.
     * @param taxConfigDTO the taxConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxConfigDTO,
     * or with status {@code 400 (Bad Request)} if the taxConfigDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taxConfigDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taxConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TaxConfigDTO>> partialUpdateTaxConfig(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody TaxConfigDTO taxConfigDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TaxConfig partially : {}, {}", id, taxConfigDTO);
        if (taxConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxConfigDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return taxConfigRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TaxConfigDTO> result = taxConfigService.partialUpdate(taxConfigDTO);

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
     * {@code GET  /tax-configs} : get all the taxConfigs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taxConfigs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<TaxConfigDTO>> getAllTaxConfigs() {
        LOG.debug("REST request to get all TaxConfigs");
        return taxConfigService.findAll().collectList();
    }

    /**
     * {@code GET  /tax-configs} : get all the taxConfigs as a stream.
     * @return the {@link Flux} of taxConfigs.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TaxConfigDTO> getAllTaxConfigsAsStream() {
        LOG.debug("REST request to get all TaxConfigs as a stream");
        return taxConfigService.findAll();
    }

    /**
     * {@code GET  /tax-configs/:id} : get the "id" taxConfig.
     *
     * @param id the id of the taxConfigDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taxConfigDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaxConfigDTO>> getTaxConfig(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get TaxConfig : {}", id);
        Mono<TaxConfigDTO> taxConfigDTO = taxConfigService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taxConfigDTO);
    }

    /**
     * {@code DELETE  /tax-configs/:id} : delete the "id" taxConfig.
     *
     * @param id the id of the taxConfigDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTaxConfig(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete TaxConfig : {}", id);
        return taxConfigService
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
