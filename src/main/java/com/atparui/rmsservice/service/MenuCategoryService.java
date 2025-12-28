package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.MenuCategoryRepository;
import com.atparui.rmsservice.repository.search.MenuCategorySearchRepository;
import com.atparui.rmsservice.service.dto.MenuCategoryDTO;
import com.atparui.rmsservice.service.mapper.MenuCategoryMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.MenuCategory}.
 */
@Service
@Transactional
public class MenuCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuCategoryService.class);

    private final MenuCategoryRepository menuCategoryRepository;

    private final MenuCategoryMapper menuCategoryMapper;

    private final MenuCategorySearchRepository menuCategorySearchRepository;

    public MenuCategoryService(
        MenuCategoryRepository menuCategoryRepository,
        MenuCategoryMapper menuCategoryMapper,
        MenuCategorySearchRepository menuCategorySearchRepository
    ) {
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuCategoryMapper = menuCategoryMapper;
        this.menuCategorySearchRepository = menuCategorySearchRepository;
    }

    /**
     * Save a menuCategory.
     *
     * @param menuCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MenuCategoryDTO> save(MenuCategoryDTO menuCategoryDTO) {
        LOG.debug("Request to save MenuCategory : {}", menuCategoryDTO);
        return menuCategoryRepository
            .save(menuCategoryMapper.toEntity(menuCategoryDTO))
            .flatMap(menuCategorySearchRepository::save)
            .map(menuCategoryMapper::toDto);
    }

    /**
     * Update a menuCategory.
     *
     * @param menuCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MenuCategoryDTO> update(MenuCategoryDTO menuCategoryDTO) {
        LOG.debug("Request to update MenuCategory : {}", menuCategoryDTO);
        return menuCategoryRepository
            .save(menuCategoryMapper.toEntity(menuCategoryDTO).setIsPersisted())
            .flatMap(menuCategorySearchRepository::save)
            .map(menuCategoryMapper::toDto);
    }

    /**
     * Partially update a menuCategory.
     *
     * @param menuCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<MenuCategoryDTO> partialUpdate(MenuCategoryDTO menuCategoryDTO) {
        LOG.debug("Request to partially update MenuCategory : {}", menuCategoryDTO);

        return menuCategoryRepository
            .findById(menuCategoryDTO.getId())
            .map(existingMenuCategory -> {
                menuCategoryMapper.partialUpdate(existingMenuCategory, menuCategoryDTO);

                return existingMenuCategory;
            })
            .flatMap(menuCategoryRepository::save)
            .flatMap(savedMenuCategory -> {
                menuCategorySearchRepository.save(savedMenuCategory);
                return Mono.just(savedMenuCategory);
            })
            .map(menuCategoryMapper::toDto);
    }

    /**
     * Get all the menuCategories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MenuCategoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all MenuCategories");
        return menuCategoryRepository.findAllBy(pageable).map(menuCategoryMapper::toDto);
    }

    /**
     * Returns the number of menuCategories available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return menuCategoryRepository.count();
    }

    /**
     * Returns the number of menuCategories available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return menuCategorySearchRepository.count();
    }

    /**
     * Get one menuCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<MenuCategoryDTO> findOne(UUID id) {
        LOG.debug("Request to get MenuCategory : {}", id);
        return menuCategoryRepository.findById(id).map(menuCategoryMapper::toDto);
    }

    /**
     * Delete the menuCategory by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete MenuCategory : {}", id);
        return menuCategoryRepository.deleteById(id).then(menuCategorySearchRepository.deleteById(id));
    }

    /**
     * Search for the menuCategory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MenuCategoryDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of MenuCategories for query {}", query);
        return menuCategorySearchRepository.search(query, pageable).map(menuCategoryMapper::toDto);
    }
}
