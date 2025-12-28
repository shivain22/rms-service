package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.TableAssignmentRepository;
import com.atparui.rmsservice.service.dto.DailyTableAssignmentRequestDTO;
import com.atparui.rmsservice.service.dto.TableAssignmentDTO;
import com.atparui.rmsservice.service.mapper.TableAssignmentMapper;
import java.time.LocalDate;
import java.util.List;
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

    private final com.atparui.rmsservice.service.TableWaiterAssignmentService tableWaiterAssignmentService;

    public TableAssignmentService(
        TableAssignmentRepository tableAssignmentRepository,
        TableAssignmentMapper tableAssignmentMapper,
        com.atparui.rmsservice.service.TableWaiterAssignmentService tableWaiterAssignmentService
    ) {
        this.tableAssignmentRepository = tableAssignmentRepository;
        this.tableAssignmentMapper = tableAssignmentMapper;
        this.tableWaiterAssignmentService = tableWaiterAssignmentService;
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

    // jhipster-needle-service-impl-add-method - JHipster will add methods here

    /**
     * Find table assignments by assignment date
     *
     * @param date the assignment date
     * @return the list of table assignment DTOs
     */
    @Transactional(readOnly = true)
    public Flux<TableAssignmentDTO> findByAssignmentDate(LocalDate date) {
        LOG.debug("Request to find TableAssignments by assignment date : {}", date);
        return tableAssignmentRepository.findByAssignmentDate(date).map(tableAssignmentMapper::toDto);
    }

    /**
     * Create daily table assignments
     *
     * @param request the daily assignment request
     * @return the list of created table assignment DTOs
     */
    public Flux<TableAssignmentDTO> createDailyAssignments(DailyTableAssignmentRequestDTO request) {
        LOG.debug("Request to create daily table assignments : {}", request);
        return Flux.fromIterable(request.getAssignments())
            .map(assignmentItem -> {
                TableAssignmentDTO dto = new TableAssignmentDTO();
                dto.setId(UUID.randomUUID());
                dto.setAssignmentDate(request.getAssignmentDate());

                // Set nested BranchTableDTO with ID
                if (assignmentItem.getBranchTableId() != null) {
                    com.atparui.rmsservice.service.dto.BranchTableDTO branchTableDTO =
                        new com.atparui.rmsservice.service.dto.BranchTableDTO();
                    branchTableDTO.setId(assignmentItem.getBranchTableId());
                    dto.setBranchTable(branchTableDTO);
                }

                // Set nested ShiftDTO with ID
                if (assignmentItem.getShiftId() != null) {
                    com.atparui.rmsservice.service.dto.ShiftDTO shiftDTO = new com.atparui.rmsservice.service.dto.ShiftDTO();
                    shiftDTO.setId(assignmentItem.getShiftId());
                    dto.setShift(shiftDTO);
                }

                dto.setIsActive(true);
                return dto;
            })
            .flatMap(this::save);
    }

    /**
     * Assign waiters to a table assignment
     *
     * @param tableAssignmentId the table assignment ID
     * @param waiterIds list of waiter user IDs
     * @return the list of waiter assignment DTOs
     */
    public Flux<com.atparui.rmsservice.service.dto.TableWaiterAssignmentDTO> assignWaiters(UUID tableAssignmentId, List<UUID> waiterIds) {
        LOG.debug("Request to assign waiters : {} - {}", tableAssignmentId, waiterIds);
        return tableAssignmentRepository
            .findById(tableAssignmentId)
            .switchIfEmpty(Mono.error(new RuntimeException("Table assignment not found")))
            .map(tableAssignmentMapper::toDto)
            .flatMapMany(tableAssignmentDTO ->
                Flux.fromIterable(waiterIds)
                    .map(waiterId -> {
                        com.atparui.rmsservice.service.dto.TableWaiterAssignmentDTO dto =
                            new com.atparui.rmsservice.service.dto.TableWaiterAssignmentDTO();
                        dto.setId(UUID.randomUUID());

                        // Set nested TableAssignmentDTO with ID
                        com.atparui.rmsservice.service.dto.TableAssignmentDTO assignmentDTO =
                            new com.atparui.rmsservice.service.dto.TableAssignmentDTO();
                        assignmentDTO.setId(tableAssignmentId);
                        dto.setTableAssignment(assignmentDTO);

                        // Set nested RmsUserDTO with ID
                        com.atparui.rmsservice.service.dto.RmsUserDTO waiterDTO = new com.atparui.rmsservice.service.dto.RmsUserDTO();
                        waiterDTO.setId(waiterId);
                        dto.setWaiter(waiterDTO);

                        // Copy assignment date from table assignment
                        dto.setAssignmentDate(tableAssignmentDTO.getAssignmentDate());
                        dto.setIsActive(true);
                        return dto;
                    })
                    .flatMap(tableWaiterAssignmentService::save)
            );
    }
}
