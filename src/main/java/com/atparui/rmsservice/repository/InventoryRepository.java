package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Inventory;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Inventory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InventoryRepository extends ReactiveCrudRepository<Inventory, UUID>, InventoryRepositoryInternal {
    @Query("SELECT * FROM inventory entity WHERE entity.branch_id = :id")
    Flux<Inventory> findByBranch(UUID id);

    @Query("SELECT * FROM inventory entity WHERE entity.branch_id IS NULL")
    Flux<Inventory> findAllWhereBranchIsNull();

    @Query("SELECT * FROM inventory entity WHERE entity.menu_item_id = :id")
    Flux<Inventory> findByMenuItem(UUID id);

    @Query("SELECT * FROM inventory entity WHERE entity.menu_item_id IS NULL")
    Flux<Inventory> findAllWhereMenuItemIsNull();

    @Query("SELECT * FROM inventory entity WHERE entity.branch_id = :branchId AND entity.current_stock < entity.minimum_stock")
    Flux<Inventory> findLowStockByBranchId(UUID branchId);

    @Override
    <S extends Inventory> Mono<S> save(S entity);

    @Override
    Flux<Inventory> findAll();

    @Override
    Mono<Inventory> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface InventoryRepositoryInternal {
    <S extends Inventory> Mono<S> save(S entity);

    Flux<Inventory> findAllBy(Pageable pageable);

    Flux<Inventory> findAll();

    Mono<Inventory> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Inventory> findAllBy(Pageable pageable, Criteria criteria);
}
