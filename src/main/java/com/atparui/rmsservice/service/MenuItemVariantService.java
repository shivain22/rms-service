package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.MenuItemVariantRepository;
import com.atparui.rmsservice.service.dto.MenuItemVariantDTO;
import com.atparui.rmsservice.service.mapper.MenuItemVariantMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.MenuItemVariant}.
 */
@Service
@Transactional
public class MenuItemVariantService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemVariantService.class);

    private final MenuItemVariantRepository menuItemVariantRepository;

    private final MenuItemVariantMapper menuItemVariantMapper;

    public MenuItemVariantService(MenuItemVariantRepository menuItemVariantRepository, MenuItemVariantMapper menuItemVariantMapper) {
        this.menuItemVariantRepository = menuItemVariantRepository;
        this.menuItemVariantMapper = menuItemVariantMapper;
    }

    /**
     * Save a menuItemVariant.
     *
     * @param menuItemVariantDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MenuItemVariantDTO> save(MenuItemVariantDTO menuItemVariantDTO) {
        LOG.debug("Request to save MenuItemVariant : {}", menuItemVariantDTO);
        return menuItemVariantRepository.save(menuItemVariantMapper.toEntity(menuItemVariantDTO)).map(menuItemVariantMapper::toDto);
    }

    /**
     * Update a menuItemVariant.
     *
     * @param menuItemVariantDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MenuItemVariantDTO> update(MenuItemVariantDTO menuItemVariantDTO) {
        LOG.debug("Request to update MenuItemVariant : {}", menuItemVariantDTO);
        return menuItemVariantRepository
            .save(menuItemVariantMapper.toEntity(menuItemVariantDTO).setIsPersisted())
            .map(menuItemVariantMapper::toDto);
    }

    /**
     * Partially update a menuItemVariant.
     *
     * @param menuItemVariantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<MenuItemVariantDTO> partialUpdate(MenuItemVariantDTO menuItemVariantDTO) {
        LOG.debug("Request to partially update MenuItemVariant : {}", menuItemVariantDTO);

        return menuItemVariantRepository
            .findById(menuItemVariantDTO.getId())
            .map(existingMenuItemVariant -> {
                menuItemVariantMapper.partialUpdate(existingMenuItemVariant, menuItemVariantDTO);

                return existingMenuItemVariant;
            })
            .flatMap(menuItemVariantRepository::save)
            .map(menuItemVariantMapper::toDto);
    }

    /**
     * Get all the menuItemVariants.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MenuItemVariantDTO> findAll() {
        LOG.debug("Request to get all MenuItemVariants");
        return menuItemVariantRepository.findAll().map(menuItemVariantMapper::toDto);
    }

    /**
     * Returns the number of menuItemVariants available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return menuItemVariantRepository.count();
    }

    /**
     * Get one menuItemVariant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<MenuItemVariantDTO> findOne(UUID id) {
        LOG.debug("Request to get MenuItemVariant : {}", id);
        return menuItemVariantRepository.findById(id).map(menuItemVariantMapper::toDto);
    }

    /**
     * Delete the menuItemVariant by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete MenuItemVariant : {}", id);
        return menuItemVariantRepository.deleteById(id);
    }
}
