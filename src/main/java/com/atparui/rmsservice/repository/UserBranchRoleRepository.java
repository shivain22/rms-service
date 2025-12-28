package com.atparui.rmsservice.repository;

import com.atparui.rmsservice.domain.UserBranchRole;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the UserBranchRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserBranchRoleRepository extends ReactiveCrudRepository<UserBranchRole, UUID>, UserBranchRoleRepositoryInternal {
    Flux<UserBranchRole> findAllBy(Pageable pageable);

    @Query("SELECT * FROM user_branch_role entity WHERE entity.user_id = :id")
    Flux<UserBranchRole> findByUser(UUID id);

    @Query("SELECT * FROM user_branch_role entity WHERE entity.user_id IS NULL")
    Flux<UserBranchRole> findAllWhereUserIsNull();

    @Query("SELECT * FROM user_branch_role entity WHERE entity.branch_id = :id")
    Flux<UserBranchRole> findByBranch(UUID id);

    @Query("SELECT * FROM user_branch_role entity WHERE entity.branch_id IS NULL")
    Flux<UserBranchRole> findAllWhereBranchIsNull();

    @Query("SELECT * FROM user_branch_role entity WHERE entity.branch_id = :branchId AND entity.role = :role AND entity.is_active = true")
    Flux<UserBranchRole> findByBranchIdAndRole(UUID branchId, String role);

    @Override
    <S extends UserBranchRole> Mono<S> save(S entity);

    @Override
    Flux<UserBranchRole> findAll();

    @Override
    Mono<UserBranchRole> findById(UUID id);

    @Override
    Mono<Void> deleteById(UUID id);
}

interface UserBranchRoleRepositoryInternal {
    <S extends UserBranchRole> Mono<S> save(S entity);

    Flux<UserBranchRole> findAllBy(Pageable pageable);

    Flux<UserBranchRole> findAll();

    Mono<UserBranchRole> findById(UUID id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<UserBranchRole> findAllBy(Pageable pageable, Criteria criteria);
}
