package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.OrderStatusHistory;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OrderStatusHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderStatusHistoryRepository
    extends ReactiveCrudRepository<OrderStatusHistory, UUID>, OrderStatusHistoryRepositoryInternal {
    @Query("SELECT * FROM order_status_history entity WHERE entity.order_id = :id")
    Flux<OrderStatusHistory> findByOrder(UUID id);

    @Query("SELECT * FROM order_status_history entity WHERE entity.order_id IS NULL")
    Flux<OrderStatusHistory> findAllWhereOrderIsNull();

    @Override
    <S extends OrderStatusHistory> Mono<S> save(S entity);

    @Override
    Flux<OrderStatusHistory> findAll();

    @Override
    Mono<OrderStatusHistory> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface OrderStatusHistoryRepositoryInternal {
    <S extends OrderStatusHistory> Mono<S> save(S entity);

    Flux<OrderStatusHistory> findAllBy(Pageable pageable);

    Flux<OrderStatusHistory> findAll();

    Mono<OrderStatusHistory> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<OrderStatusHistory> findAllBy(Pageable pageable, Criteria criteria);
}
