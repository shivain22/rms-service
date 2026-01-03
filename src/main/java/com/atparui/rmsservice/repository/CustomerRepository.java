package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.Customer;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, UUID>, CustomerRepositoryInternal {
    Flux<Customer> findAllBy(Pageable pageable);

    @Query("SELECT * FROM customer entity WHERE entity.user_id = :id")
    Flux<Customer> findByUser(UUID id);

    @Query("SELECT * FROM customer entity WHERE entity.user_id IS NULL")
    Flux<Customer> findAllByUserIsNull();

    @Override
    <S extends Customer> Mono<S> save(S entity);

    @Override
    Flux<Customer> findAll();

    @Override
    Mono<Customer> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface CustomerRepositoryInternal {
    <S extends Customer> Mono<S> save(S entity);

    Flux<Customer> findAllBy(Pageable pageable);

    Flux<Customer> findAll();

    Mono<Customer> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Customer> findAllBy(Pageable pageable, Criteria criteria);
}
