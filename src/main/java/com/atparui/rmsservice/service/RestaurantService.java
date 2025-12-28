package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.RestaurantRepository;
import com.atparui.rmsservice.repository.search.RestaurantSearchRepository;
import com.atparui.rmsservice.service.dto.RestaurantDTO;
import com.atparui.rmsservice.service.mapper.RestaurantMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Restaurant}.
 */
@Service
@Transactional
public class RestaurantService {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurantService.class);

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMapper restaurantMapper;

    private final RestaurantSearchRepository restaurantSearchRepository;

    public RestaurantService(
        RestaurantRepository restaurantRepository,
        RestaurantMapper restaurantMapper,
        RestaurantSearchRepository restaurantSearchRepository
    ) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
        this.restaurantSearchRepository = restaurantSearchRepository;
    }

    /**
     * Save a restaurant.
     *
     * @param restaurantDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RestaurantDTO> save(RestaurantDTO restaurantDTO) {
        LOG.debug("Request to save Restaurant : {}", restaurantDTO);
        return restaurantRepository
            .save(restaurantMapper.toEntity(restaurantDTO))
            .flatMap(restaurantSearchRepository::save)
            .map(restaurantMapper::toDto);
    }

    /**
     * Update a restaurant.
     *
     * @param restaurantDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RestaurantDTO> update(RestaurantDTO restaurantDTO) {
        LOG.debug("Request to update Restaurant : {}", restaurantDTO);
        return restaurantRepository
            .save(restaurantMapper.toEntity(restaurantDTO).setIsPersisted())
            .flatMap(restaurantSearchRepository::save)
            .map(restaurantMapper::toDto);
    }

    /**
     * Partially update a restaurant.
     *
     * @param restaurantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RestaurantDTO> partialUpdate(RestaurantDTO restaurantDTO) {
        LOG.debug("Request to partially update Restaurant : {}", restaurantDTO);

        return restaurantRepository
            .findById(restaurantDTO.getId())
            .map(existingRestaurant -> {
                restaurantMapper.partialUpdate(existingRestaurant, restaurantDTO);

                return existingRestaurant;
            })
            .flatMap(restaurantRepository::save)
            .flatMap(savedRestaurant -> {
                restaurantSearchRepository.save(savedRestaurant);
                return Mono.just(savedRestaurant);
            })
            .map(restaurantMapper::toDto);
    }

    /**
     * Get all the restaurants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RestaurantDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Restaurants");
        return restaurantRepository.findAllBy(pageable).map(restaurantMapper::toDto);
    }

    /**
     * Returns the number of restaurants available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return restaurantRepository.count();
    }

    /**
     * Returns the number of restaurants available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return restaurantSearchRepository.count();
    }

    /**
     * Get one restaurant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RestaurantDTO> findOne(UUID id) {
        LOG.debug("Request to get Restaurant : {}", id);
        return restaurantRepository.findById(id).map(restaurantMapper::toDto);
    }

    /**
     * Delete the restaurant by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Restaurant : {}", id);
        return restaurantRepository.deleteById(id).then(restaurantSearchRepository.deleteById(id));
    }

    /**
     * Search for the restaurant corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RestaurantDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Restaurants for query {}", query);
        return restaurantSearchRepository.search(query, pageable).map(restaurantMapper::toDto);
    }
}
