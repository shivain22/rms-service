package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.BillItemRepository;
import com.atparui.rmsservice.service.dto.BillItemDTO;
import com.atparui.rmsservice.service.mapper.BillItemMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.BillItem}.
 */
@Service
@Transactional
public class BillItemService {

    private static final Logger LOG = LoggerFactory.getLogger(BillItemService.class);

    private final BillItemRepository billItemRepository;

    private final BillItemMapper billItemMapper;

    public BillItemService(BillItemRepository billItemRepository, BillItemMapper billItemMapper) {
        this.billItemRepository = billItemRepository;
        this.billItemMapper = billItemMapper;
    }

    /**
     * Save a billItem.
     *
     * @param billItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillItemDTO> save(BillItemDTO billItemDTO) {
        LOG.debug("Request to save BillItem : {}", billItemDTO);
        return billItemRepository.save(billItemMapper.toEntity(billItemDTO)).map(billItemMapper::toDto);
    }

    /**
     * Update a billItem.
     *
     * @param billItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillItemDTO> update(BillItemDTO billItemDTO) {
        LOG.debug("Request to update BillItem : {}", billItemDTO);
        return billItemRepository.save(billItemMapper.toEntity(billItemDTO).setIsPersisted()).map(billItemMapper::toDto);
    }

    /**
     * Partially update a billItem.
     *
     * @param billItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BillItemDTO> partialUpdate(BillItemDTO billItemDTO) {
        LOG.debug("Request to partially update BillItem : {}", billItemDTO);

        return billItemRepository
            .findById(billItemDTO.getId())
            .map(existingBillItem -> {
                billItemMapper.partialUpdate(existingBillItem, billItemDTO);

                return existingBillItem;
            })
            .flatMap(billItemRepository::save)
            .map(billItemMapper::toDto);
    }

    /**
     * Get all the billItems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BillItemDTO> findAll() {
        LOG.debug("Request to get all BillItems");
        return billItemRepository.findAll().map(billItemMapper::toDto);
    }

    /**
     * Returns the number of billItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return billItemRepository.count();
    }

    /**
     * Get one billItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BillItemDTO> findOne(UUID id) {
        LOG.debug("Request to get BillItem : {}", id);
        return billItemRepository.findById(id).map(billItemMapper::toDto);
    }

    /**
     * Delete the billItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete BillItem : {}", id);
        return billItemRepository.deleteById(id);
    }
}
