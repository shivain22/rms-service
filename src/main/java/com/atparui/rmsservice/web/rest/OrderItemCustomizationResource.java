package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.OrderItemCustomizationRepository;
import com.atparui.rmsservice.service.OrderItemCustomizationService;
import com.atparui.rmsservice.service.dto.OrderItemCustomizationDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.OrderItemCustomization}.
 */
@RestController
@RequestMapping("/api/order-item-customizations")
public class OrderItemCustomizationResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemCustomizationResource.class);

    private static final String ENTITY_NAME = "rmsserviceOrderItemCustomization";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderItemCustomizationService orderItemCustomizationService;

    private final OrderItemCustomizationRepository orderItemCustomizationRepository;

    public OrderItemCustomizationResource(
        OrderItemCustomizationService orderItemCustomizationService,
        OrderItemCustomizationRepository orderItemCustomizationRepository
    ) {
        this.orderItemCustomizationService = orderItemCustomizationService;
        this.orderItemCustomizationRepository = orderItemCustomizationRepository;
    }

    /**
     * {@code POST  /order-item-customizations} : Create a new orderItemCustomization.
     *
     * @param orderItemCustomizationDTO the orderItemCustomizationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderItemCustomizationDTO, or with status {@code 400 (Bad Request)} if the orderItemCustomization has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OrderItemCustomizationDTO>> createOrderItemCustomization(
        @Valid @RequestBody OrderItemCustomizationDTO orderItemCustomizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save OrderItemCustomization : {}", orderItemCustomizationDTO);
        if (orderItemCustomizationDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderItemCustomization cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderItemCustomizationDTO.setId(UUID.randomUUID());
        return orderItemCustomizationService
            .save(orderItemCustomizationDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/order-item-customizations/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /order-item-customizations/:id} : Updates an existing orderItemCustomization.
     *
     * @param id the id of the orderItemCustomizationDTO to save.
     * @param orderItemCustomizationDTO the orderItemCustomizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemCustomizationDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemCustomizationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderItemCustomizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OrderItemCustomizationDTO>> updateOrderItemCustomization(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody OrderItemCustomizationDTO orderItemCustomizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OrderItemCustomization : {}, {}", id, orderItemCustomizationDTO);
        if (orderItemCustomizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemCustomizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderItemCustomizationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return orderItemCustomizationService
                    .update(orderItemCustomizationDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /order-item-customizations/:id} : Partial updates given fields of an existing orderItemCustomization, field will ignore if it is null
     *
     * @param id the id of the orderItemCustomizationDTO to save.
     * @param orderItemCustomizationDTO the orderItemCustomizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemCustomizationDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemCustomizationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderItemCustomizationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderItemCustomizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OrderItemCustomizationDTO>> partialUpdateOrderItemCustomization(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody OrderItemCustomizationDTO orderItemCustomizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OrderItemCustomization partially : {}, {}", id, orderItemCustomizationDTO);
        if (orderItemCustomizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemCustomizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderItemCustomizationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OrderItemCustomizationDTO> result = orderItemCustomizationService.partialUpdate(orderItemCustomizationDTO);

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
     * {@code GET  /order-item-customizations} : get all the orderItemCustomizations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderItemCustomizations in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<OrderItemCustomizationDTO>> getAllOrderItemCustomizations() {
        LOG.debug("REST request to get all OrderItemCustomizations");
        return orderItemCustomizationService.findAll().collectList();
    }

    /**
     * {@code GET  /order-item-customizations} : get all the orderItemCustomizations as a stream.
     * @return the {@link Flux} of orderItemCustomizations.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrderItemCustomizationDTO> getAllOrderItemCustomizationsAsStream() {
        LOG.debug("REST request to get all OrderItemCustomizations as a stream");
        return orderItemCustomizationService.findAll();
    }

    /**
     * {@code GET  /order-item-customizations/:id} : get the "id" orderItemCustomization.
     *
     * @param id the id of the orderItemCustomizationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderItemCustomizationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderItemCustomizationDTO>> getOrderItemCustomization(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get OrderItemCustomization : {}", id);
        Mono<OrderItemCustomizationDTO> orderItemCustomizationDTO = orderItemCustomizationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderItemCustomizationDTO);
    }

    /**
     * {@code DELETE  /order-item-customizations/:id} : delete the "id" orderItemCustomization.
     *
     * @param id the id of the orderItemCustomizationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOrderItemCustomization(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete OrderItemCustomization : {}", id);
        return orderItemCustomizationService
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
