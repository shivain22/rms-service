package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Shift;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Shift entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShiftRepository extends ReactiveCrudRepository<Shift, UUID>, ShiftRepositoryInternal {
    Flux<Shift> findAllBy(Pageable pageable);

    @Query("SELECT * FROM shift entity WHERE entity.branch_id = :id")
    Flux<Shift> findByBranch(UUID id);

    @Query("SELECT * FROM shift entity WHERE entity.branch_id IS NULL")
    Flux<Shift> findAllWhereBranchIsNull();

    @Override
    <S extends Shift> Mono<S> save(S entity);

    @Override
    Flux<Shift> findAll();

    @Override
    Mono<Shift> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface ShiftRepositoryInternal {
    <S extends Shift> Mono<S> save(S entity);

    Flux<Shift> findAllBy(Pageable pageable);

    Flux<Shift> findAll();

    Mono<Shift> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Shift> findAllBy(Pageable pageable, Criteria criteria);
}
