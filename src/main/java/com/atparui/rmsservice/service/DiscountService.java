package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.DiscountRepository;
import com.atparui.rmsservice.service.dto.DiscountDTO;
import com.atparui.rmsservice.service.mapper.DiscountMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Discount}.
 */
@Service
@Transactional
public class DiscountService {

    private static final Logger LOG = LoggerFactory.getLogger(DiscountService.class);

    private final DiscountRepository discountRepository;

    private final DiscountMapper discountMapper;

    public DiscountService(DiscountRepository discountRepository, DiscountMapper discountMapper) {
        this.discountRepository = discountRepository;
        this.discountMapper = discountMapper;
    }

    /**
     * Save a discount.
     *
     * @param discountDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DiscountDTO> save(DiscountDTO discountDTO) {
        LOG.debug("Request to save Discount : {}", discountDTO);
        return discountRepository.save(discountMapper.toEntity(discountDTO)).map(discountMapper::toDto);
    }

    /**
     * Update a discount.
     *
     * @param discountDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DiscountDTO> update(DiscountDTO discountDTO) {
        LOG.debug("Request to update Discount : {}", discountDTO);
        return discountRepository.save(discountMapper.toEntity(discountDTO).setIsPersisted()).map(discountMapper::toDto);
    }

    /**
     * Partially update a discount.
     *
     * @param discountDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<DiscountDTO> partialUpdate(DiscountDTO discountDTO) {
        LOG.debug("Request to partially update Discount : {}", discountDTO);

        return discountRepository
            .findById(discountDTO.getId())
            .map(existingDiscount -> {
                discountMapper.partialUpdate(existingDiscount, discountDTO);

                return existingDiscount;
            })
            .flatMap(discountRepository::save)
            .map(discountMapper::toDto);
    }

    /**
     * Get all the discounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DiscountDTO> findAll() {
        LOG.debug("Request to get all Discounts");
        return discountRepository.findAll().map(discountMapper::toDto);
    }

    /**
     * Returns the number of discounts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return discountRepository.count();
    }

    /**
     * Get one discount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DiscountDTO> findOne(UUID id) {
        LOG.debug("Request to get Discount : {}", id);
        return discountRepository.findById(id).map(discountMapper::toDto);
    }

    /**
     * Delete the discount by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Discount : {}", id);
        return discountRepository.deleteById(id);
    }
}
