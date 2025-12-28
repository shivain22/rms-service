package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Order;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, UUID>, OrderRepositoryInternal {
    Flux<Order> findAllBy(Pageable pageable);

    @Query("SELECT * FROM jhi_order entity WHERE entity.branch_id = :id")
    Flux<Order> findByBranch(UUID id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.branch_id IS NULL")
    Flux<Order> findAllWhereBranchIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.customer_id = :id")
    Flux<Order> findByCustomer(UUID id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.customer_id IS NULL")
    Flux<Order> findAllWhereCustomerIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.user_id = :id")
    Flux<Order> findByUser(UUID id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.user_id IS NULL")
    Flux<Order> findAllWhereUserIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.branch_table_id = :id")
    Flux<Order> findByBranchTable(UUID id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.branch_table_id IS NULL")
    Flux<Order> findAllWhereBranchTableIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.branch_id = :branchId AND entity.status = :status")
    Flux<Order> findByBranchIdAndStatus(UUID branchId, String status);

    @Query(
        "SELECT * FROM jhi_order entity WHERE entity.branch_id = :branchId AND entity.order_date >= :startDate AND entity.order_date <= :endDate"
    )
    Flux<Order> findByBranchIdAndOrderDateBetween(UUID branchId, java.time.Instant startDate, java.time.Instant endDate);

    @Override
    <S extends Order> Mono<S> save(S entity);

    @Override
    Flux<Order> findAll();

    @Override
    Mono<Order> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface OrderRepositoryInternal {
    <S extends Order> Mono<S> save(S entity);

    Flux<Order> findAllBy(Pageable pageable);

    Flux<Order> findAll();

    Mono<Order> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Order> findAllBy(Pageable pageable, Criteria criteria);
}
