package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.InventoryRepository;
import com.atparui.rmsservice.service.dto.InventoryDTO;
import com.atparui.rmsservice.service.mapper.InventoryMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Inventory}.
 */
@Service
@Transactional
public class InventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    /**
     * Save a inventory.
     *
     * @param inventoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<InventoryDTO> save(InventoryDTO inventoryDTO) {
        LOG.debug("Request to save Inventory : {}", inventoryDTO);
        return inventoryRepository.save(inventoryMapper.toEntity(inventoryDTO)).map(inventoryMapper::toDto);
    }

    /**
     * Update a inventory.
     *
     * @param inventoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<InventoryDTO> update(InventoryDTO inventoryDTO) {
        LOG.debug("Request to update Inventory : {}", inventoryDTO);
        return inventoryRepository.save(inventoryMapper.toEntity(inventoryDTO).setIsPersisted()).map(inventoryMapper::toDto);
    }

    /**
     * Partially update a inventory.
     *
     * @param inventoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<InventoryDTO> partialUpdate(InventoryDTO inventoryDTO) {
        LOG.debug("Request to partially update Inventory : {}", inventoryDTO);

        return inventoryRepository
            .findById(inventoryDTO.getId())
            .map(existingInventory -> {
                inventoryMapper.partialUpdate(existingInventory, inventoryDTO);

                return existingInventory;
            })
            .flatMap(inventoryRepository::save)
            .map(inventoryMapper::toDto);
    }

    /**
     * Get all the inventories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<InventoryDTO> findAll() {
        LOG.debug("Request to get all Inventories");
        return inventoryRepository.findAll().map(inventoryMapper::toDto);
    }

    /**
     * Returns the number of inventories available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return inventoryRepository.count();
    }

    /**
     * Get one inventory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<InventoryDTO> findOne(UUID id) {
        LOG.debug("Request to get Inventory : {}", id);
        return inventoryRepository.findById(id).map(inventoryMapper::toDto);
    }

    /**
     * Delete the inventory by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Inventory : {}", id);
        return inventoryRepository.deleteById(id);
    }
}
