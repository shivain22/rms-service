package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.OrderItemCustomizationRepository;
import com.atparui.rmsservice.service.dto.OrderItemCustomizationDTO;
import com.atparui.rmsservice.service.mapper.OrderItemCustomizationMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.OrderItemCustomization}.
 */
@Service
@Transactional
public class OrderItemCustomizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemCustomizationService.class);

    private final OrderItemCustomizationRepository orderItemCustomizationRepository;

    private final OrderItemCustomizationMapper orderItemCustomizationMapper;

    public OrderItemCustomizationService(
        OrderItemCustomizationRepository orderItemCustomizationRepository,
        OrderItemCustomizationMapper orderItemCustomizationMapper
    ) {
        this.orderItemCustomizationRepository = orderItemCustomizationRepository;
        this.orderItemCustomizationMapper = orderItemCustomizationMapper;
    }

    /**
     * Save a orderItemCustomization.
     *
     * @param orderItemCustomizationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderItemCustomizationDTO> save(OrderItemCustomizationDTO orderItemCustomizationDTO) {
        LOG.debug("Request to save OrderItemCustomization : {}", orderItemCustomizationDTO);
        return orderItemCustomizationRepository
            .save(orderItemCustomizationMapper.toEntity(orderItemCustomizationDTO))
            .map(orderItemCustomizationMapper::toDto);
    }

    /**
     * Update a orderItemCustomization.
     *
     * @param orderItemCustomizationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderItemCustomizationDTO> update(OrderItemCustomizationDTO orderItemCustomizationDTO) {
        LOG.debug("Request to update OrderItemCustomization : {}", orderItemCustomizationDTO);
        return orderItemCustomizationRepository
            .save(orderItemCustomizationMapper.toEntity(orderItemCustomizationDTO).setIsPersisted())
            .map(orderItemCustomizationMapper::toDto);
    }

    /**
     * Partially update a orderItemCustomization.
     *
     * @param orderItemCustomizationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrderItemCustomizationDTO> partialUpdate(OrderItemCustomizationDTO orderItemCustomizationDTO) {
        LOG.debug("Request to partially update OrderItemCustomization : {}", orderItemCustomizationDTO);

        return orderItemCustomizationRepository
            .findById(orderItemCustomizationDTO.getId())
            .map(existingOrderItemCustomization -> {
                orderItemCustomizationMapper.partialUpdate(existingOrderItemCustomization, orderItemCustomizationDTO);

                return existingOrderItemCustomization;
            })
            .flatMap(orderItemCustomizationRepository::save)
            .map(orderItemCustomizationMapper::toDto);
    }

    /**
     * Get all the orderItemCustomizations.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderItemCustomizationDTO> findAll() {
        LOG.debug("Request to get all OrderItemCustomizations");
        return orderItemCustomizationRepository.findAll().map(orderItemCustomizationMapper::toDto);
    }

    /**
     * Returns the number of orderItemCustomizations available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return orderItemCustomizationRepository.count();
    }

    /**
     * Get one orderItemCustomization by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrderItemCustomizationDTO> findOne(UUID id) {
        LOG.debug("Request to get OrderItemCustomization : {}", id);
        return orderItemCustomizationRepository.findById(id).map(orderItemCustomizationMapper::toDto);
    }

    /**
     * Delete the orderItemCustomization by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete OrderItemCustomization : {}", id);
        return orderItemCustomizationRepository.deleteById(id);
    }
}
