package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Branch;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Branch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BranchRepository extends ReactiveCrudRepository<Branch, UUID>, BranchRepositoryInternal {
    Flux<Branch> findAllBy(Pageable pageable);

    @Query("SELECT * FROM branch entity WHERE entity.restaurant_id = :id")
    Flux<Branch> findByRestaurant(UUID id);

    @Query("SELECT * FROM branch entity WHERE entity.restaurant_id IS NULL")
    Flux<Branch> findAllByRestaurantIsNull();

    @Override
    <S extends Branch> Mono<S> save(S entity);

    @Override
    Flux<Branch> findAll();

    @Override
    Mono<Branch> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface BranchRepositoryInternal {
    <S extends Branch> Mono<S> save(S entity);

    Flux<Branch> findAllBy(Pageable pageable);

    Flux<Branch> findAll();

    Mono<Branch> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Branch> findAllBy(Pageable pageable, Criteria criteria);
}
