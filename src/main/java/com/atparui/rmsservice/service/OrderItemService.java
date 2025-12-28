package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.OrderItemRepository;
import com.atparui.rmsservice.service.dto.OrderItemDTO;
import com.atparui.rmsservice.service.mapper.OrderItemMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.OrderItem}.
 */
@Service
@Transactional
public class OrderItemService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderItemService.class);

    private final OrderItemRepository orderItemRepository;

    private final OrderItemMapper orderItemMapper;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * Save a orderItem.
     *
     * @param orderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderItemDTO> save(OrderItemDTO orderItemDTO) {
        LOG.debug("Request to save OrderItem : {}", orderItemDTO);
        return orderItemRepository.save(orderItemMapper.toEntity(orderItemDTO)).map(orderItemMapper::toDto);
    }

    /**
     * Update a orderItem.
     *
     * @param orderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderItemDTO> update(OrderItemDTO orderItemDTO) {
        LOG.debug("Request to update OrderItem : {}", orderItemDTO);
        return orderItemRepository.save(orderItemMapper.toEntity(orderItemDTO).setIsPersisted()).map(orderItemMapper::toDto);
    }

    /**
     * Partially update a orderItem.
     *
     * @param orderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrderItemDTO> partialUpdate(OrderItemDTO orderItemDTO) {
        LOG.debug("Request to partially update OrderItem : {}", orderItemDTO);

        return orderItemRepository
            .findById(orderItemDTO.getId())
            .map(existingOrderItem -> {
                orderItemMapper.partialUpdate(existingOrderItem, orderItemDTO);

                return existingOrderItem;
            })
            .flatMap(orderItemRepository::save)
            .map(orderItemMapper::toDto);
    }

    /**
     * Get all the orderItems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderItemDTO> findAll() {
        LOG.debug("Request to get all OrderItems");
        return orderItemRepository.findAll().map(orderItemMapper::toDto);
    }

    /**
     * Returns the number of orderItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return orderItemRepository.count();
    }

    /**
     * Get one orderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrderItemDTO> findOne(UUID id) {
        LOG.debug("Request to get OrderItem : {}", id);
        return orderItemRepository.findById(id).map(orderItemMapper::toDto);
    }

    /**
     * Delete the orderItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete OrderItem : {}", id);
        return orderItemRepository.deleteById(id);
    }
}
