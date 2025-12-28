package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.BillRepository;
import com.atparui.rmsservice.service.BillService;
import com.atparui.rmsservice.service.dto.BillDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.Bill}.
 */
@RestController
@RequestMapping("/api/bills")
public class BillResource {

    private static final Logger LOG = LoggerFactory.getLogger(BillResource.class);

    private static final String ENTITY_NAME = "rmsserviceBill";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BillService billService;

    private final BillRepository billRepository;

    public BillResource(BillService billService, BillRepository billRepository) {
        this.billService = billService;
        this.billRepository = billRepository;
    }

    /**
     * {@code POST  /bills} : Create a new bill.
     *
     * @param billDTO the billDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new billDTO, or with status {@code 400 (Bad Request)} if the bill has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<BillDTO>> createBill(@Valid @RequestBody BillDTO billDTO) throws URISyntaxException {
        LOG.debug("REST request to save Bill : {}", billDTO);
        if (billDTO.getId() != null) {
            throw new BadRequestAlertException("A new bill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        billDTO.setId(UUID.randomUUID());
        return billService
            .save(billDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/bills/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /bills/:id} : Updates an existing bill.
     *
     * @param id the id of the billDTO to save.
     * @param billDTO the billDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billDTO,
     * or with status {@code 400 (Bad Request)} if the billDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the billDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<BillDTO>> updateBill(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody BillDTO billDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Bill : {}, {}", id, billDTO);
        if (billDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return billRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return billService
                    .update(billDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /bills/:id} : Partial updates given fields of an existing bill, field will ignore if it is null
     *
     * @param id the id of the billDTO to save.
     * @param billDTO the billDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billDTO,
     * or with status {@code 400 (Bad Request)} if the billDTO is not valid,
     * or with status {@code 404 (Not Found)} if the billDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the billDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<BillDTO>> partialUpdateBill(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody BillDTO billDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Bill partially : {}, {}", id, billDTO);
        if (billDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return billRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BillDTO> result = billService.partialUpdate(billDTO);

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
     * {@code GET  /bills} : get all the bills.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bills in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<BillDTO>>> getAllBills(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Bills");
        return billService
            .countAll()
            .zipWith(billService.findAll(pageable).collectList())
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
     * {@code GET  /bills/:id} : get the "id" bill.
     *
     * @param id the id of the billDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the billDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<BillDTO>> getBill(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get Bill : {}", id);
        Mono<BillDTO> billDTO = billService.findOne(id);
        return ResponseUtil.wrapOrNotFound(billDTO);
    }

    /**
     * {@code DELETE  /bills/:id} : delete the "id" bill.
     *
     * @param id the id of the billDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBill(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete Bill : {}", id);
        return billService
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
     * {@code SEARCH  /bills/_search?query=:query} : search for the bill corresponding
     * to the query.
     *
     * @param query the query of the bill search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<BillDTO>>> searchBills(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of Bills for query {}", query);
        return billService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(billService.search(query, pageable)));
    }

    // jhipster-needle-rest-add-get-method - JHipster will add get methods here

    /**
     * {@code GET /api/bills/{id}/breakdown} : Get bill with breakdown
     *
     * @param id the id of the bill
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and bill breakdown DTO
     */
    @GetMapping("/{id}/breakdown")
    public Mono<ResponseEntity<com.atparui.rmsservice.service.dto.BillBreakdownDTO>> getBillBreakdown(@PathVariable UUID id) {
        LOG.debug("REST request to get bill breakdown : {}", id);
        return billService
            .getBillBreakdown(id)
            .map(result -> ResponseEntity.ok().body(result))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * {@code GET /api/bills/branch/{branchId}/date-range} : Get bills by date range
     *
     * @param branchId the branch ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and list of bills
     */
    @GetMapping("/branch/{branchId}/date-range")
    public Mono<ResponseEntity<List<BillDTO>>> getBillsByDateRange(
        @PathVariable UUID branchId,
        @RequestParam @org.springframework.format.annotation.DateTimeFormat(
            iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME
        ) java.time.Instant startDate,
        @RequestParam @org.springframework.format.annotation.DateTimeFormat(
            iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME
        ) java.time.Instant endDate
    ) {
        LOG.debug("REST request to get bills by date range : {} - {} to {}", branchId, startDate, endDate);
        return billService
            .findByBranchIdAndDateRange(branchId, startDate, endDate)
            .collectList()
            .map(result -> ResponseEntity.ok().body(result));
    }

    // jhipster-needle-rest-add-post-method - JHipster will add post methods here

    /**
     * {@code POST /api/bills/generate} : Generate bill from order
     *
     * @param request the bill generation request
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and bill DTO
     */
    @PostMapping("/generate")
    public Mono<ResponseEntity<BillDTO>> generateBill(
        @Valid @RequestBody com.atparui.rmsservice.service.dto.BillGenerationRequestDTO request
    ) {
        LOG.debug("REST request to generate bill : {}", request);
        return billService.generateBillFromOrder(request).map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }

    /**
     * {@code POST /api/bills/{id}/apply-discount} : Apply discount to bill
     *
     * @param id the id of the bill
     * @param request the discount application request
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and updated bill DTO
     */
    @PostMapping("/{id}/apply-discount")
    public Mono<ResponseEntity<BillDTO>> applyDiscount(
        @PathVariable UUID id,
        @Valid @RequestBody com.atparui.rmsservice.service.dto.DiscountApplicationRequestDTO request
    ) {
        LOG.debug("REST request to apply discount to bill : {} - {}", id, request);
        return billService.applyDiscount(id, request).map(result -> ResponseEntity.ok().body(result));
    }
}
