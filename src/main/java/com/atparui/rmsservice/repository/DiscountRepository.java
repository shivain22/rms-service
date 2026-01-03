package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Discount;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Discount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DiscountRepository extends ReactiveCrudRepository<Discount, UUID>, DiscountRepositoryInternal {
    @Query("SELECT * FROM discount entity WHERE entity.restaurant_id = :id")
    Flux<Discount> findByRestaurant(UUID id);

    @Query("SELECT * FROM discount entity WHERE entity.restaurant_id IS NULL")
    Flux<Discount> findAllByRestaurantIsNull();

    @Query(
        "SELECT * FROM discount entity WHERE entity.restaurant_id = :restaurantId AND entity.is_active = true AND (entity.valid_from <= CURRENT_DATE AND (entity.valid_to IS NULL OR entity.valid_to >= CURRENT_DATE))"
    )
    Flux<Discount> findActiveByRestaurantId(UUID restaurantId);

    @Query("SELECT * FROM discount entity WHERE entity.discount_code = :discountCode AND entity.restaurant_id = :restaurantId")
    Mono<Discount> findByDiscountCodeAndRestaurantId(String discountCode, UUID restaurantId);

    @Override
    <S extends Discount> Mono<S> save(S entity);

    @Override
    Flux<Discount> findAll();

    @Override
    Mono<Discount> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface DiscountRepositoryInternal {
    <S extends Discount> Mono<S> save(S entity);

    Flux<Discount> findAllBy(Pageable pageable);

    Flux<Discount> findAll();

    Mono<Discount> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Discount> findAllBy(Pageable pageable, Criteria criteria);
}
