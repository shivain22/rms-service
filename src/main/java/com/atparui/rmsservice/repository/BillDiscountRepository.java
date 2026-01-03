package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.BillDiscount;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the BillDiscount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BillDiscountRepository extends ReactiveCrudRepository<BillDiscount, UUID>, BillDiscountRepositoryInternal {
    @Query("SELECT * FROM bill_discount entity WHERE entity.bill_id = :id")
    Flux<BillDiscount> findByBill(UUID id);

    @Query("SELECT * FROM bill_discount entity WHERE entity.bill_id IS NULL")
    Flux<BillDiscount> findAllByBillIsNull();

    @Query("SELECT * FROM bill_discount entity WHERE entity.discount_id = :id")
    Flux<BillDiscount> findByDiscount(UUID id);

    @Query("SELECT * FROM bill_discount entity WHERE entity.discount_id IS NULL")
    Flux<BillDiscount> findAllByDiscountIsNull();

    @Override
    <S extends BillDiscount> Mono<S> save(S entity);

    @Override
    Flux<BillDiscount> findAll();

    @Override
    Mono<BillDiscount> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface BillDiscountRepositoryInternal {
    <S extends BillDiscount> Mono<S> save(S entity);

    Flux<BillDiscount> findAllBy(Pageable pageable);

    Flux<BillDiscount> findAll();

    Mono<BillDiscount> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<BillDiscount> findAllBy(Pageable pageable, Criteria criteria);
}
