package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Restaurant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Restaurant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RestaurantRepository extends ReactiveCrudRepository<Restaurant, UUID>, RestaurantRepositoryInternal {
    Flux<Restaurant> findAllBy(Pageable pageable);

    @Override
    <S extends Restaurant> Mono<S> save(S entity);

    @Override
    Flux<Restaurant> findAll();

    @Override
    Mono<Restaurant> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface RestaurantRepositoryInternal {
    <S extends Restaurant> Mono<S> save(S entity);

    Flux<Restaurant> findAllBy(Pageable pageable);

    Flux<Restaurant> findAll();

    Mono<Restaurant> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Restaurant> findAllBy(Pageable pageable, Criteria criteria);
}
