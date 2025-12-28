package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.AppNavigationMenuRoleRepository;
import com.atparui.rmsservice.service.dto.AppNavigationMenuRoleDTO;
import com.atparui.rmsservice.service.mapper.AppNavigationMenuRoleMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.AppNavigationMenuRole}.
 */
@Service
@Transactional
public class AppNavigationMenuRoleService {

    private static final Logger LOG = LoggerFactory.getLogger(AppNavigationMenuRoleService.class);

    private final AppNavigationMenuRoleRepository appNavigationMenuRoleRepository;

    private final AppNavigationMenuRoleMapper appNavigationMenuRoleMapper;

    public AppNavigationMenuRoleService(
        AppNavigationMenuRoleRepository appNavigationMenuRoleRepository,
        AppNavigationMenuRoleMapper appNavigationMenuRoleMapper
    ) {
        this.appNavigationMenuRoleRepository = appNavigationMenuRoleRepository;
        this.appNavigationMenuRoleMapper = appNavigationMenuRoleMapper;
    }

    /**
     * Save a appNavigationMenuRole.
     *
     * @param appNavigationMenuRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AppNavigationMenuRoleDTO> save(AppNavigationMenuRoleDTO appNavigationMenuRoleDTO) {
        LOG.debug("Request to save AppNavigationMenuRole : {}", appNavigationMenuRoleDTO);
        return appNavigationMenuRoleRepository
            .save(appNavigationMenuRoleMapper.toEntity(appNavigationMenuRoleDTO))
            .map(appNavigationMenuRoleMapper::toDto);
    }

    /**
     * Update a appNavigationMenuRole.
     *
     * @param appNavigationMenuRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AppNavigationMenuRoleDTO> update(AppNavigationMenuRoleDTO appNavigationMenuRoleDTO) {
        LOG.debug("Request to update AppNavigationMenuRole : {}", appNavigationMenuRoleDTO);
        return appNavigationMenuRoleRepository
            .save(appNavigationMenuRoleMapper.toEntity(appNavigationMenuRoleDTO).setIsPersisted())
            .map(appNavigationMenuRoleMapper::toDto);
    }

    /**
     * Partially update a appNavigationMenuRole.
     *
     * @param appNavigationMenuRoleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AppNavigationMenuRoleDTO> partialUpdate(AppNavigationMenuRoleDTO appNavigationMenuRoleDTO) {
        LOG.debug("Request to partially update AppNavigationMenuRole : {}", appNavigationMenuRoleDTO);

        return appNavigationMenuRoleRepository
            .findById(appNavigationMenuRoleDTO.getId())
            .map(existingAppNavigationMenuRole -> {
                appNavigationMenuRoleMapper.partialUpdate(existingAppNavigationMenuRole, appNavigationMenuRoleDTO);

                return existingAppNavigationMenuRole;
            })
            .flatMap(appNavigationMenuRoleRepository::save)
            .map(appNavigationMenuRoleMapper::toDto);
    }

    /**
     * Get all the appNavigationMenuRoles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AppNavigationMenuRoleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AppNavigationMenuRoles");
        return appNavigationMenuRoleRepository.findAllBy(pageable).map(appNavigationMenuRoleMapper::toDto);
    }

    /**
     * Returns the number of appNavigationMenuRoles available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return appNavigationMenuRoleRepository.count();
    }

    /**
     * Get one appNavigationMenuRole by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AppNavigationMenuRoleDTO> findOne(UUID id) {
        LOG.debug("Request to get AppNavigationMenuRole : {}", id);
        return appNavigationMenuRoleRepository.findById(id).map(appNavigationMenuRoleMapper::toDto);
    }

    /**
     * Delete the appNavigationMenuRole by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete AppNavigationMenuRole : {}", id);
        return appNavigationMenuRoleRepository.deleteById(id);
    }
}
