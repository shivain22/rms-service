package com.atparui.rmsservice.service;

import com.atparui.rmsservice.domain.Discount;
import com.atparui.rmsservice.repository.DiscountRepository;
import com.atparui.rmsservice.service.dto.DiscountDTO;
import com.atparui.rmsservice.service.dto.DiscountValidationDTO;
import com.atparui.rmsservice.service.dto.DiscountValidationRequestDTO;
import com.atparui.rmsservice.service.mapper.DiscountMapper;
import java.time.Instant;
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

    // jhipster-needle-service-impl-add-method - JHipster will add methods here

    /**
     * Find active discounts by restaurant ID
     *
     * @param restaurantId the restaurant ID
     * @return the list of active discount DTOs
     */
    @Transactional(readOnly = true)
    public Flux<DiscountDTO> findActiveByRestaurantId(UUID restaurantId) {
        LOG.debug("Request to find active Discounts by restaurant ID : {}", restaurantId);
        return discountRepository.findActiveByRestaurantId(restaurantId).map(discountMapper::toDto);
    }

    /**
     * Validate a discount code
     *
     * @param request the discount validation request
     * @return the discount validation result
     */
    @Transactional(readOnly = true)
    public Mono<DiscountValidationDTO> validateDiscount(DiscountValidationRequestDTO request) {
        LOG.debug("Request to validate discount : {}", request);
        return discountRepository
            .findByDiscountCodeAndRestaurantId(request.getDiscountCode(), request.getRestaurantId())
            .map(discount -> {
                DiscountValidationDTO validation = new DiscountValidationDTO();
                validation.setDiscountId(discount.getId());
                validation.setDiscountCode(discount.getDiscountCode());
                validation.setDiscountType(discount.getDiscountType());
                validation.setDiscountValue(discount.getDiscountValue());

                Instant now = Instant.now();
                boolean isValid =
                    discount.getIsActive() != null &&
                    discount.getIsActive() &&
                    discount.getValidFrom() != null &&
                    discount.getValidFrom().isBefore(now) &&
                    (discount.getValidTo() == null || discount.getValidTo().isAfter(now)) &&
                    (discount.getMaxUses() == null ||
                        discount.getCurrentUses() == null ||
                        discount.getCurrentUses() < discount.getMaxUses());

                validation.setIsValid(isValid);
                validation.setMessage(isValid ? "Discount is valid" : "Discount is not valid");
                return validation;
            })
            .switchIfEmpty(
                Mono.defer(() -> {
                    DiscountValidationDTO validation = new DiscountValidationDTO();
                    validation.setIsValid(false);
                    validation.setMessage("Discount code not found");
                    return Mono.just(validation);
                })
            );
    }
}
