package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.ShiftRepository;
import com.atparui.rmsservice.service.dto.ShiftDTO;
import com.atparui.rmsservice.service.mapper.ShiftMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Shift}.
 */
@Service
@Transactional
public class ShiftService {

    private static final Logger LOG = LoggerFactory.getLogger(ShiftService.class);

    private final ShiftRepository shiftRepository;

    private final ShiftMapper shiftMapper;

    public ShiftService(ShiftRepository shiftRepository, ShiftMapper shiftMapper) {
        this.shiftRepository = shiftRepository;
        this.shiftMapper = shiftMapper;
    }

    /**
     * Save a shift.
     *
     * @param shiftDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ShiftDTO> save(ShiftDTO shiftDTO) {
        LOG.debug("Request to save Shift : {}", shiftDTO);
        return shiftRepository.save(shiftMapper.toEntity(shiftDTO)).map(shiftMapper::toDto);
    }

    /**
     * Update a shift.
     *
     * @param shiftDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ShiftDTO> update(ShiftDTO shiftDTO) {
        LOG.debug("Request to update Shift : {}", shiftDTO);
        return shiftRepository.save(shiftMapper.toEntity(shiftDTO).setIsPersisted()).map(shiftMapper::toDto);
    }

    /**
     * Partially update a shift.
     *
     * @param shiftDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ShiftDTO> partialUpdate(ShiftDTO shiftDTO) {
        LOG.debug("Request to partially update Shift : {}", shiftDTO);

        return shiftRepository
            .findById(shiftDTO.getId())
            .map(existingShift -> {
                shiftMapper.partialUpdate(existingShift, shiftDTO);

                return existingShift;
            })
            .flatMap(shiftRepository::save)
            .map(shiftMapper::toDto);
    }

    /**
     * Get all the shifts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ShiftDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Shifts");
        return shiftRepository.findAllBy(pageable).map(shiftMapper::toDto);
    }

    /**
     * Returns the number of shifts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return shiftRepository.count();
    }

    /**
     * Get one shift by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ShiftDTO> findOne(UUID id) {
        LOG.debug("Request to get Shift : {}", id);
        return shiftRepository.findById(id).map(shiftMapper::toDto);
    }

    /**
     * Delete the shift by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Shift : {}", id);
        return shiftRepository.deleteById(id);
    }
}
