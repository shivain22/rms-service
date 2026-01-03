package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.MenuItemAddon;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MenuItemAddon entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MenuItemAddonRepository extends ReactiveCrudRepository<MenuItemAddon, UUID>, MenuItemAddonRepositoryInternal {
    @Query("SELECT * FROM menu_item_addon entity WHERE entity.menu_item_id = :id")
    Flux<MenuItemAddon> findByMenuItem(UUID id);

    @Query("SELECT * FROM menu_item_addon entity WHERE entity.menu_item_id IS NULL")
    Flux<MenuItemAddon> findAllByMenuItemIsNull();

    @Override
    <S extends MenuItemAddon> Mono<S> save(S entity);

    @Override
    Flux<MenuItemAddon> findAll();

    @Override
    Mono<MenuItemAddon> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface MenuItemAddonRepositoryInternal {
    <S extends MenuItemAddon> Mono<S> save(S entity);

    Flux<MenuItemAddon> findAllBy(Pageable pageable);

    Flux<MenuItemAddon> findAll();

    Mono<MenuItemAddon> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MenuItemAddon> findAllBy(Pageable pageable, Criteria criteria);
}
