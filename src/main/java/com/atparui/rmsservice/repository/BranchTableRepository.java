package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.BranchTable;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the BranchTable entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BranchTableRepository extends ReactiveCrudRepository<BranchTable, UUID>, BranchTableRepositoryInternal {
    Flux<BranchTable> findAllBy(Pageable pageable);

    @Query("SELECT * FROM branch_table entity WHERE entity.branch_id = :id")
    Flux<BranchTable> findByBranch(UUID id);

    @Query("SELECT * FROM branch_table entity WHERE entity.branch_id IS NULL")
    Flux<BranchTable> findAllWhereBranchIsNull();

    @Query("SELECT * FROM branch_table entity WHERE entity.branch_id = :branchId AND entity.status = 'AVAILABLE'")
    Flux<BranchTable> findAvailableByBranchId(UUID branchId);

    @Query("SELECT * FROM branch_table entity WHERE entity.branch_id = :branchId AND entity.status = :status")
    Flux<BranchTable> findByBranchIdAndStatus(UUID branchId, String status);

    @Override
    <S extends BranchTable> Mono<S> save(S entity);

    @Override
    Flux<BranchTable> findAll();

    @Override
    Mono<BranchTable> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface BranchTableRepositoryInternal {
    <S extends BranchTable> Mono<S> save(S entity);

    Flux<BranchTable> findAllBy(Pageable pageable);

    Flux<BranchTable> findAll();

    Mono<BranchTable> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<BranchTable> findAllBy(Pageable pageable, Criteria criteria);
}
