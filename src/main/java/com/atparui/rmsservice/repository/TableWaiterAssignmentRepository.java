package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.TableWaiterAssignment;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TableWaiterAssignment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TableWaiterAssignmentRepository
    extends ReactiveCrudRepository<TableWaiterAssignment, UUID>, TableWaiterAssignmentRepositoryInternal {
    @Query("SELECT * FROM table_waiter_assignment entity WHERE entity.table_assignment_id = :id")
    Flux<TableWaiterAssignment> findByTableAssignment(UUID id);

    @Query("SELECT * FROM table_waiter_assignment entity WHERE entity.table_assignment_id IS NULL")
    Flux<TableWaiterAssignment> findAllByTableAssignmentIsNull();

    @Query("SELECT * FROM table_waiter_assignment entity WHERE entity.waiter_id = :id")
    Flux<TableWaiterAssignment> findByWaiter(UUID id);

    @Query("SELECT * FROM table_waiter_assignment entity WHERE entity.waiter_id IS NULL")
    Flux<TableWaiterAssignment> findAllByWaiterIsNull();

    @Override
    <S extends TableWaiterAssignment> Mono<S> save(S entity);

    @Override
    Flux<TableWaiterAssignment> findAll();

    @Override
    Mono<TableWaiterAssignment> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface TableWaiterAssignmentRepositoryInternal {
    <S extends TableWaiterAssignment> Mono<S> save(S entity);

    Flux<TableWaiterAssignment> findAllBy(Pageable pageable);

    Flux<TableWaiterAssignment> findAll();

    Mono<TableWaiterAssignment> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TableWaiterAssignment> findAllBy(Pageable pageable, Criteria criteria);
}
