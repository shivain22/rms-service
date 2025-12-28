package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.PaymentRepository;
import com.atparui.rmsservice.repository.search.PaymentSearchRepository;
import com.atparui.rmsservice.service.dto.PaymentDTO;
import com.atparui.rmsservice.service.mapper.PaymentMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Payment}.
 */
@Service
@Transactional
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final PaymentSearchRepository paymentSearchRepository;

    public PaymentService(
        PaymentRepository paymentRepository,
        PaymentMapper paymentMapper,
        PaymentSearchRepository paymentSearchRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.paymentSearchRepository = paymentSearchRepository;
    }

    /**
     * Save a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentDTO> save(PaymentDTO paymentDTO) {
        LOG.debug("Request to save Payment : {}", paymentDTO);
        return paymentRepository.save(paymentMapper.toEntity(paymentDTO)).flatMap(paymentSearchRepository::save).map(paymentMapper::toDto);
    }

    /**
     * Update a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentDTO> update(PaymentDTO paymentDTO) {
        LOG.debug("Request to update Payment : {}", paymentDTO);
        return paymentRepository
            .save(paymentMapper.toEntity(paymentDTO).setIsPersisted())
            .flatMap(paymentSearchRepository::save)
            .map(paymentMapper::toDto);
    }

    /**
     * Partially update a payment.
     *
     * @param paymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PaymentDTO> partialUpdate(PaymentDTO paymentDTO) {
        LOG.debug("Request to partially update Payment : {}", paymentDTO);

        return paymentRepository
            .findById(paymentDTO.getId())
            .map(existingPayment -> {
                paymentMapper.partialUpdate(existingPayment, paymentDTO);

                return existingPayment;
            })
            .flatMap(paymentRepository::save)
            .flatMap(savedPayment -> {
                paymentSearchRepository.save(savedPayment);
                return Mono.just(savedPayment);
            })
            .map(paymentMapper::toDto);
    }

    /**
     * Get all the payments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaymentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Payments");
        return paymentRepository.findAllBy(pageable).map(paymentMapper::toDto);
    }

    /**
     * Returns the number of payments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return paymentRepository.count();
    }

    /**
     * Returns the number of payments available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return paymentSearchRepository.count();
    }

    /**
     * Get one payment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PaymentDTO> findOne(UUID id) {
        LOG.debug("Request to get Payment : {}", id);
        return paymentRepository.findById(id).map(paymentMapper::toDto);
    }

    /**
     * Delete the payment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Payment : {}", id);
        return paymentRepository.deleteById(id).then(paymentSearchRepository.deleteById(id));
    }

    /**
     * Search for the payment corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaymentDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Payments for query {}", query);
        return paymentSearchRepository.search(query, pageable).map(paymentMapper::toDto);
    }
}
