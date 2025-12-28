package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.RmsUserRepository;
import com.atparui.rmsservice.repository.search.RmsUserSearchRepository;
import com.atparui.rmsservice.service.UserSyncLogService;
import com.atparui.rmsservice.service.dto.RmsUserDTO;
import com.atparui.rmsservice.service.dto.UserSyncLogDTO;
import com.atparui.rmsservice.service.mapper.RmsUserMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.RmsUser}.
 */
@Service
@Transactional
public class RmsUserService {

    private static final Logger LOG = LoggerFactory.getLogger(RmsUserService.class);

    private final RmsUserRepository rmsUserRepository;

    private final RmsUserMapper rmsUserMapper;

    private final RmsUserSearchRepository rmsUserSearchRepository;

    private final UserSyncLogService userSyncLogService;

    public RmsUserService(
        RmsUserRepository rmsUserRepository,
        RmsUserMapper rmsUserMapper,
        RmsUserSearchRepository rmsUserSearchRepository,
        UserSyncLogService userSyncLogService
    ) {
        this.rmsUserRepository = rmsUserRepository;
        this.rmsUserMapper = rmsUserMapper;
        this.rmsUserSearchRepository = rmsUserSearchRepository;
        this.userSyncLogService = userSyncLogService;
    }

    /**
     * Save a rmsUser.
     *
     * @param rmsUserDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RmsUserDTO> save(RmsUserDTO rmsUserDTO) {
        LOG.debug("Request to save RmsUser : {}", rmsUserDTO);
        return rmsUserRepository.save(rmsUserMapper.toEntity(rmsUserDTO)).flatMap(rmsUserSearchRepository::save).map(rmsUserMapper::toDto);
    }

    /**
     * Update a rmsUser.
     *
     * @param rmsUserDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RmsUserDTO> update(RmsUserDTO rmsUserDTO) {
        LOG.debug("Request to update RmsUser : {}", rmsUserDTO);
        return rmsUserRepository
            .save(rmsUserMapper.toEntity(rmsUserDTO).setIsPersisted())
            .flatMap(rmsUserSearchRepository::save)
            .map(rmsUserMapper::toDto);
    }

    /**
     * Partially update a rmsUser.
     *
     * @param rmsUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RmsUserDTO> partialUpdate(RmsUserDTO rmsUserDTO) {
        LOG.debug("Request to partially update RmsUser : {}", rmsUserDTO);

        return rmsUserRepository
            .findById(rmsUserDTO.getId())
            .map(existingRmsUser -> {
                rmsUserMapper.partialUpdate(existingRmsUser, rmsUserDTO);

                return existingRmsUser;
            })
            .flatMap(rmsUserRepository::save)
            .flatMap(savedRmsUser -> {
                rmsUserSearchRepository.save(savedRmsUser);
                return Mono.just(savedRmsUser);
            })
            .map(rmsUserMapper::toDto);
    }

    /**
     * Get all the rmsUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RmsUserDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all RmsUsers");
        return rmsUserRepository.findAllBy(pageable).map(rmsUserMapper::toDto);
    }

    /**
     * Returns the number of rmsUsers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return rmsUserRepository.count();
    }

    /**
     * Returns the number of rmsUsers available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return rmsUserSearchRepository.count();
    }

    /**
     * Get one rmsUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RmsUserDTO> findOne(UUID id) {
        LOG.debug("Request to get RmsUser : {}", id);
        return rmsUserRepository.findById(id).map(rmsUserMapper::toDto);
    }

    /**
     * Delete the rmsUser by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete RmsUser : {}", id);
        return rmsUserRepository.deleteById(id).then(rmsUserSearchRepository.deleteById(id));
    }

    /**
     * Search for the rmsUser corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RmsUserDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of RmsUsers for query {}", query);
        return rmsUserSearchRepository.search(query, pageable).map(rmsUserMapper::toDto);
    }

    // jhipster-needle-service-impl-add-method - JHipster will add methods here

    /**
     * Find user by external user ID from Gateway/Keycloak
     *
     * @param externalUserId the external user ID
     * @return the user DTO
     */
    @Transactional(readOnly = true)
    public Mono<RmsUserDTO> findByExternalUserId(String externalUserId) {
        LOG.debug("Request to find RmsUser by external user ID : {}", externalUserId);
        return rmsUserRepository.findByExternalUserId(externalUserId).map(rmsUserMapper::toDto);
    }

    /**
     * Sync user with Gateway/Keycloak
     *
     * @param userId the id of the user
     * @return the sync log DTO
     */
    public Mono<UserSyncLogDTO> syncUserWithGateway(UUID userId) {
        LOG.debug("Request to sync user with Gateway/Keycloak : {}", userId);
        return rmsUserRepository
            .findById(userId)
            .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
            .flatMap(user -> {
                // TODO: Implement actual Gateway/Keycloak sync logic
                // For now, create a sync log entry
                UserSyncLogDTO syncLog = new UserSyncLogDTO();
                syncLog.setId(UUID.randomUUID());
                syncLog.setSyncType("MANUAL");
                syncLog.setSyncStatus("SUCCESS");
                syncLog.setExternalUserId(user.getExternalUserId());
                syncLog.setSyncedAt(Instant.now());
                syncLog.setUser(rmsUserMapper.toDto(user));

                // Update user sync status
                user.setLastSyncAt(Instant.now());
                user.setSyncStatus("SYNCED");
                return rmsUserRepository.save(user).flatMap(savedUser -> userSyncLogService.save(syncLog));
            });
    }

    /**
     * Bulk sync users with Gateway/Keycloak
     *
     * @param userIds list of user IDs to sync
     * @return the list of sync log DTOs
     */
    public Flux<UserSyncLogDTO> bulkSyncUsers(List<UUID> userIds) {
        LOG.debug("Request to bulk sync users : {}", userIds);
        return Flux.fromIterable(userIds).flatMap(this::syncUserWithGateway);
    }
}
