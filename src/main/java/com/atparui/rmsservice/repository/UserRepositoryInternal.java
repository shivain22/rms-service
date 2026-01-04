package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.User;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Internal repository interface for User operations that require custom database access.
 * This interface is public to allow access from test classes.
 */
public interface UserRepositoryInternal {
    Mono<Void> resetUserAuthorityMappings();
    Mono<User> findOneWithAuthoritiesByLogin(String login);
    Mono<User> create(User user);
    Flux<User> findAllWithAuthorities(Pageable pageable);
}
