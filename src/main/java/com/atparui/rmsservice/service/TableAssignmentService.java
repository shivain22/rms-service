package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.TableAssignmentRepository;
import com.atparui.rmsservice.service.dto.TableAssignmentDTO;
import com.atparui.rmsservice.service.mapper.TableAssignmentMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.TableAssignment}.
 */
@Service
@Transactional
public class TableAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(TableAssignmentService.class);

    private final TableAssignmentRepository tableAssignmentRepository;

    private final TableAssignmentMapper tableAssignmentMapper;

    public TableAssignmentService(TableAssignmentRepository tableAssignmentRepository, TableAssignmentMapper tableAssignmentMapper) {
        this.tableAssignmentRepository = tableAssignmentRepository;
        this.tableAssignmentMapper = tableAssignmentMapper;
    }

    /**
     * Save a tableAssignment.
     *
     * @param tableAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TableAssignmentDTO> save(TableAssignmentDTO tableAssignmentDTO) {
        LOG.debug("Request to save TableAssignment : {}", tableAssignmentDTO);
        return tableAssignmentRepository.save(tableAssignmentMapper.toEntity(tableAssignmentDTO)).map(tableAssignmentMapper::toDto);
    }

    /**
     * Update a tableAssignment.
     *
     * @param tableAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TableAssignmentDTO> update(TableAssignmentDTO tableAssignmentDTO) {
        LOG.debug("Request to update TableAssignment : {}", tableAssignmentDTO);
        return tableAssignmentRepository
            .save(tableAssignmentMapper.toEntity(tableAssignmentDTO).setIsPersisted())
            .map(tableAssignmentMapper::toDto);
    }

    /**
     * Partially update a tableAssignment.
     *
     * @param tableAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TableAssignmentDTO> partialUpdate(TableAssignmentDTO tableAssignmentDTO) {
        LOG.debug("Request to partially update TableAssignment : {}", tableAssignmentDTO);

        return tableAssignmentRepository
            .findById(tableAssignmentDTO.getId())
            .map(existingTableAssignment -> {
                tableAssignmentMapper.partialUpdate(existingTableAssignment, tableAssignmentDTO);

                return existingTableAssignment;
            })
            .flatMap(tableAssignmentRepository::save)
            .map(tableAssignmentMapper::toDto);
    }

    /**
     * Get all the tableAssignments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TableAssignmentDTO> findAll() {
        LOG.debug("Request to get all TableAssignments");
        return tableAssignmentRepository.findAll().map(tableAssignmentMapper::toDto);
    }

    /**
     * Returns the number of tableAssignments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return tableAssignmentRepository.count();
    }

    /**
     * Get one tableAssignment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TableAssignmentDTO> findOne(UUID id) {
        LOG.debug("Request to get TableAssignment : {}", id);
        return tableAssignmentRepository.findById(id).map(tableAssignmentMapper::toDto);
    }

    /**
     * Delete the tableAssignment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete TableAssignment : {}", id);
        return tableAssignmentRepository.deleteById(id);
    }
}
