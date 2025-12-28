package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.OrderStatusHistoryRepository;
import com.atparui.rmsservice.service.OrderStatusHistoryService;
import com.atparui.rmsservice.service.dto.OrderStatusHistoryDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.OrderStatusHistory}.
 */
@RestController
@RequestMapping("/api/order-status-histories")
public class OrderStatusHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStatusHistoryResource.class);

    private static final String ENTITY_NAME = "rmsserviceOrderStatusHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderStatusHistoryService orderStatusHistoryService;

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    public OrderStatusHistoryResource(
        OrderStatusHistoryService orderStatusHistoryService,
        OrderStatusHistoryRepository orderStatusHistoryRepository
    ) {
        this.orderStatusHistoryService = orderStatusHistoryService;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
    }

    /**
     * {@code POST  /order-status-histories} : Create a new orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the orderStatusHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderStatusHistoryDTO, or with status {@code 400 (Bad Request)} if the orderStatusHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OrderStatusHistoryDTO>> createOrderStatusHistory(
        @Valid @RequestBody OrderStatusHistoryDTO orderStatusHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save OrderStatusHistory : {}", orderStatusHistoryDTO);
        if (orderStatusHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderStatusHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderStatusHistoryDTO.setId(UUID.randomUUID());
        return orderStatusHistoryService
            .save(orderStatusHistoryDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/order-status-histories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /order-status-histories/:id} : Updates an existing orderStatusHistory.
     *
     * @param id the id of the orderStatusHistoryDTO to save.
     * @param orderStatusHistoryDTO the orderStatusHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderStatusHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the orderStatusHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderStatusHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OrderStatusHistoryDTO>> updateOrderStatusHistory(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody OrderStatusHistoryDTO orderStatusHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OrderStatusHistory : {}, {}", id, orderStatusHistoryDTO);
        if (orderStatusHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderStatusHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderStatusHistoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return orderStatusHistoryService
                    .update(orderStatusHistoryDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /order-status-histories/:id} : Partial updates given fields of an existing orderStatusHistory, field will ignore if it is null
     *
     * @param id the id of the orderStatusHistoryDTO to save.
     * @param orderStatusHistoryDTO the orderStatusHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderStatusHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the orderStatusHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderStatusHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderStatusHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OrderStatusHistoryDTO>> partialUpdateOrderStatusHistory(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody OrderStatusHistoryDTO orderStatusHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OrderStatusHistory partially : {}, {}", id, orderStatusHistoryDTO);
        if (orderStatusHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderStatusHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderStatusHistoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OrderStatusHistoryDTO> result = orderStatusHistoryService.partialUpdate(orderStatusHistoryDTO);

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
     * {@code GET  /order-status-histories} : get all the orderStatusHistories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderStatusHistories in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<OrderStatusHistoryDTO>> getAllOrderStatusHistories() {
        LOG.debug("REST request to get all OrderStatusHistories");
        return orderStatusHistoryService.findAll().collectList();
    }

    /**
     * {@code GET  /order-status-histories} : get all the orderStatusHistories as a stream.
     * @return the {@link Flux} of orderStatusHistories.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrderStatusHistoryDTO> getAllOrderStatusHistoriesAsStream() {
        LOG.debug("REST request to get all OrderStatusHistories as a stream");
        return orderStatusHistoryService.findAll();
    }

    /**
     * {@code GET  /order-status-histories/:id} : get the "id" orderStatusHistory.
     *
     * @param id the id of the orderStatusHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderStatusHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderStatusHistoryDTO>> getOrderStatusHistory(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get OrderStatusHistory : {}", id);
        Mono<OrderStatusHistoryDTO> orderStatusHistoryDTO = orderStatusHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderStatusHistoryDTO);
    }

    /**
     * {@code DELETE  /order-status-histories/:id} : delete the "id" orderStatusHistory.
     *
     * @param id the id of the orderStatusHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOrderStatusHistory(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete OrderStatusHistory : {}", id);
        return orderStatusHistoryService
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
