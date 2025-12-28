package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.BillDiscountRepository;
import com.atparui.rmsservice.service.dto.BillDiscountDTO;
import com.atparui.rmsservice.service.mapper.BillDiscountMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.BillDiscount}.
 */
@Service
@Transactional
public class BillDiscountService {

    private static final Logger LOG = LoggerFactory.getLogger(BillDiscountService.class);

    private final BillDiscountRepository billDiscountRepository;

    private final BillDiscountMapper billDiscountMapper;

    public BillDiscountService(BillDiscountRepository billDiscountRepository, BillDiscountMapper billDiscountMapper) {
        this.billDiscountRepository = billDiscountRepository;
        this.billDiscountMapper = billDiscountMapper;
    }

    /**
     * Save a billDiscount.
     *
     * @param billDiscountDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillDiscountDTO> save(BillDiscountDTO billDiscountDTO) {
        LOG.debug("Request to save BillDiscount : {}", billDiscountDTO);
        return billDiscountRepository.save(billDiscountMapper.toEntity(billDiscountDTO)).map(billDiscountMapper::toDto);
    }

    /**
     * Update a billDiscount.
     *
     * @param billDiscountDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillDiscountDTO> update(BillDiscountDTO billDiscountDTO) {
        LOG.debug("Request to update BillDiscount : {}", billDiscountDTO);
        return billDiscountRepository.save(billDiscountMapper.toEntity(billDiscountDTO).setIsPersisted()).map(billDiscountMapper::toDto);
    }

    /**
     * Partially update a billDiscount.
     *
     * @param billDiscountDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BillDiscountDTO> partialUpdate(BillDiscountDTO billDiscountDTO) {
        LOG.debug("Request to partially update BillDiscount : {}", billDiscountDTO);

        return billDiscountRepository
            .findById(billDiscountDTO.getId())
            .map(existingBillDiscount -> {
                billDiscountMapper.partialUpdate(existingBillDiscount, billDiscountDTO);

                return existingBillDiscount;
            })
            .flatMap(billDiscountRepository::save)
            .map(billDiscountMapper::toDto);
    }

    /**
     * Get all the billDiscounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BillDiscountDTO> findAll() {
        LOG.debug("Request to get all BillDiscounts");
        return billDiscountRepository.findAll().map(billDiscountMapper::toDto);
    }

    /**
     * Returns the number of billDiscounts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return billDiscountRepository.count();
    }

    /**
     * Get one billDiscount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BillDiscountDTO> findOne(UUID id) {
        LOG.debug("Request to get BillDiscount : {}", id);
        return billDiscountRepository.findById(id).map(billDiscountMapper::toDto);
    }

    /**
     * Delete the billDiscount by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete BillDiscount : {}", id);
        return billDiscountRepository.deleteById(id);
    }
}
