package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.RmsUserRepository;
import com.atparui.rmsservice.repository.search.RmsUserSearchRepository;
import com.atparui.rmsservice.service.dto.RmsUserDTO;
import com.atparui.rmsservice.service.mapper.RmsUserMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.RmsUser}.
 */
@Service
@Transactional
public class RmsUserService {

    private static final Logger LOG = LoggerFactory.getLogger(RmsUserService.class);

    private final RmsUserRepository rmsUserRepository;

    private final RmsUserMapper rmsUserMapper;

    private final RmsUserSearchRepository rmsUserSearchRepository;

    public RmsUserService(
        RmsUserRepository rmsUserRepository,
        RmsUserMapper rmsUserMapper,
        RmsUserSearchRepository rmsUserSearchRepository
    ) {
        this.rmsUserRepository = rmsUserRepository;
        this.rmsUserMapper = rmsUserMapper;
        this.rmsUserSearchRepository = rmsUserSearchRepository;
    }

    /**
     * Save a rmsUser.
     *
     * @param rmsUserDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RmsUserDTO> save(RmsUserDTO rmsUserDTO) {
        LOG.debug("Request to save RmsUser : {}", rmsUserDTO);
        return rmsUserRepository.save(rmsUserMapper.toEntity(rmsUserDTO)).flatMap(rmsUserSearchRepository::save).map(rmsUserMapper::toDto);
    }

    /**
     * Update a rmsUser.
     *
     * @param rmsUserDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RmsUserDTO> update(RmsUserDTO rmsUserDTO) {
        LOG.debug("Request to update RmsUser : {}", rmsUserDTO);
        return rmsUserRepository
            .save(rmsUserMapper.toEntity(rmsUserDTO).setIsPersisted())
            .flatMap(rmsUserSearchRepository::save)
            .map(rmsUserMapper::toDto);
    }

    /**
     * Partially update a rmsUser.
     *
     * @param rmsUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RmsUserDTO> partialUpdate(RmsUserDTO rmsUserDTO) {
        LOG.debug("Request to partially update RmsUser : {}", rmsUserDTO);

        return rmsUserRepository
            .findById(rmsUserDTO.getId())
            .map(existingRmsUser -> {
                rmsUserMapper.partialUpdate(existingRmsUser, rmsUserDTO);

                return existingRmsUser;
            })
            .flatMap(rmsUserRepository::save)
            .flatMap(savedRmsUser -> {
                rmsUserSearchRepository.save(savedRmsUser);
                return Mono.just(savedRmsUser);
            })
            .map(rmsUserMapper::toDto);
    }

    /**
     * Get all the rmsUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RmsUserDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all RmsUsers");
        return rmsUserRepository.findAllBy(pageable).map(rmsUserMapper::toDto);
    }

    /**
     * Returns the number of rmsUsers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return rmsUserRepository.count();
    }

    /**
     * Returns the number of rmsUsers available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return rmsUserSearchRepository.count();
    }

    /**
     * Get one rmsUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RmsUserDTO> findOne(UUID id) {
        LOG.debug("Request to get RmsUser : {}", id);
        return rmsUserRepository.findById(id).map(rmsUserMapper::toDto);
    }

    /**
     * Delete the rmsUser by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete RmsUser : {}", id);
        return rmsUserRepository.deleteById(id).then(rmsUserSearchRepository.deleteById(id));
    }

    /**
     * Search for the rmsUser corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RmsUserDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of RmsUsers for query {}", query);
        return rmsUserSearchRepository.search(query, pageable).map(rmsUserMapper::toDto);
    }
}
