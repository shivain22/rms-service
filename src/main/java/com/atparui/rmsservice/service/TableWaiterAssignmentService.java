package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.TableWaiterAssignmentRepository;
import com.atparui.rmsservice.service.dto.TableWaiterAssignmentDTO;
import com.atparui.rmsservice.service.mapper.TableWaiterAssignmentMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.TableWaiterAssignment}.
 */
@Service
@Transactional
public class TableWaiterAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(TableWaiterAssignmentService.class);

    private final TableWaiterAssignmentRepository tableWaiterAssignmentRepository;

    private final TableWaiterAssignmentMapper tableWaiterAssignmentMapper;

    public TableWaiterAssignmentService(
        TableWaiterAssignmentRepository tableWaiterAssignmentRepository,
        TableWaiterAssignmentMapper tableWaiterAssignmentMapper
    ) {
        this.tableWaiterAssignmentRepository = tableWaiterAssignmentRepository;
        this.tableWaiterAssignmentMapper = tableWaiterAssignmentMapper;
    }

    /**
     * Save a tableWaiterAssignment.
     *
     * @param tableWaiterAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TableWaiterAssignmentDTO> save(TableWaiterAssignmentDTO tableWaiterAssignmentDTO) {
        LOG.debug("Request to save TableWaiterAssignment : {}", tableWaiterAssignmentDTO);
        return tableWaiterAssignmentRepository
            .save(tableWaiterAssignmentMapper.toEntity(tableWaiterAssignmentDTO))
            .map(tableWaiterAssignmentMapper::toDto);
    }

    /**
     * Update a tableWaiterAssignment.
     *
     * @param tableWaiterAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TableWaiterAssignmentDTO> update(TableWaiterAssignmentDTO tableWaiterAssignmentDTO) {
        LOG.debug("Request to update TableWaiterAssignment : {}", tableWaiterAssignmentDTO);
        return tableWaiterAssignmentRepository
            .save(tableWaiterAssignmentMapper.toEntity(tableWaiterAssignmentDTO).setIsPersisted())
            .map(tableWaiterAssignmentMapper::toDto);
    }

    /**
     * Partially update a tableWaiterAssignment.
     *
     * @param tableWaiterAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TableWaiterAssignmentDTO> partialUpdate(TableWaiterAssignmentDTO tableWaiterAssignmentDTO) {
        LOG.debug("Request to partially update TableWaiterAssignment : {}", tableWaiterAssignmentDTO);

        return tableWaiterAssignmentRepository
            .findById(tableWaiterAssignmentDTO.getId())
            .map(existingTableWaiterAssignment -> {
                tableWaiterAssignmentMapper.partialUpdate(existingTableWaiterAssignment, tableWaiterAssignmentDTO);

                return existingTableWaiterAssignment;
            })
            .flatMap(tableWaiterAssignmentRepository::save)
            .map(tableWaiterAssignmentMapper::toDto);
    }

    /**
     * Get all the tableWaiterAssignments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TableWaiterAssignmentDTO> findAll() {
        LOG.debug("Request to get all TableWaiterAssignments");
        return tableWaiterAssignmentRepository.findAll().map(tableWaiterAssignmentMapper::toDto);
    }

    /**
     * Returns the number of tableWaiterAssignments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return tableWaiterAssignmentRepository.count();
    }

    /**
     * Get one tableWaiterAssignment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TableWaiterAssignmentDTO> findOne(UUID id) {
        LOG.debug("Request to get TableWaiterAssignment : {}", id);
        return tableWaiterAssignmentRepository.findById(id).map(tableWaiterAssignmentMapper::toDto);
    }

    /**
     * Delete the tableWaiterAssignment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete TableWaiterAssignment : {}", id);
        return tableWaiterAssignmentRepository.deleteById(id);
    }
}
