package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Bill;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Bill entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BillRepository extends ReactiveCrudRepository<Bill, UUID>, BillRepositoryInternal {
    Flux<Bill> findAllBy(Pageable pageable);

    @Query("SELECT * FROM bill entity WHERE entity.order_id = :id")
    Flux<Bill> findByOrder(UUID id);

    @Query("SELECT * FROM bill entity WHERE entity.order_id IS NULL")
    Flux<Bill> findAllByOrderIsNull();

    @Query("SELECT * FROM bill entity WHERE entity.branch_id = :id")
    Flux<Bill> findByBranch(UUID id);

    @Query("SELECT * FROM bill entity WHERE entity.branch_id IS NULL")
    Flux<Bill> findAllByBranchIsNull();

    @Query("SELECT * FROM bill entity WHERE entity.customer_id = :id")
    Flux<Bill> findByCustomer(UUID id);

    @Query("SELECT * FROM bill entity WHERE entity.customer_id IS NULL")
    Flux<Bill> findAllByCustomerIsNull();

    @Query(
        "SELECT * FROM bill entity WHERE entity.branch_id = :branchId AND entity.bill_date >= :startDate AND entity.bill_date <= :endDate"
    )
    Flux<Bill> findByBranchIdAndBillDateBetween(UUID branchId, java.time.Instant startDate, java.time.Instant endDate);

    @Override
    <S extends Bill> Mono<S> save(S entity);

    @Override
    Flux<Bill> findAll();

    @Override
    Mono<Bill> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface BillRepositoryInternal {
    <S extends Bill> Mono<S> save(S entity);

    Flux<Bill> findAllBy(Pageable pageable);

    Flux<Bill> findAll();

    Mono<Bill> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Bill> findAllBy(Pageable pageable, Criteria criteria);
}
