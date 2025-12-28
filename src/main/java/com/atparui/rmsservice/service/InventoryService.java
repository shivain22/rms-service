package com.atparui.rmsservice.service;

import com.atparui.rmsservice.domain.Inventory;
import com.atparui.rmsservice.repository.InventoryRepository;
import com.atparui.rmsservice.service.dto.InventoryDTO;
import com.atparui.rmsservice.service.dto.StockAdjustmentRequestDTO;
import com.atparui.rmsservice.service.dto.StockUpdateRequestDTO;
import com.atparui.rmsservice.service.mapper.InventoryMapper;
import java.math.BigDecimal;
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

    // jhipster-needle-service-impl-add-method - JHipster will add methods here

    /**
     * Find low stock items by branch ID
     *
     * @param branchId the branch ID
     * @return the list of low stock inventory DTOs
     */
    @Transactional(readOnly = true)
    public Flux<InventoryDTO> findLowStockByBranchId(UUID branchId) {
        LOG.debug("Request to find low stock Inventories by branch ID : {}", branchId);
        return inventoryRepository.findLowStockByBranchId(branchId).map(inventoryMapper::toDto);
    }

    /**
     * Update inventory stock level
     *
     * @param id the id of the inventory
     * @param request the stock update request
     * @return the updated inventory DTO
     */
    public Mono<InventoryDTO> updateStock(UUID id, StockUpdateRequestDTO request) {
        LOG.debug("Request to update Inventory stock : {} - {}", id, request);
        return inventoryRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Inventory not found")))
            .map(inventory -> {
                inventory.setCurrentStock(request.getCurrentStock());
                return inventory;
            })
            .flatMap(inventoryRepository::save)
            .map(inventoryMapper::toDto);
    }

    /**
     * Adjust inventory stock (add or remove quantity)
     *
     * @param id the id of the inventory
     * @param request the stock adjustment request
     * @return the updated inventory DTO
     */
    public Mono<InventoryDTO> adjustStock(UUID id, StockAdjustmentRequestDTO request) {
        LOG.debug("Request to adjust Inventory stock : {} - {}", id, request);
        return inventoryRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Inventory not found")))
            .map(inventory -> {
                BigDecimal currentStock = inventory.getCurrentStock();
                if (currentStock == null) {
                    currentStock = BigDecimal.ZERO;
                }
                if ("ADD".equals(request.getAdjustmentType())) {
                    inventory.setCurrentStock(currentStock.add(request.getAdjustmentQuantity()));
                } else if ("REMOVE".equals(request.getAdjustmentType())) {
                    inventory.setCurrentStock(currentStock.subtract(request.getAdjustmentQuantity()));
                }
                return inventory;
            })
            .flatMap(inventoryRepository::save)
            .map(inventoryMapper::toDto);
    }
}
