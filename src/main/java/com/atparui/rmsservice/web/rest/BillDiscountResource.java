package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.BillDiscountRepository;
import com.atparui.rmsservice.service.BillDiscountService;
import com.atparui.rmsservice.service.dto.BillDiscountDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.BillDiscount}.
 */
@RestController
@RequestMapping("/api/bill-discounts")
public class BillDiscountResource {

    private static final Logger LOG = LoggerFactory.getLogger(BillDiscountResource.class);

    private static final String ENTITY_NAME = "rmsserviceBillDiscount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BillDiscountService billDiscountService;

    private final BillDiscountRepository billDiscountRepository;

    public BillDiscountResource(BillDiscountService billDiscountService, BillDiscountRepository billDiscountRepository) {
        this.billDiscountService = billDiscountService;
        this.billDiscountRepository = billDiscountRepository;
    }

    /**
     * {@code POST  /bill-discounts} : Create a new billDiscount.
     *
     * @param billDiscountDTO the billDiscountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new billDiscountDTO, or with status {@code 400 (Bad Request)} if the billDiscount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<BillDiscountDTO>> createBillDiscount(@Valid @RequestBody BillDiscountDTO billDiscountDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save BillDiscount : {}", billDiscountDTO);
        if (billDiscountDTO.getId() != null) {
            throw new BadRequestAlertException("A new billDiscount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        billDiscountDTO.setId(UUID.randomUUID());
        return billDiscountService
            .save(billDiscountDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/bill-discounts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /bill-discounts/:id} : Updates an existing billDiscount.
     *
     * @param id the id of the billDiscountDTO to save.
     * @param billDiscountDTO the billDiscountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billDiscountDTO,
     * or with status {@code 400 (Bad Request)} if the billDiscountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the billDiscountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<BillDiscountDTO>> updateBillDiscount(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody BillDiscountDTO billDiscountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BillDiscount : {}, {}", id, billDiscountDTO);
        if (billDiscountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billDiscountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return billDiscountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return billDiscountService
                    .update(billDiscountDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /bill-discounts/:id} : Partial updates given fields of an existing billDiscount, field will ignore if it is null
     *
     * @param id the id of the billDiscountDTO to save.
     * @param billDiscountDTO the billDiscountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billDiscountDTO,
     * or with status {@code 400 (Bad Request)} if the billDiscountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the billDiscountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the billDiscountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<BillDiscountDTO>> partialUpdateBillDiscount(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody BillDiscountDTO billDiscountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BillDiscount partially : {}, {}", id, billDiscountDTO);
        if (billDiscountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billDiscountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return billDiscountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BillDiscountDTO> result = billDiscountService.partialUpdate(billDiscountDTO);

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
     * {@code GET  /bill-discounts} : get all the billDiscounts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of billDiscounts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<BillDiscountDTO>> getAllBillDiscounts() {
        LOG.debug("REST request to get all BillDiscounts");
        return billDiscountService.findAll().collectList();
    }

    /**
     * {@code GET  /bill-discounts} : get all the billDiscounts as a stream.
     * @return the {@link Flux} of billDiscounts.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<BillDiscountDTO> getAllBillDiscountsAsStream() {
        LOG.debug("REST request to get all BillDiscounts as a stream");
        return billDiscountService.findAll();
    }

    /**
     * {@code GET  /bill-discounts/:id} : get the "id" billDiscount.
     *
     * @param id the id of the billDiscountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the billDiscountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<BillDiscountDTO>> getBillDiscount(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get BillDiscount : {}", id);
        Mono<BillDiscountDTO> billDiscountDTO = billDiscountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(billDiscountDTO);
    }

    /**
     * {@code DELETE  /bill-discounts/:id} : delete the "id" billDiscount.
     *
     * @param id the id of the billDiscountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBillDiscount(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete BillDiscount : {}", id);
        return billDiscountService
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
