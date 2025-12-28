package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.MenuItemAddonRepository;
import com.atparui.rmsservice.service.dto.MenuItemAddonDTO;
import com.atparui.rmsservice.service.mapper.MenuItemAddonMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.MenuItemAddon}.
 */
@Service
@Transactional
public class MenuItemAddonService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemAddonService.class);

    private final MenuItemAddonRepository menuItemAddonRepository;

    private final MenuItemAddonMapper menuItemAddonMapper;

    public MenuItemAddonService(MenuItemAddonRepository menuItemAddonRepository, MenuItemAddonMapper menuItemAddonMapper) {
        this.menuItemAddonRepository = menuItemAddonRepository;
        this.menuItemAddonMapper = menuItemAddonMapper;
    }

    /**
     * Save a menuItemAddon.
     *
     * @param menuItemAddonDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MenuItemAddonDTO> save(MenuItemAddonDTO menuItemAddonDTO) {
        LOG.debug("Request to save MenuItemAddon : {}", menuItemAddonDTO);
        return menuItemAddonRepository.save(menuItemAddonMapper.toEntity(menuItemAddonDTO)).map(menuItemAddonMapper::toDto);
    }

    /**
     * Update a menuItemAddon.
     *
     * @param menuItemAddonDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MenuItemAddonDTO> update(MenuItemAddonDTO menuItemAddonDTO) {
        LOG.debug("Request to update MenuItemAddon : {}", menuItemAddonDTO);
        return menuItemAddonRepository
            .save(menuItemAddonMapper.toEntity(menuItemAddonDTO).setIsPersisted())
            .map(menuItemAddonMapper::toDto);
    }

    /**
     * Partially update a menuItemAddon.
     *
     * @param menuItemAddonDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<MenuItemAddonDTO> partialUpdate(MenuItemAddonDTO menuItemAddonDTO) {
        LOG.debug("Request to partially update MenuItemAddon : {}", menuItemAddonDTO);

        return menuItemAddonRepository
            .findById(menuItemAddonDTO.getId())
            .map(existingMenuItemAddon -> {
                menuItemAddonMapper.partialUpdate(existingMenuItemAddon, menuItemAddonDTO);

                return existingMenuItemAddon;
            })
            .flatMap(menuItemAddonRepository::save)
            .map(menuItemAddonMapper::toDto);
    }

    /**
     * Get all the menuItemAddons.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MenuItemAddonDTO> findAll() {
        LOG.debug("Request to get all MenuItemAddons");
        return menuItemAddonRepository.findAll().map(menuItemAddonMapper::toDto);
    }

    /**
     * Returns the number of menuItemAddons available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return menuItemAddonRepository.count();
    }

    /**
     * Get one menuItemAddon by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<MenuItemAddonDTO> findOne(UUID id) {
        LOG.debug("Request to get MenuItemAddon : {}", id);
        return menuItemAddonRepository.findById(id).map(menuItemAddonMapper::toDto);
    }

    /**
     * Delete the menuItemAddon by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete MenuItemAddon : {}", id);
        return menuItemAddonRepository.deleteById(id);
    }
}
