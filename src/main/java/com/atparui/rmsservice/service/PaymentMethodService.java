package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.PaymentMethodRepository;
import com.atparui.rmsservice.service.dto.PaymentMethodDTO;
import com.atparui.rmsservice.service.mapper.PaymentMethodMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.PaymentMethod}.
 */
@Service
@Transactional
public class PaymentMethodService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentMethodService.class);

    private final PaymentMethodRepository paymentMethodRepository;

    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository, PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    /**
     * Save a paymentMethod.
     *
     * @param paymentMethodDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentMethodDTO> save(PaymentMethodDTO paymentMethodDTO) {
        LOG.debug("Request to save PaymentMethod : {}", paymentMethodDTO);
        return paymentMethodRepository.save(paymentMethodMapper.toEntity(paymentMethodDTO)).map(paymentMethodMapper::toDto);
    }

    /**
     * Update a paymentMethod.
     *
     * @param paymentMethodDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentMethodDTO> update(PaymentMethodDTO paymentMethodDTO) {
        LOG.debug("Request to update PaymentMethod : {}", paymentMethodDTO);
        return paymentMethodRepository
            .save(paymentMethodMapper.toEntity(paymentMethodDTO).setIsPersisted())
            .map(paymentMethodMapper::toDto);
    }

    /**
     * Partially update a paymentMethod.
     *
     * @param paymentMethodDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PaymentMethodDTO> partialUpdate(PaymentMethodDTO paymentMethodDTO) {
        LOG.debug("Request to partially update PaymentMethod : {}", paymentMethodDTO);

        return paymentMethodRepository
            .findById(paymentMethodDTO.getId())
            .map(existingPaymentMethod -> {
                paymentMethodMapper.partialUpdate(existingPaymentMethod, paymentMethodDTO);

                return existingPaymentMethod;
            })
            .flatMap(paymentMethodRepository::save)
            .map(paymentMethodMapper::toDto);
    }

    /**
     * Get all the paymentMethods.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaymentMethodDTO> findAll() {
        LOG.debug("Request to get all PaymentMethods");
        return paymentMethodRepository.findAll().map(paymentMethodMapper::toDto);
    }

    /**
     * Returns the number of paymentMethods available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return paymentMethodRepository.count();
    }

    /**
     * Get one paymentMethod by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PaymentMethodDTO> findOne(UUID id) {
        LOG.debug("Request to get PaymentMethod : {}", id);
        return paymentMethodRepository.findById(id).map(paymentMethodMapper::toDto);
    }

    /**
     * Delete the paymentMethod by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete PaymentMethod : {}", id);
        return paymentMethodRepository.deleteById(id);
    }
}
