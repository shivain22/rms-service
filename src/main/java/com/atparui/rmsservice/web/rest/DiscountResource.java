package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.DiscountRepository;
import com.atparui.rmsservice.service.DiscountService;
import com.atparui.rmsservice.service.dto.DiscountDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.Discount}.
 */
@RestController
@RequestMapping("/api/discounts")
public class DiscountResource {

    private static final Logger LOG = LoggerFactory.getLogger(DiscountResource.class);

    private static final String ENTITY_NAME = "rmsserviceDiscount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DiscountService discountService;

    private final DiscountRepository discountRepository;

    public DiscountResource(DiscountService discountService, DiscountRepository discountRepository) {
        this.discountService = discountService;
        this.discountRepository = discountRepository;
    }

    /**
     * {@code POST  /discounts} : Create a new discount.
     *
     * @param discountDTO the discountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new discountDTO, or with status {@code 400 (Bad Request)} if the discount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DiscountDTO>> createDiscount(@Valid @RequestBody DiscountDTO discountDTO) throws URISyntaxException {
        LOG.debug("REST request to save Discount : {}", discountDTO);
        if (discountDTO.getId() != null) {
            throw new BadRequestAlertException("A new discount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        discountDTO.setId(UUID.randomUUID());
        return discountService
            .save(discountDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/discounts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /discounts/:id} : Updates an existing discount.
     *
     * @param id the id of the discountDTO to save.
     * @param discountDTO the discountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated discountDTO,
     * or with status {@code 400 (Bad Request)} if the discountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the discountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DiscountDTO>> updateDiscount(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody DiscountDTO discountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Discount : {}, {}", id, discountDTO);
        if (discountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, discountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return discountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return discountService
                    .update(discountDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /discounts/:id} : Partial updates given fields of an existing discount, field will ignore if it is null
     *
     * @param id the id of the discountDTO to save.
     * @param discountDTO the discountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated discountDTO,
     * or with status {@code 400 (Bad Request)} if the discountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the discountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the discountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DiscountDTO>> partialUpdateDiscount(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody DiscountDTO discountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Discount partially : {}, {}", id, discountDTO);
        if (discountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, discountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return discountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<DiscountDTO> result = discountService.partialUpdate(discountDTO);

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
     * {@code GET  /discounts} : get all the discounts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of discounts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<DiscountDTO>> getAllDiscounts() {
        LOG.debug("REST request to get all Discounts");
        return discountService.findAll().collectList();
    }

    /**
     * {@code GET  /discounts} : get all the discounts as a stream.
     * @return the {@link Flux} of discounts.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<DiscountDTO> getAllDiscountsAsStream() {
        LOG.debug("REST request to get all Discounts as a stream");
        return discountService.findAll();
    }

    /**
     * {@code GET  /discounts/:id} : get the "id" discount.
     *
     * @param id the id of the discountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the discountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DiscountDTO>> getDiscount(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get Discount : {}", id);
        Mono<DiscountDTO> discountDTO = discountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(discountDTO);
    }

    /**
     * {@code DELETE  /discounts/:id} : delete the "id" discount.
     *
     * @param id the id of the discountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDiscount(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete Discount : {}", id);
        return discountService
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
