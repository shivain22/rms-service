package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.TaxConfigRepository;
import com.atparui.rmsservice.service.dto.TaxConfigDTO;
import com.atparui.rmsservice.service.mapper.TaxConfigMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.TaxConfig}.
 */
@Service
@Transactional
public class TaxConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(TaxConfigService.class);

    private final TaxConfigRepository taxConfigRepository;

    private final TaxConfigMapper taxConfigMapper;

    public TaxConfigService(TaxConfigRepository taxConfigRepository, TaxConfigMapper taxConfigMapper) {
        this.taxConfigRepository = taxConfigRepository;
        this.taxConfigMapper = taxConfigMapper;
    }

    /**
     * Save a taxConfig.
     *
     * @param taxConfigDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TaxConfigDTO> save(TaxConfigDTO taxConfigDTO) {
        LOG.debug("Request to save TaxConfig : {}", taxConfigDTO);
        return taxConfigRepository.save(taxConfigMapper.toEntity(taxConfigDTO)).map(taxConfigMapper::toDto);
    }

    /**
     * Update a taxConfig.
     *
     * @param taxConfigDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TaxConfigDTO> update(TaxConfigDTO taxConfigDTO) {
        LOG.debug("Request to update TaxConfig : {}", taxConfigDTO);
        return taxConfigRepository.save(taxConfigMapper.toEntity(taxConfigDTO).setIsPersisted()).map(taxConfigMapper::toDto);
    }

    /**
     * Partially update a taxConfig.
     *
     * @param taxConfigDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TaxConfigDTO> partialUpdate(TaxConfigDTO taxConfigDTO) {
        LOG.debug("Request to partially update TaxConfig : {}", taxConfigDTO);

        return taxConfigRepository
            .findById(taxConfigDTO.getId())
            .map(existingTaxConfig -> {
                taxConfigMapper.partialUpdate(existingTaxConfig, taxConfigDTO);

                return existingTaxConfig;
            })
            .flatMap(taxConfigRepository::save)
            .map(taxConfigMapper::toDto);
    }

    /**
     * Get all the taxConfigs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TaxConfigDTO> findAll() {
        LOG.debug("Request to get all TaxConfigs");
        return taxConfigRepository.findAll().map(taxConfigMapper::toDto);
    }

    /**
     * Returns the number of taxConfigs available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return taxConfigRepository.count();
    }

    /**
     * Get one taxConfig by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TaxConfigDTO> findOne(UUID id) {
        LOG.debug("Request to get TaxConfig : {}", id);
        return taxConfigRepository.findById(id).map(taxConfigMapper::toDto);
    }

    /**
     * Delete the taxConfig by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete TaxConfig : {}", id);
        return taxConfigRepository.deleteById(id);
    }
}
