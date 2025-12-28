package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.UserBranchRoleRepository;
import com.atparui.rmsservice.service.dto.UserBranchRoleAssignmentDTO;
import com.atparui.rmsservice.service.dto.UserBranchRoleDTO;
import com.atparui.rmsservice.service.mapper.UserBranchRoleMapper;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.UserBranchRole}.
 */
@Service
@Transactional
public class UserBranchRoleService {

    private static final Logger LOG = LoggerFactory.getLogger(UserBranchRoleService.class);

    private final UserBranchRoleRepository userBranchRoleRepository;

    private final UserBranchRoleMapper userBranchRoleMapper;

    public UserBranchRoleService(UserBranchRoleRepository userBranchRoleRepository, UserBranchRoleMapper userBranchRoleMapper) {
        this.userBranchRoleRepository = userBranchRoleRepository;
        this.userBranchRoleMapper = userBranchRoleMapper;
    }

    /**
     * Save a userBranchRole.
     *
     * @param userBranchRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserBranchRoleDTO> save(UserBranchRoleDTO userBranchRoleDTO) {
        LOG.debug("Request to save UserBranchRole : {}", userBranchRoleDTO);
        return userBranchRoleRepository.save(userBranchRoleMapper.toEntity(userBranchRoleDTO)).map(userBranchRoleMapper::toDto);
    }

    /**
     * Update a userBranchRole.
     *
     * @param userBranchRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserBranchRoleDTO> update(UserBranchRoleDTO userBranchRoleDTO) {
        LOG.debug("Request to update UserBranchRole : {}", userBranchRoleDTO);
        return userBranchRoleRepository
            .save(userBranchRoleMapper.toEntity(userBranchRoleDTO).setIsPersisted())
            .map(userBranchRoleMapper::toDto);
    }

    /**
     * Partially update a userBranchRole.
     *
     * @param userBranchRoleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UserBranchRoleDTO> partialUpdate(UserBranchRoleDTO userBranchRoleDTO) {
        LOG.debug("Request to partially update UserBranchRole : {}", userBranchRoleDTO);

        return userBranchRoleRepository
            .findById(userBranchRoleDTO.getId())
            .map(existingUserBranchRole -> {
                userBranchRoleMapper.partialUpdate(existingUserBranchRole, userBranchRoleDTO);

                return existingUserBranchRole;
            })
            .flatMap(userBranchRoleRepository::save)
            .map(userBranchRoleMapper::toDto);
    }

    /**
     * Get all the userBranchRoles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserBranchRoleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserBranchRoles");
        return userBranchRoleRepository.findAllBy(pageable).map(userBranchRoleMapper::toDto);
    }

    /**
     * Returns the number of userBranchRoles available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return userBranchRoleRepository.count();
    }

    /**
     * Get one userBranchRole by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<UserBranchRoleDTO> findOne(UUID id) {
        LOG.debug("Request to get UserBranchRole : {}", id);
        return userBranchRoleRepository.findById(id).map(userBranchRoleMapper::toDto);
    }

    /**
     * Delete the userBranchRole by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete UserBranchRole : {}", id);
        return userBranchRoleRepository.deleteById(id);
    }

    // jhipster-needle-service-impl-add-method - JHipster will add methods here

    /**
     * Find user branch roles by branch ID and role
     *
     * @param branchId the branch ID
     * @param role the role name
     * @return the list of user branch role DTOs
     */
    @Transactional(readOnly = true)
    public Flux<UserBranchRoleDTO> findByBranchIdAndRole(UUID branchId, String role) {
        LOG.debug("Request to find UserBranchRoles by branch ID and role : {} - {}", branchId, role);
        return userBranchRoleRepository.findByBranchIdAndRole(branchId, role).map(userBranchRoleMapper::toDto);
    }

    /**
     * Assign a role to a user for a specific branch
     *
     * @param assignmentDTO the assignment details
     * @return the created user branch role DTO
     */
    public Mono<UserBranchRoleDTO> assignRole(UserBranchRoleAssignmentDTO assignmentDTO) {
        LOG.debug("Request to assign role : {}", assignmentDTO);
        UserBranchRoleDTO dto = new UserBranchRoleDTO();
        dto.setId(UUID.randomUUID());

        // Set nested RmsUserDTO with ID
        if (assignmentDTO.getUserId() != null) {
            com.atparui.rmsservice.service.dto.RmsUserDTO userDTO = new com.atparui.rmsservice.service.dto.RmsUserDTO();
            userDTO.setId(assignmentDTO.getUserId());
            dto.setUser(userDTO);
        }

        // Set nested BranchDTO with ID
        if (assignmentDTO.getBranchId() != null) {
            com.atparui.rmsservice.service.dto.BranchDTO branchDTO = new com.atparui.rmsservice.service.dto.BranchDTO();
            branchDTO.setId(assignmentDTO.getBranchId());
            dto.setBranch(branchDTO);
        }

        dto.setRole(assignmentDTO.getRole());
        dto.setIsActive(true);
        dto.setAssignedAt(Instant.now());
        return save(dto);
    }

    /**
     * Revoke a role from a user
     *
     * @param id the id of the user branch role
     * @return the updated user branch role DTO
     */
    public Mono<UserBranchRoleDTO> revokeRole(UUID id) {
        LOG.debug("Request to revoke role : {}", id);
        return userBranchRoleRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("UserBranchRole not found")))
            .map(userBranchRole -> {
                userBranchRole.setIsActive(false);
                userBranchRole.setRevokedAt(Instant.now());
                return userBranchRole;
            })
            .flatMap(userBranchRoleRepository::save)
            .map(userBranchRoleMapper::toDto);
    }
}
