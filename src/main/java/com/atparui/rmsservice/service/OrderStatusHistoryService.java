package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.OrderStatusHistoryRepository;
import com.atparui.rmsservice.service.dto.OrderStatusHistoryDTO;
import com.atparui.rmsservice.service.mapper.OrderStatusHistoryMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.OrderStatusHistory}.
 */
@Service
@Transactional
public class OrderStatusHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStatusHistoryService.class);

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private final OrderStatusHistoryMapper orderStatusHistoryMapper;

    public OrderStatusHistoryService(
        OrderStatusHistoryRepository orderStatusHistoryRepository,
        OrderStatusHistoryMapper orderStatusHistoryMapper
    ) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderStatusHistoryMapper = orderStatusHistoryMapper;
    }

    /**
     * Save a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderStatusHistoryDTO> save(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        LOG.debug("Request to save OrderStatusHistory : {}", orderStatusHistoryDTO);
        return orderStatusHistoryRepository
            .save(orderStatusHistoryMapper.toEntity(orderStatusHistoryDTO))
            .map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Update a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderStatusHistoryDTO> update(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        LOG.debug("Request to update OrderStatusHistory : {}", orderStatusHistoryDTO);
        return orderStatusHistoryRepository
            .save(orderStatusHistoryMapper.toEntity(orderStatusHistoryDTO).setIsPersisted())
            .map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Partially update a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrderStatusHistoryDTO> partialUpdate(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        LOG.debug("Request to partially update OrderStatusHistory : {}", orderStatusHistoryDTO);

        return orderStatusHistoryRepository
            .findById(orderStatusHistoryDTO.getId())
            .map(existingOrderStatusHistory -> {
                orderStatusHistoryMapper.partialUpdate(existingOrderStatusHistory, orderStatusHistoryDTO);

                return existingOrderStatusHistory;
            })
            .flatMap(orderStatusHistoryRepository::save)
            .map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Get all the orderStatusHistories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderStatusHistoryDTO> findAll() {
        LOG.debug("Request to get all OrderStatusHistories");
        return orderStatusHistoryRepository.findAll().map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Returns the number of orderStatusHistories available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return orderStatusHistoryRepository.count();
    }

    /**
     * Get one orderStatusHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrderStatusHistoryDTO> findOne(UUID id) {
        LOG.debug("Request to get OrderStatusHistory : {}", id);
        return orderStatusHistoryRepository.findById(id).map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Delete the orderStatusHistory by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete OrderStatusHistory : {}", id);
        return orderStatusHistoryRepository.deleteById(id);
    }
}
