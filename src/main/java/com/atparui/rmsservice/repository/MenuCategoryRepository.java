package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.MenuCategory;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MenuCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MenuCategoryRepository extends ReactiveCrudRepository<MenuCategory, UUID>, MenuCategoryRepositoryInternal {
    Flux<MenuCategory> findAllBy(Pageable pageable);

    @Query("SELECT * FROM menu_category entity WHERE entity.restaurant_id = :id")
    Flux<MenuCategory> findByRestaurant(UUID id);

    @Query("SELECT * FROM menu_category entity WHERE entity.restaurant_id IS NULL")
    Flux<MenuCategory> findAllByRestaurantIsNull();

    @Override
    <S extends MenuCategory> Mono<S> save(S entity);

    @Override
    Flux<MenuCategory> findAll();

    @Override
    Mono<MenuCategory> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface MenuCategoryRepositoryInternal {
    <S extends MenuCategory> Mono<S> save(S entity);

    Flux<MenuCategory> findAllBy(Pageable pageable);

    Flux<MenuCategory> findAll();

    Mono<MenuCategory> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MenuCategory> findAllBy(Pageable pageable, Criteria criteria);
}
