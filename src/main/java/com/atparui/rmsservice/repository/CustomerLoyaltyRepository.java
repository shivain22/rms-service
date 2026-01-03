package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.CustomerLoyalty;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the CustomerLoyalty entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerLoyaltyRepository extends ReactiveCrudRepository<CustomerLoyalty, UUID>, CustomerLoyaltyRepositoryInternal {
    @Query("SELECT * FROM customer_loyalty entity WHERE entity.customer_id = :id")
    Flux<CustomerLoyalty> findByCustomer(UUID id);

    @Query("SELECT * FROM customer_loyalty entity WHERE entity.customer_id IS NULL")
    Flux<CustomerLoyalty> findAllByCustomerIsNull();

    @Query("SELECT * FROM customer_loyalty entity WHERE entity.restaurant_id = :id")
    Flux<CustomerLoyalty> findByRestaurant(UUID id);

    @Query("SELECT * FROM customer_loyalty entity WHERE entity.restaurant_id IS NULL")
    Flux<CustomerLoyalty> findAllByRestaurantIsNull();

    @Override
    <S extends CustomerLoyalty> Mono<S> save(S entity);

    @Override
    Flux<CustomerLoyalty> findAll();

    @Override
    Mono<CustomerLoyalty> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface CustomerLoyaltyRepositoryInternal {
    <S extends CustomerLoyalty> Mono<S> save(S entity);

    Flux<CustomerLoyalty> findAllBy(Pageable pageable);

    Flux<CustomerLoyalty> findAll();

    Mono<CustomerLoyalty> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<CustomerLoyalty> findAllBy(Pageable pageable, Criteria criteria);
}
