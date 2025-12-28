package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.PaymentMethod;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PaymentMethod entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentMethodRepository extends ReactiveCrudRepository<PaymentMethod, UUID>, PaymentMethodRepositoryInternal {
    @Override
    <S extends PaymentMethod> Mono<S> save(S entity);

    @Override
    Flux<PaymentMethod> findAll();

    @Override
    Mono<PaymentMethod> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface PaymentMethodRepositoryInternal {
    <S extends PaymentMethod> Mono<S> save(S entity);

    Flux<PaymentMethod> findAllBy(Pageable pageable);

    Flux<PaymentMethod> findAll();

    Mono<PaymentMethod> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<PaymentMethod> findAllBy(Pageable pageable, Criteria criteria);
}
