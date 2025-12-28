package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.UserSyncLogRepository;
import com.atparui.rmsservice.service.dto.UserSyncLogDTO;
import com.atparui.rmsservice.service.mapper.UserSyncLogMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.UserSyncLog}.
 */
@Service
@Transactional
public class UserSyncLogService {

    private static final Logger LOG = LoggerFactory.getLogger(UserSyncLogService.class);

    private final UserSyncLogRepository userSyncLogRepository;

    private final UserSyncLogMapper userSyncLogMapper;

    public UserSyncLogService(UserSyncLogRepository userSyncLogRepository, UserSyncLogMapper userSyncLogMapper) {
        this.userSyncLogRepository = userSyncLogRepository;
        this.userSyncLogMapper = userSyncLogMapper;
    }

    /**
     * Save a userSyncLog.
     *
     * @param userSyncLogDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserSyncLogDTO> save(UserSyncLogDTO userSyncLogDTO) {
        LOG.debug("Request to save UserSyncLog : {}", userSyncLogDTO);
        return userSyncLogRepository.save(userSyncLogMapper.toEntity(userSyncLogDTO)).map(userSyncLogMapper::toDto);
    }

    /**
     * Update a userSyncLog.
     *
     * @param userSyncLogDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserSyncLogDTO> update(UserSyncLogDTO userSyncLogDTO) {
        LOG.debug("Request to update UserSyncLog : {}", userSyncLogDTO);
        return userSyncLogRepository.save(userSyncLogMapper.toEntity(userSyncLogDTO).setIsPersisted()).map(userSyncLogMapper::toDto);
    }

    /**
     * Partially update a userSyncLog.
     *
     * @param userSyncLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UserSyncLogDTO> partialUpdate(UserSyncLogDTO userSyncLogDTO) {
        LOG.debug("Request to partially update UserSyncLog : {}", userSyncLogDTO);

        return userSyncLogRepository
            .findById(userSyncLogDTO.getId())
            .map(existingUserSyncLog -> {
                userSyncLogMapper.partialUpdate(existingUserSyncLog, userSyncLogDTO);

                return existingUserSyncLog;
            })
            .flatMap(userSyncLogRepository::save)
            .map(userSyncLogMapper::toDto);
    }

    /**
     * Get all the userSyncLogs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserSyncLogDTO> findAll() {
        LOG.debug("Request to get all UserSyncLogs");
        return userSyncLogRepository.findAll().map(userSyncLogMapper::toDto);
    }

    /**
     * Returns the number of userSyncLogs available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return userSyncLogRepository.count();
    }

    /**
     * Get one userSyncLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<UserSyncLogDTO> findOne(UUID id) {
        LOG.debug("Request to get UserSyncLog : {}", id);
        return userSyncLogRepository.findById(id).map(userSyncLogMapper::toDto);
    }

    /**
     * Delete the userSyncLog by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete UserSyncLog : {}", id);
        return userSyncLogRepository.deleteById(id);
    }
}
