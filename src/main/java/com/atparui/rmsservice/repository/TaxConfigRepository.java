package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.TaxConfig;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TaxConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaxConfigRepository extends ReactiveCrudRepository<TaxConfig, UUID>, TaxConfigRepositoryInternal {
    @Query("SELECT * FROM tax_config entity WHERE entity.restaurant_id = :id")
    Flux<TaxConfig> findByRestaurant(UUID id);

    @Query("SELECT * FROM tax_config entity WHERE entity.restaurant_id IS NULL")
    Flux<TaxConfig> findAllByRestaurantIsNull();

    @Override
    <S extends TaxConfig> Mono<S> save(S entity);

    @Override
    Flux<TaxConfig> findAll();

    @Override
    Mono<TaxConfig> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface TaxConfigRepositoryInternal {
    <S extends TaxConfig> Mono<S> save(S entity);

    Flux<TaxConfig> findAllBy(Pageable pageable);

    Flux<TaxConfig> findAll();

    Mono<TaxConfig> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TaxConfig> findAllBy(Pageable pageable, Criteria criteria);
}
