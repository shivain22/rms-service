package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.AppNavigationMenuItemRepository;
import com.atparui.rmsservice.service.dto.AppNavigationMenuItemDTO;
import com.atparui.rmsservice.service.mapper.AppNavigationMenuItemMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.AppNavigationMenuItem}.
 */
@Service
@Transactional
public class AppNavigationMenuItemService {

    private static final Logger LOG = LoggerFactory.getLogger(AppNavigationMenuItemService.class);

    private final AppNavigationMenuItemRepository appNavigationMenuItemRepository;

    private final AppNavigationMenuItemMapper appNavigationMenuItemMapper;

    public AppNavigationMenuItemService(
        AppNavigationMenuItemRepository appNavigationMenuItemRepository,
        AppNavigationMenuItemMapper appNavigationMenuItemMapper
    ) {
        this.appNavigationMenuItemRepository = appNavigationMenuItemRepository;
        this.appNavigationMenuItemMapper = appNavigationMenuItemMapper;
    }

    /**
     * Save a appNavigationMenuItem.
     *
     * @param appNavigationMenuItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AppNavigationMenuItemDTO> save(AppNavigationMenuItemDTO appNavigationMenuItemDTO) {
        LOG.debug("Request to save AppNavigationMenuItem : {}", appNavigationMenuItemDTO);
        return appNavigationMenuItemRepository
            .save(appNavigationMenuItemMapper.toEntity(appNavigationMenuItemDTO))
            .map(appNavigationMenuItemMapper::toDto);
    }

    /**
     * Update a appNavigationMenuItem.
     *
     * @param appNavigationMenuItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AppNavigationMenuItemDTO> update(AppNavigationMenuItemDTO appNavigationMenuItemDTO) {
        LOG.debug("Request to update AppNavigationMenuItem : {}", appNavigationMenuItemDTO);
        return appNavigationMenuItemRepository
            .save(appNavigationMenuItemMapper.toEntity(appNavigationMenuItemDTO).setIsPersisted())
            .map(appNavigationMenuItemMapper::toDto);
    }

    /**
     * Partially update a appNavigationMenuItem.
     *
     * @param appNavigationMenuItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AppNavigationMenuItemDTO> partialUpdate(AppNavigationMenuItemDTO appNavigationMenuItemDTO) {
        LOG.debug("Request to partially update AppNavigationMenuItem : {}", appNavigationMenuItemDTO);

        return appNavigationMenuItemRepository
            .findById(appNavigationMenuItemDTO.getId())
            .map(existingAppNavigationMenuItem -> {
                appNavigationMenuItemMapper.partialUpdate(existingAppNavigationMenuItem, appNavigationMenuItemDTO);

                return existingAppNavigationMenuItem;
            })
            .flatMap(appNavigationMenuItemRepository::save)
            .map(appNavigationMenuItemMapper::toDto);
    }

    /**
     * Get all the appNavigationMenuItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AppNavigationMenuItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AppNavigationMenuItems");
        return appNavigationMenuItemRepository.findAllBy(pageable).map(appNavigationMenuItemMapper::toDto);
    }

    /**
     * Returns the number of appNavigationMenuItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return appNavigationMenuItemRepository.count();
    }

    /**
     * Get one appNavigationMenuItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AppNavigationMenuItemDTO> findOne(UUID id) {
        LOG.debug("Request to get AppNavigationMenuItem : {}", id);
        return appNavigationMenuItemRepository.findById(id).map(appNavigationMenuItemMapper::toDto);
    }

    /**
     * Delete the appNavigationMenuItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete AppNavigationMenuItem : {}", id);
        return appNavigationMenuItemRepository.deleteById(id);
    }
}
