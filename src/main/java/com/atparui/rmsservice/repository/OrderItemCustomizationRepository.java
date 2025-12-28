package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.OrderItemCustomization;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OrderItemCustomization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderItemCustomizationRepository
    extends ReactiveCrudRepository<OrderItemCustomization, UUID>, OrderItemCustomizationRepositoryInternal {
    @Query("SELECT * FROM order_item_customization entity WHERE entity.order_item_id = :id")
    Flux<OrderItemCustomization> findByOrderItem(UUID id);

    @Query("SELECT * FROM order_item_customization entity WHERE entity.order_item_id IS NULL")
    Flux<OrderItemCustomization> findAllWhereOrderItemIsNull();

    @Query("SELECT * FROM order_item_customization entity WHERE entity.menu_item_addon_id = :id")
    Flux<OrderItemCustomization> findByMenuItemAddon(UUID id);

    @Query("SELECT * FROM order_item_customization entity WHERE entity.menu_item_addon_id IS NULL")
    Flux<OrderItemCustomization> findAllWhereMenuItemAddonIsNull();

    @Override
    <S extends OrderItemCustomization> Mono<S> save(S entity);

    @Override
    Flux<OrderItemCustomization> findAll();

    @Override
    Mono<OrderItemCustomization> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface OrderItemCustomizationRepositoryInternal {
    <S extends OrderItemCustomization> Mono<S> save(S entity);

    Flux<OrderItemCustomization> findAllBy(Pageable pageable);

    Flux<OrderItemCustomization> findAll();

    Mono<OrderItemCustomization> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<OrderItemCustomization> findAllBy(Pageable pageable, Criteria criteria);
}
