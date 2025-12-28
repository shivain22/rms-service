package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.AppNavigationMenu;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AppNavigationMenu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppNavigationMenuRepository extends ReactiveCrudRepository<AppNavigationMenu, UUID>, AppNavigationMenuRepositoryInternal {
    Flux<AppNavigationMenu> findAllBy(Pageable pageable);

    @Override
    <S extends AppNavigationMenu> Mono<S> save(S entity);

    @Override
    Flux<AppNavigationMenu> findAll();

    @Override
    Mono<AppNavigationMenu> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface AppNavigationMenuRepositoryInternal {
    <S extends AppNavigationMenu> Mono<S> save(S entity);

    Flux<AppNavigationMenu> findAllBy(Pageable pageable);

    Flux<AppNavigationMenu> findAll();

    Mono<AppNavigationMenu> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AppNavigationMenu> findAllBy(Pageable pageable, Criteria criteria);
}
