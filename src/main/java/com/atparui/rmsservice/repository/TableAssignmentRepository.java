package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.TableAssignment;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TableAssignment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TableAssignmentRepository extends ReactiveCrudRepository<TableAssignment, UUID>, TableAssignmentRepositoryInternal {
    @Query("SELECT * FROM table_assignment entity WHERE entity.branch_table_id = :id")
    Flux<TableAssignment> findByBranchTable(UUID id);

    @Query("SELECT * FROM table_assignment entity WHERE entity.branch_table_id IS NULL")
    Flux<TableAssignment> findAllWhereBranchTableIsNull();

    @Query("SELECT * FROM table_assignment entity WHERE entity.shift_id = :id")
    Flux<TableAssignment> findByShift(UUID id);

    @Query("SELECT * FROM table_assignment entity WHERE entity.shift_id IS NULL")
    Flux<TableAssignment> findAllWhereShiftIsNull();

    @Query("SELECT * FROM table_assignment entity WHERE entity.supervisor_id = :id")
    Flux<TableAssignment> findBySupervisor(UUID id);

    @Query("SELECT * FROM table_assignment entity WHERE entity.supervisor_id IS NULL")
    Flux<TableAssignment> findAllWhereSupervisorIsNull();

    @Override
    <S extends TableAssignment> Mono<S> save(S entity);

    @Override
    Flux<TableAssignment> findAll();

    @Override
    Mono<TableAssignment> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface TableAssignmentRepositoryInternal {
    <S extends TableAssignment> Mono<S> save(S entity);

    Flux<TableAssignment> findAllBy(Pageable pageable);

    Flux<TableAssignment> findAll();

    Mono<TableAssignment> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TableAssignment> findAllBy(Pageable pageable, Criteria criteria);
}
