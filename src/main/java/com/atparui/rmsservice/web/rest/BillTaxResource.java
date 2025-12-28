package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.BillTaxRepository;
import com.atparui.rmsservice.service.BillTaxService;
import com.atparui.rmsservice.service.dto.BillTaxDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.BillTax}.
 */
@RestController
@RequestMapping("/api/bill-taxes")
public class BillTaxResource {

    private static final Logger LOG = LoggerFactory.getLogger(BillTaxResource.class);

    private static final String ENTITY_NAME = "rmsserviceBillTax";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BillTaxService billTaxService;

    private final BillTaxRepository billTaxRepository;

    public BillTaxResource(BillTaxService billTaxService, BillTaxRepository billTaxRepository) {
        this.billTaxService = billTaxService;
        this.billTaxRepository = billTaxRepository;
    }

    /**
     * {@code POST  /bill-taxes} : Create a new billTax.
     *
     * @param billTaxDTO the billTaxDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new billTaxDTO, or with status {@code 400 (Bad Request)} if the billTax has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<BillTaxDTO>> createBillTax(@Valid @RequestBody BillTaxDTO billTaxDTO) throws URISyntaxException {
        LOG.debug("REST request to save BillTax : {}", billTaxDTO);
        if (billTaxDTO.getId() != null) {
            throw new BadRequestAlertException("A new billTax cannot already have an ID", ENTITY_NAME, "idexists");
        }
        billTaxDTO.setId(UUID.randomUUID());
        return billTaxService
            .save(billTaxDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/bill-taxes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /bill-taxes/:id} : Updates an existing billTax.
     *
     * @param id the id of the billTaxDTO to save.
     * @param billTaxDTO the billTaxDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billTaxDTO,
     * or with status {@code 400 (Bad Request)} if the billTaxDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the billTaxDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<BillTaxDTO>> updateBillTax(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody BillTaxDTO billTaxDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BillTax : {}, {}", id, billTaxDTO);
        if (billTaxDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billTaxDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return billTaxRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return billTaxService
                    .update(billTaxDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /bill-taxes/:id} : Partial updates given fields of an existing billTax, field will ignore if it is null
     *
     * @param id the id of the billTaxDTO to save.
     * @param billTaxDTO the billTaxDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billTaxDTO,
     * or with status {@code 400 (Bad Request)} if the billTaxDTO is not valid,
     * or with status {@code 404 (Not Found)} if the billTaxDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the billTaxDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<BillTaxDTO>> partialUpdateBillTax(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody BillTaxDTO billTaxDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BillTax partially : {}, {}", id, billTaxDTO);
        if (billTaxDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billTaxDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return billTaxRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BillTaxDTO> result = billTaxService.partialUpdate(billTaxDTO);

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
     * {@code GET  /bill-taxes} : get all the billTaxes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of billTaxes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<BillTaxDTO>> getAllBillTaxes() {
        LOG.debug("REST request to get all BillTaxes");
        return billTaxService.findAll().collectList();
    }

    /**
     * {@code GET  /bill-taxes} : get all the billTaxes as a stream.
     * @return the {@link Flux} of billTaxes.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<BillTaxDTO> getAllBillTaxesAsStream() {
        LOG.debug("REST request to get all BillTaxes as a stream");
        return billTaxService.findAll();
    }

    /**
     * {@code GET  /bill-taxes/:id} : get the "id" billTax.
     *
     * @param id the id of the billTaxDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the billTaxDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<BillTaxDTO>> getBillTax(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get BillTax : {}", id);
        Mono<BillTaxDTO> billTaxDTO = billTaxService.findOne(id);
        return ResponseUtil.wrapOrNotFound(billTaxDTO);
    }

    /**
     * {@code DELETE  /bill-taxes/:id} : delete the "id" billTax.
     *
     * @param id the id of the billTaxDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBillTax(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete BillTax : {}", id);
        return billTaxService
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
