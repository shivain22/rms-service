package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.BillItem;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the BillItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BillItemRepository extends ReactiveCrudRepository<BillItem, UUID>, BillItemRepositoryInternal {
    @Query("SELECT * FROM bill_item entity WHERE entity.bill_id = :id")
    Flux<BillItem> findByBill(UUID id);

    @Query("SELECT * FROM bill_item entity WHERE entity.bill_id IS NULL")
    Flux<BillItem> findAllWhereBillIsNull();

    @Query("SELECT * FROM bill_item entity WHERE entity.order_item_id = :id")
    Flux<BillItem> findByOrderItem(UUID id);

    @Query("SELECT * FROM bill_item entity WHERE entity.order_item_id IS NULL")
    Flux<BillItem> findAllWhereOrderItemIsNull();

    @Override
    <S extends BillItem> Mono<S> save(S entity);

    @Override
    Flux<BillItem> findAll();

    @Override
    Mono<BillItem> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface BillItemRepositoryInternal {
    <S extends BillItem> Mono<S> save(S entity);

    Flux<BillItem> findAllBy(Pageable pageable);

    Flux<BillItem> findAll();

    Mono<BillItem> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<BillItem> findAllBy(Pageable pageable, Criteria criteria);
}
