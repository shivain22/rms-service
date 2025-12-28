package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.UserSyncLog;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the UserSyncLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserSyncLogRepository extends ReactiveCrudRepository<UserSyncLog, UUID>, UserSyncLogRepositoryInternal {
    @Query("SELECT * FROM user_sync_log entity WHERE entity.user_id = :id")
    Flux<UserSyncLog> findByUser(UUID id);

    @Query("SELECT * FROM user_sync_log entity WHERE entity.user_id IS NULL")
    Flux<UserSyncLog> findAllWhereUserIsNull();

    @Override
    <S extends UserSyncLog> Mono<S> save(S entity);

    @Override
    Flux<UserSyncLog> findAll();

    @Override
    Mono<UserSyncLog> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface UserSyncLogRepositoryInternal {
    <S extends UserSyncLog> Mono<S> save(S entity);

    Flux<UserSyncLog> findAllBy(Pageable pageable);

    Flux<UserSyncLog> findAll();

    Mono<UserSyncLog> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<UserSyncLog> findAllBy(Pageable pageable, Criteria criteria);
}
