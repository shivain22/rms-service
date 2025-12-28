package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.repository.PaymentMethodRepository;
import com.atparui.rmsservice.service.PaymentMethodService;
import com.atparui.rmsservice.service.dto.PaymentMethodDTO;
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
 * REST controller for managing {@link com.atparui.rmsservice.domain.PaymentMethod}.
 */
@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentMethodResource.class);

    private static final String ENTITY_NAME = "rmsservicePaymentMethod";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentMethodService paymentMethodService;

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodResource(PaymentMethodService paymentMethodService, PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodService = paymentMethodService;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    /**
     * {@code POST  /payment-methods} : Create a new paymentMethod.
     *
     * @param paymentMethodDTO the paymentMethodDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentMethodDTO, or with status {@code 400 (Bad Request)} if the paymentMethod has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PaymentMethodDTO>> createPaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PaymentMethod : {}", paymentMethodDTO);
        if (paymentMethodDTO.getId() != null) {
            throw new BadRequestAlertException("A new paymentMethod cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paymentMethodDTO.setId(UUID.randomUUID());
        return paymentMethodService
            .save(paymentMethodDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/payment-methods/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /payment-methods/:id} : Updates an existing paymentMethod.
     *
     * @param id the id of the paymentMethodDTO to save.
     * @param paymentMethodDTO the paymentMethodDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentMethodDTO,
     * or with status {@code 400 (Bad Request)} if the paymentMethodDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentMethodDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PaymentMethodDTO>> updatePaymentMethod(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody PaymentMethodDTO paymentMethodDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PaymentMethod : {}, {}", id, paymentMethodDTO);
        if (paymentMethodDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentMethodDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paymentMethodRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return paymentMethodService
                    .update(paymentMethodDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /payment-methods/:id} : Partial updates given fields of an existing paymentMethod, field will ignore if it is null
     *
     * @param id the id of the paymentMethodDTO to save.
     * @param paymentMethodDTO the paymentMethodDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentMethodDTO,
     * or with status {@code 400 (Bad Request)} if the paymentMethodDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paymentMethodDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paymentMethodDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PaymentMethodDTO>> partialUpdatePaymentMethod(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody PaymentMethodDTO paymentMethodDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PaymentMethod partially : {}, {}", id, paymentMethodDTO);
        if (paymentMethodDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentMethodDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paymentMethodRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PaymentMethodDTO> result = paymentMethodService.partialUpdate(paymentMethodDTO);

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
     * {@code GET  /payment-methods} : get all the paymentMethods.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentMethods in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<PaymentMethodDTO>> getAllPaymentMethods() {
        LOG.debug("REST request to get all PaymentMethods");
        return paymentMethodService.findAll().collectList();
    }

    /**
     * {@code GET  /payment-methods} : get all the paymentMethods as a stream.
     * @return the {@link Flux} of paymentMethods.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PaymentMethodDTO> getAllPaymentMethodsAsStream() {
        LOG.debug("REST request to get all PaymentMethods as a stream");
        return paymentMethodService.findAll();
    }

    /**
     * {@code GET  /payment-methods/:id} : get the "id" paymentMethod.
     *
     * @param id the id of the paymentMethodDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentMethodDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PaymentMethodDTO>> getPaymentMethod(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get PaymentMethod : {}", id);
        Mono<PaymentMethodDTO> paymentMethodDTO = paymentMethodService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentMethodDTO);
    }

    /**
     * {@code DELETE  /payment-methods/:id} : delete the "id" paymentMethod.
     *
     * @param id the id of the paymentMethodDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePaymentMethod(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete PaymentMethod : {}", id);
        return paymentMethodService
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
