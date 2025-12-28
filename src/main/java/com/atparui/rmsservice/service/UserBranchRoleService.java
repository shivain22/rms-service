package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.UserBranchRoleRepository;
import com.atparui.rmsservice.service.dto.UserBranchRoleDTO;
import com.atparui.rmsservice.service.mapper.UserBranchRoleMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.UserBranchRole}.
 */
@Service
@Transactional
public class UserBranchRoleService {

    private static final Logger LOG = LoggerFactory.getLogger(UserBranchRoleService.class);

    private final UserBranchRoleRepository userBranchRoleRepository;

    private final UserBranchRoleMapper userBranchRoleMapper;

    public UserBranchRoleService(UserBranchRoleRepository userBranchRoleRepository, UserBranchRoleMapper userBranchRoleMapper) {
        this.userBranchRoleRepository = userBranchRoleRepository;
        this.userBranchRoleMapper = userBranchRoleMapper;
    }

    /**
     * Save a userBranchRole.
     *
     * @param userBranchRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserBranchRoleDTO> save(UserBranchRoleDTO userBranchRoleDTO) {
        LOG.debug("Request to save UserBranchRole : {}", userBranchRoleDTO);
        return userBranchRoleRepository.save(userBranchRoleMapper.toEntity(userBranchRoleDTO)).map(userBranchRoleMapper::toDto);
    }

    /**
     * Update a userBranchRole.
     *
     * @param userBranchRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserBranchRoleDTO> update(UserBranchRoleDTO userBranchRoleDTO) {
        LOG.debug("Request to update UserBranchRole : {}", userBranchRoleDTO);
        return userBranchRoleRepository
            .save(userBranchRoleMapper.toEntity(userBranchRoleDTO).setIsPersisted())
            .map(userBranchRoleMapper::toDto);
    }

    /**
     * Partially update a userBranchRole.
     *
     * @param userBranchRoleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UserBranchRoleDTO> partialUpdate(UserBranchRoleDTO userBranchRoleDTO) {
        LOG.debug("Request to partially update UserBranchRole : {}", userBranchRoleDTO);

        return userBranchRoleRepository
            .findById(userBranchRoleDTO.getId())
            .map(existingUserBranchRole -> {
                userBranchRoleMapper.partialUpdate(existingUserBranchRole, userBranchRoleDTO);

                return existingUserBranchRole;
            })
            .flatMap(userBranchRoleRepository::save)
            .map(userBranchRoleMapper::toDto);
    }

    /**
     * Get all the userBranchRoles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserBranchRoleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserBranchRoles");
        return userBranchRoleRepository.findAllBy(pageable).map(userBranchRoleMapper::toDto);
    }

    /**
     * Returns the number of userBranchRoles available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return userBranchRoleRepository.count();
    }

    /**
     * Get one userBranchRole by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<UserBranchRoleDTO> findOne(UUID id) {
        LOG.debug("Request to get UserBranchRole : {}", id);
        return userBranchRoleRepository.findById(id).map(userBranchRoleMapper::toDto);
    }

    /**
     * Delete the userBranchRole by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete UserBranchRole : {}", id);
        return userBranchRoleRepository.deleteById(id);
    }
}
