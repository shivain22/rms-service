package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.MenuItemVariant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MenuItemVariant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MenuItemVariantRepository extends ReactiveCrudRepository<MenuItemVariant, UUID>, MenuItemVariantRepositoryInternal {
    @Query("SELECT * FROM menu_item_variant entity WHERE entity.menu_item_id = :id")
    Flux<MenuItemVariant> findByMenuItem(UUID id);

    @Query("SELECT * FROM menu_item_variant entity WHERE entity.menu_item_id IS NULL")
    Flux<MenuItemVariant> findAllByMenuItemIsNull();

    @Override
    <S extends MenuItemVariant> Mono<S> save(S entity);

    @Override
    Flux<MenuItemVariant> findAll();

    @Override
    Mono<MenuItemVariant> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface MenuItemVariantRepositoryInternal {
    <S extends MenuItemVariant> Mono<S> save(S entity);

    Flux<MenuItemVariant> findAllBy(Pageable pageable);

    Flux<MenuItemVariant> findAll();

    Mono<MenuItemVariant> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MenuItemVariant> findAllBy(Pageable pageable, Criteria criteria);
}
