package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.MenuItem;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MenuItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MenuItemRepository extends ReactiveCrudRepository<MenuItem, UUID>, MenuItemRepositoryInternal {
    Flux<MenuItem> findAllBy(Pageable pageable);

    @Query("SELECT * FROM menu_item entity WHERE entity.branch_id = :id")
    Flux<MenuItem> findByBranch(UUID id);

    @Query("SELECT * FROM menu_item entity WHERE entity.branch_id IS NULL")
    Flux<MenuItem> findAllWhereBranchIsNull();

    @Query("SELECT * FROM menu_item entity WHERE entity.menu_category_id = :id")
    Flux<MenuItem> findByMenuCategory(UUID id);

    @Query("SELECT * FROM menu_item entity WHERE entity.menu_category_id IS NULL")
    Flux<MenuItem> findAllWhereMenuCategoryIsNull();

    @Query("SELECT * FROM menu_item entity WHERE entity.branch_id = :branchId AND entity.is_available = true")
    Flux<MenuItem> findAvailableByBranchId(UUID branchId);

    @Query("SELECT * FROM menu_item entity WHERE entity.menu_category_id = :categoryId")
    Flux<MenuItem> findByCategoryId(UUID categoryId);

    @Override
    <S extends MenuItem> Mono<S> save(S entity);

    @Override
    Flux<MenuItem> findAll();

    @Override
    Mono<MenuItem> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface MenuItemRepositoryInternal {
    <S extends MenuItem> Mono<S> save(S entity);

    Flux<MenuItem> findAllBy(Pageable pageable);

    Flux<MenuItem> findAll();

    Mono<MenuItem> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<MenuItem> findAllBy(Pageable pageable, Criteria criteria);
}
