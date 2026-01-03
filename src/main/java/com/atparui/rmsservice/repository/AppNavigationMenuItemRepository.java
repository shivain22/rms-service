package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.AppNavigationMenuItem;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AppNavigationMenuItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppNavigationMenuItemRepository
    extends ReactiveCrudRepository<AppNavigationMenuItem, UUID>, AppNavigationMenuItemRepositoryInternal {
    Flux<AppNavigationMenuItem> findAllBy(Pageable pageable);

    @Query("SELECT * FROM app_navigation_menu_item entity WHERE entity.parent_menu_id = :id")
    Flux<AppNavigationMenuItem> findByParentMenu(UUID id);

    @Query("SELECT * FROM app_navigation_menu_item entity WHERE entity.parent_menu_id IS NULL")
    Flux<AppNavigationMenuItem> findAllByParentMenuIsNull();

    @Override
    <S extends AppNavigationMenuItem> Mono<S> save(S entity);

    @Override
    Flux<AppNavigationMenuItem> findAll();

    @Override
    Mono<AppNavigationMenuItem> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface AppNavigationMenuItemRepositoryInternal {
    <S extends AppNavigationMenuItem> Mono<S> save(S entity);

    Flux<AppNavigationMenuItem> findAllBy(Pageable pageable);

    Flux<AppNavigationMenuItem> findAll();

    Mono<AppNavigationMenuItem> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AppNavigationMenuItem> findAllBy(Pageable pageable, Criteria criteria);
}
