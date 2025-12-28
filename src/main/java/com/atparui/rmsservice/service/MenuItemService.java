package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.MenuItemRepository;
import com.atparui.rmsservice.repository.search.MenuItemSearchRepository;
import com.atparui.rmsservice.service.dto.MenuItemDTO;
import com.atparui.rmsservice.service.mapper.MenuItemMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.MenuItem}.
 */
@Service
@Transactional
public class MenuItemService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuItemService.class);

    private final MenuItemRepository menuItemRepository;

    private final MenuItemMapper menuItemMapper;

    private final MenuItemSearchRepository menuItemSearchRepository;

    public MenuItemService(
        MenuItemRepository menuItemRepository,
        MenuItemMapper menuItemMapper,
        MenuItemSearchRepository menuItemSearchRepository
    ) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemMapper = menuItemMapper;
        this.menuItemSearchRepository = menuItemSearchRepository;
    }

    /**
     * Save a menuItem.
     *
     * @param menuItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MenuItemDTO> save(MenuItemDTO menuItemDTO) {
        LOG.debug("Request to save MenuItem : {}", menuItemDTO);
        return menuItemRepository
            .save(menuItemMapper.toEntity(menuItemDTO))
            .flatMap(menuItemSearchRepository::save)
            .map(menuItemMapper::toDto);
    }

    /**
     * Update a menuItem.
     *
     * @param menuItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MenuItemDTO> update(MenuItemDTO menuItemDTO) {
        LOG.debug("Request to update MenuItem : {}", menuItemDTO);
        return menuItemRepository
            .save(menuItemMapper.toEntity(menuItemDTO).setIsPersisted())
            .flatMap(menuItemSearchRepository::save)
            .map(menuItemMapper::toDto);
    }

    /**
     * Partially update a menuItem.
     *
     * @param menuItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<MenuItemDTO> partialUpdate(MenuItemDTO menuItemDTO) {
        LOG.debug("Request to partially update MenuItem : {}", menuItemDTO);

        return menuItemRepository
            .findById(menuItemDTO.getId())
            .map(existingMenuItem -> {
                menuItemMapper.partialUpdate(existingMenuItem, menuItemDTO);

                return existingMenuItem;
            })
            .flatMap(menuItemRepository::save)
            .flatMap(savedMenuItem -> {
                menuItemSearchRepository.save(savedMenuItem);
                return Mono.just(savedMenuItem);
            })
            .map(menuItemMapper::toDto);
    }

    /**
     * Get all the menuItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MenuItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all MenuItems");
        return menuItemRepository.findAllBy(pageable).map(menuItemMapper::toDto);
    }

    /**
     * Returns the number of menuItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return menuItemRepository.count();
    }

    /**
     * Returns the number of menuItems available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return menuItemSearchRepository.count();
    }

    /**
     * Get one menuItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<MenuItemDTO> findOne(UUID id) {
        LOG.debug("Request to get MenuItem : {}", id);
        return menuItemRepository.findById(id).map(menuItemMapper::toDto);
    }

    /**
     * Delete the menuItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete MenuItem : {}", id);
        return menuItemRepository.deleteById(id).then(menuItemSearchRepository.deleteById(id));
    }

    /**
     * Search for the menuItem corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MenuItemDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of MenuItems for query {}", query);
        return menuItemSearchRepository.search(query, pageable).map(menuItemMapper::toDto);
    }
}
