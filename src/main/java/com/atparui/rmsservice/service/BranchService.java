package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.BranchRepository;
import com.atparui.rmsservice.repository.search.BranchSearchRepository;
import com.atparui.rmsservice.service.dto.BranchDTO;
import com.atparui.rmsservice.service.mapper.BranchMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Branch}.
 */
@Service
@Transactional
public class BranchService {

    private static final Logger LOG = LoggerFactory.getLogger(BranchService.class);

    private final BranchRepository branchRepository;

    private final BranchMapper branchMapper;

    private final BranchSearchRepository branchSearchRepository;

    public BranchService(BranchRepository branchRepository, BranchMapper branchMapper, BranchSearchRepository branchSearchRepository) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
        this.branchSearchRepository = branchSearchRepository;
    }

    /**
     * Save a branch.
     *
     * @param branchDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BranchDTO> save(BranchDTO branchDTO) {
        LOG.debug("Request to save Branch : {}", branchDTO);
        return branchRepository.save(branchMapper.toEntity(branchDTO)).flatMap(branchSearchRepository::save).map(branchMapper::toDto);
    }

    /**
     * Update a branch.
     *
     * @param branchDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BranchDTO> update(BranchDTO branchDTO) {
        LOG.debug("Request to update Branch : {}", branchDTO);
        return branchRepository
            .save(branchMapper.toEntity(branchDTO).setIsPersisted())
            .flatMap(branchSearchRepository::save)
            .map(branchMapper::toDto);
    }

    /**
     * Partially update a branch.
     *
     * @param branchDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BranchDTO> partialUpdate(BranchDTO branchDTO) {
        LOG.debug("Request to partially update Branch : {}", branchDTO);

        return branchRepository
            .findById(branchDTO.getId())
            .map(existingBranch -> {
                branchMapper.partialUpdate(existingBranch, branchDTO);

                return existingBranch;
            })
            .flatMap(branchRepository::save)
            .flatMap(savedBranch -> {
                branchSearchRepository.save(savedBranch);
                return Mono.just(savedBranch);
            })
            .map(branchMapper::toDto);
    }

    /**
     * Get all the branches.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BranchDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Branches");
        return branchRepository.findAllBy(pageable).map(branchMapper::toDto);
    }

    /**
     * Returns the number of branches available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return branchRepository.count();
    }

    /**
     * Returns the number of branches available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return branchSearchRepository.count();
    }

    /**
     * Get one branch by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BranchDTO> findOne(UUID id) {
        LOG.debug("Request to get Branch : {}", id);
        return branchRepository.findById(id).map(branchMapper::toDto);
    }

    /**
     * Delete the branch by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Branch : {}", id);
        return branchRepository.deleteById(id).then(branchSearchRepository.deleteById(id));
    }

    /**
     * Search for the branch corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BranchDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Branches for query {}", query);
        return branchSearchRepository.search(query, pageable).map(branchMapper::toDto);
    }
}
