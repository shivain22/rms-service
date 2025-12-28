package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.BillTaxRepository;
import com.atparui.rmsservice.service.dto.BillTaxDTO;
import com.atparui.rmsservice.service.mapper.BillTaxMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.BillTax}.
 */
@Service
@Transactional
public class BillTaxService {

    private static final Logger LOG = LoggerFactory.getLogger(BillTaxService.class);

    private final BillTaxRepository billTaxRepository;

    private final BillTaxMapper billTaxMapper;

    public BillTaxService(BillTaxRepository billTaxRepository, BillTaxMapper billTaxMapper) {
        this.billTaxRepository = billTaxRepository;
        this.billTaxMapper = billTaxMapper;
    }

    /**
     * Save a billTax.
     *
     * @param billTaxDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillTaxDTO> save(BillTaxDTO billTaxDTO) {
        LOG.debug("Request to save BillTax : {}", billTaxDTO);
        return billTaxRepository.save(billTaxMapper.toEntity(billTaxDTO)).map(billTaxMapper::toDto);
    }

    /**
     * Update a billTax.
     *
     * @param billTaxDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillTaxDTO> update(BillTaxDTO billTaxDTO) {
        LOG.debug("Request to update BillTax : {}", billTaxDTO);
        return billTaxRepository.save(billTaxMapper.toEntity(billTaxDTO).setIsPersisted()).map(billTaxMapper::toDto);
    }

    /**
     * Partially update a billTax.
     *
     * @param billTaxDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BillTaxDTO> partialUpdate(BillTaxDTO billTaxDTO) {
        LOG.debug("Request to partially update BillTax : {}", billTaxDTO);

        return billTaxRepository
            .findById(billTaxDTO.getId())
            .map(existingBillTax -> {
                billTaxMapper.partialUpdate(existingBillTax, billTaxDTO);

                return existingBillTax;
            })
            .flatMap(billTaxRepository::save)
            .map(billTaxMapper::toDto);
    }

    /**
     * Get all the billTaxes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BillTaxDTO> findAll() {
        LOG.debug("Request to get all BillTaxes");
        return billTaxRepository.findAll().map(billTaxMapper::toDto);
    }

    /**
     * Returns the number of billTaxes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return billTaxRepository.count();
    }

    /**
     * Get one billTax by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BillTaxDTO> findOne(UUID id) {
        LOG.debug("Request to get BillTax : {}", id);
        return billTaxRepository.findById(id).map(billTaxMapper::toDto);
    }

    /**
     * Delete the billTax by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete BillTax : {}", id);
        return billTaxRepository.deleteById(id);
    }
}
