package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.BranchTableRepository;
import com.atparui.rmsservice.repository.search.BranchTableSearchRepository;
import com.atparui.rmsservice.service.dto.BranchTableDTO;
import com.atparui.rmsservice.service.mapper.BranchTableMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.BranchTable}.
 */
@Service
@Transactional
public class BranchTableService {

    private static final Logger LOG = LoggerFactory.getLogger(BranchTableService.class);

    private final BranchTableRepository branchTableRepository;

    private final BranchTableMapper branchTableMapper;

    private final BranchTableSearchRepository branchTableSearchRepository;

    public BranchTableService(
        BranchTableRepository branchTableRepository,
        BranchTableMapper branchTableMapper,
        BranchTableSearchRepository branchTableSearchRepository
    ) {
        this.branchTableRepository = branchTableRepository;
        this.branchTableMapper = branchTableMapper;
        this.branchTableSearchRepository = branchTableSearchRepository;
    }

    /**
     * Save a branchTable.
     *
     * @param branchTableDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BranchTableDTO> save(BranchTableDTO branchTableDTO) {
        LOG.debug("Request to save BranchTable : {}", branchTableDTO);
        return branchTableRepository
            .save(branchTableMapper.toEntity(branchTableDTO))
            .flatMap(branchTableSearchRepository::save)
            .map(branchTableMapper::toDto);
    }

    /**
     * Update a branchTable.
     *
     * @param branchTableDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BranchTableDTO> update(BranchTableDTO branchTableDTO) {
        LOG.debug("Request to update BranchTable : {}", branchTableDTO);
        return branchTableRepository
            .save(branchTableMapper.toEntity(branchTableDTO).setIsPersisted())
            .flatMap(branchTableSearchRepository::save)
            .map(branchTableMapper::toDto);
    }

    /**
     * Partially update a branchTable.
     *
     * @param branchTableDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BranchTableDTO> partialUpdate(BranchTableDTO branchTableDTO) {
        LOG.debug("Request to partially update BranchTable : {}", branchTableDTO);

        return branchTableRepository
            .findById(branchTableDTO.getId())
            .map(existingBranchTable -> {
                branchTableMapper.partialUpdate(existingBranchTable, branchTableDTO);

                return existingBranchTable;
            })
            .flatMap(branchTableRepository::save)
            .flatMap(savedBranchTable -> {
                branchTableSearchRepository.save(savedBranchTable);
                return Mono.just(savedBranchTable);
            })
            .map(branchTableMapper::toDto);
    }

    /**
     * Get all the branchTables.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BranchTableDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all BranchTables");
        return branchTableRepository.findAllBy(pageable).map(branchTableMapper::toDto);
    }

    /**
     * Returns the number of branchTables available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return branchTableRepository.count();
    }

    /**
     * Returns the number of branchTables available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return branchTableSearchRepository.count();
    }

    /**
     * Get one branchTable by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BranchTableDTO> findOne(UUID id) {
        LOG.debug("Request to get BranchTable : {}", id);
        return branchTableRepository.findById(id).map(branchTableMapper::toDto);
    }

    /**
     * Delete the branchTable by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete BranchTable : {}", id);
        return branchTableRepository.deleteById(id).then(branchTableSearchRepository.deleteById(id));
    }

    /**
     * Search for the branchTable corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BranchTableDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of BranchTables for query {}", query);
        return branchTableSearchRepository.search(query, pageable).map(branchTableMapper::toDto);
    }
}
