package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.CustomerLoyaltyRepository;
import com.atparui.rmsservice.service.CustomerLoyaltyService;
import com.atparui.rmsservice.service.dto.CustomerLoyaltyDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.CustomerLoyalty}.
 */
@RestController
@RequestMapping("/api/customer-loyalties")
public class CustomerLoyaltyResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerLoyaltyResource.class);

    private static final String ENTITY_NAME = "rmsserviceCustomerLoyalty";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerLoyaltyService customerLoyaltyService;

    private final CustomerLoyaltyRepository customerLoyaltyRepository;

    public CustomerLoyaltyResource(CustomerLoyaltyService customerLoyaltyService, CustomerLoyaltyRepository customerLoyaltyRepository) {
        this.customerLoyaltyService = customerLoyaltyService;
        this.customerLoyaltyRepository = customerLoyaltyRepository;
    }

    /**
     * {@code POST  /customer-loyalties} : Create a new customerLoyalty.
     *
     * @param customerLoyaltyDTO the customerLoyaltyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerLoyaltyDTO, or with status {@code 400 (Bad Request)} if the customerLoyalty has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<CustomerLoyaltyDTO>> createCustomerLoyalty(@Valid @RequestBody CustomerLoyaltyDTO customerLoyaltyDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CustomerLoyalty : {}", customerLoyaltyDTO);
        if (customerLoyaltyDTO.getId() != null) {
            throw new BadRequestAlertException("A new customerLoyalty cannot already have an ID", ENTITY_NAME, "idexists");
        }
        customerLoyaltyDTO.setId(UUID.randomUUID());
        return customerLoyaltyService
            .save(customerLoyaltyDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/customer-loyalties/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /customer-loyalties/:id} : Updates an existing customerLoyalty.
     *
     * @param id the id of the customerLoyaltyDTO to save.
     * @param customerLoyaltyDTO the customerLoyaltyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerLoyaltyDTO,
     * or with status {@code 400 (Bad Request)} if the customerLoyaltyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerLoyaltyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CustomerLoyaltyDTO>> updateCustomerLoyalty(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody CustomerLoyaltyDTO customerLoyaltyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CustomerLoyalty : {}, {}", id, customerLoyaltyDTO);
        if (customerLoyaltyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerLoyaltyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerLoyaltyRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return customerLoyaltyService
                    .update(customerLoyaltyDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /customer-loyalties/:id} : Partial updates given fields of an existing customerLoyalty, field will ignore if it is null
     *
     * @param id the id of the customerLoyaltyDTO to save.
     * @param customerLoyaltyDTO the customerLoyaltyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerLoyaltyDTO,
     * or with status {@code 400 (Bad Request)} if the customerLoyaltyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customerLoyaltyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customerLoyaltyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CustomerLoyaltyDTO>> partialUpdateCustomerLoyalty(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody CustomerLoyaltyDTO customerLoyaltyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CustomerLoyalty partially : {}, {}", id, customerLoyaltyDTO);
        if (customerLoyaltyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerLoyaltyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerLoyaltyRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CustomerLoyaltyDTO> result = customerLoyaltyService.partialUpdate(customerLoyaltyDTO);

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
     * {@code GET  /customer-loyalties} : get all the customerLoyalties.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customerLoyalties in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<CustomerLoyaltyDTO>> getAllCustomerLoyalties() {
        LOG.debug("REST request to get all CustomerLoyalties");
        return customerLoyaltyService.findAll().collectList();
    }

    /**
     * {@code GET  /customer-loyalties} : get all the customerLoyalties as a stream.
     * @return the {@link Flux} of customerLoyalties.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CustomerLoyaltyDTO> getAllCustomerLoyaltiesAsStream() {
        LOG.debug("REST request to get all CustomerLoyalties as a stream");
        return customerLoyaltyService.findAll();
    }

    /**
     * {@code GET  /customer-loyalties/:id} : get the "id" customerLoyalty.
     *
     * @param id the id of the customerLoyaltyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerLoyaltyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CustomerLoyaltyDTO>> getCustomerLoyalty(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get CustomerLoyalty : {}", id);
        Mono<CustomerLoyaltyDTO> customerLoyaltyDTO = customerLoyaltyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerLoyaltyDTO);
    }

    /**
     * {@code DELETE  /customer-loyalties/:id} : delete the "id" customerLoyalty.
     *
     * @param id the id of the customerLoyaltyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomerLoyalty(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete CustomerLoyalty : {}", id);
        return customerLoyaltyService
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
