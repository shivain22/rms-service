package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.RmsUser;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the RmsUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RmsUserRepository extends ReactiveCrudRepository<RmsUser, UUID>, RmsUserRepositoryInternal {
    Flux<RmsUser> findAllBy(Pageable pageable);

    Mono<RmsUser> findByExternalUserId(String externalUserId);

    @Override
    <S extends RmsUser> Mono<S> save(S entity);

    @Override
    Flux<RmsUser> findAll();

    @Override
    Mono<RmsUser> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface RmsUserRepositoryInternal {
    <S extends RmsUser> Mono<S> save(S entity);

    Flux<RmsUser> findAllBy(Pageable pageable);

    Flux<RmsUser> findAll();

    Mono<RmsUser> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<RmsUser> findAllBy(Pageable pageable, Criteria criteria);
}
