package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.AppNavigationMenuRole;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AppNavigationMenuRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppNavigationMenuRoleRepository
    extends ReactiveCrudRepository<AppNavigationMenuRole, UUID>, AppNavigationMenuRoleRepositoryInternal {
    Flux<AppNavigationMenuRole> findAllBy(Pageable pageable);

    @Query("SELECT * FROM app_navigation_menu_role entity WHERE entity.app_navigation_menu_id = :id")
    Flux<AppNavigationMenuRole> findByAppNavigationMenu(UUID id);

    @Query("SELECT * FROM app_navigation_menu_role entity WHERE entity.app_navigation_menu_id IS NULL")
    Flux<AppNavigationMenuRole> findAllByAppNavigationMenuIsNull();

    @Query("SELECT * FROM app_navigation_menu_role entity WHERE entity.app_navigation_menu_item_id = :id")
    Flux<AppNavigationMenuRole> findByAppNavigationMenuItem(UUID id);

    @Query("SELECT * FROM app_navigation_menu_role entity WHERE entity.app_navigation_menu_item_id IS NULL")
    Flux<AppNavigationMenuRole> findAllByAppNavigationMenuItemIsNull();

    @Query("SELECT * FROM app_navigation_menu_role entity WHERE entity.role = :role AND entity.is_active = true")
    Flux<AppNavigationMenuRole> findByRole(String role);

    @Query(
        "SELECT * FROM app_navigation_menu_role entity WHERE entity.app_navigation_menu_id = :menuId AND entity.role = :role AND entity.is_active = true"
    )
    Mono<AppNavigationMenuRole> findByMenuIdAndRole(UUID menuId, String role);

    @Query(
        "SELECT * FROM app_navigation_menu_role entity WHERE entity.app_navigation_menu_item_id = :itemId AND entity.role = :role AND entity.is_active = true"
    )
    Mono<AppNavigationMenuRole> findByMenuItemIdAndRole(UUID itemId, String role);

    @Override
    <S extends AppNavigationMenuRole> Mono<S> save(S entity);

    @Override
    Flux<AppNavigationMenuRole> findAll();

    @Override
    Mono<AppNavigationMenuRole> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface AppNavigationMenuRoleRepositoryInternal {
    <S extends AppNavigationMenuRole> Mono<S> save(S entity);

    Flux<AppNavigationMenuRole> findAllBy(Pageable pageable);

    Flux<AppNavigationMenuRole> findAll();

    Mono<AppNavigationMenuRole> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AppNavigationMenuRole> findAllBy(Pageable pageable, Criteria criteria);
}
