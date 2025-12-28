package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.OrderItem;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OrderItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, UUID>, OrderItemRepositoryInternal {
    @Query("SELECT * FROM order_item entity WHERE entity.order_id = :id")
    Flux<OrderItem> findByOrder(UUID id);

    @Query("SELECT * FROM order_item entity WHERE entity.order_id IS NULL")
    Flux<OrderItem> findAllWhereOrderIsNull();

    @Query("SELECT * FROM order_item entity WHERE entity.menu_item_id = :id")
    Flux<OrderItem> findByMenuItem(UUID id);

    @Query("SELECT * FROM order_item entity WHERE entity.menu_item_id IS NULL")
    Flux<OrderItem> findAllWhereMenuItemIsNull();

    @Query("SELECT * FROM order_item entity WHERE entity.menu_item_variant_id = :id")
    Flux<OrderItem> findByMenuItemVariant(UUID id);

    @Query("SELECT * FROM order_item entity WHERE entity.menu_item_variant_id IS NULL")
    Flux<OrderItem> findAllWhereMenuItemVariantIsNull();

    @Override
    <S extends OrderItem> Mono<S> save(S entity);

    @Override
    Flux<OrderItem> findAll();

    @Override
    Mono<OrderItem> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface OrderItemRepositoryInternal {
    <S extends OrderItem> Mono<S> save(S entity);

    Flux<OrderItem> findAllBy(Pageable pageable);

    Flux<OrderItem> findAll();

    Mono<OrderItem> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<OrderItem> findAllBy(Pageable pageable, Criteria criteria);
}
