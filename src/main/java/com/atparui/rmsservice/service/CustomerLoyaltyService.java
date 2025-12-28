package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.CustomerLoyaltyRepository;
import com.atparui.rmsservice.service.dto.CustomerLoyaltyDTO;
import com.atparui.rmsservice.service.mapper.CustomerLoyaltyMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.CustomerLoyalty}.
 */
@Service
@Transactional
public class CustomerLoyaltyService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerLoyaltyService.class);

    private final CustomerLoyaltyRepository customerLoyaltyRepository;

    private final CustomerLoyaltyMapper customerLoyaltyMapper;

    public CustomerLoyaltyService(CustomerLoyaltyRepository customerLoyaltyRepository, CustomerLoyaltyMapper customerLoyaltyMapper) {
        this.customerLoyaltyRepository = customerLoyaltyRepository;
        this.customerLoyaltyMapper = customerLoyaltyMapper;
    }

    /**
     * Save a customerLoyalty.
     *
     * @param customerLoyaltyDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerLoyaltyDTO> save(CustomerLoyaltyDTO customerLoyaltyDTO) {
        LOG.debug("Request to save CustomerLoyalty : {}", customerLoyaltyDTO);
        return customerLoyaltyRepository.save(customerLoyaltyMapper.toEntity(customerLoyaltyDTO)).map(customerLoyaltyMapper::toDto);
    }

    /**
     * Update a customerLoyalty.
     *
     * @param customerLoyaltyDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerLoyaltyDTO> update(CustomerLoyaltyDTO customerLoyaltyDTO) {
        LOG.debug("Request to update CustomerLoyalty : {}", customerLoyaltyDTO);
        return customerLoyaltyRepository
            .save(customerLoyaltyMapper.toEntity(customerLoyaltyDTO).setIsPersisted())
            .map(customerLoyaltyMapper::toDto);
    }

    /**
     * Partially update a customerLoyalty.
     *
     * @param customerLoyaltyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CustomerLoyaltyDTO> partialUpdate(CustomerLoyaltyDTO customerLoyaltyDTO) {
        LOG.debug("Request to partially update CustomerLoyalty : {}", customerLoyaltyDTO);

        return customerLoyaltyRepository
            .findById(customerLoyaltyDTO.getId())
            .map(existingCustomerLoyalty -> {
                customerLoyaltyMapper.partialUpdate(existingCustomerLoyalty, customerLoyaltyDTO);

                return existingCustomerLoyalty;
            })
            .flatMap(customerLoyaltyRepository::save)
            .map(customerLoyaltyMapper::toDto);
    }

    /**
     * Get all the customerLoyalties.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerLoyaltyDTO> findAll() {
        LOG.debug("Request to get all CustomerLoyalties");
        return customerLoyaltyRepository.findAll().map(customerLoyaltyMapper::toDto);
    }

    /**
     * Returns the number of customerLoyalties available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return customerLoyaltyRepository.count();
    }

    /**
     * Get one customerLoyalty by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CustomerLoyaltyDTO> findOne(UUID id) {
        LOG.debug("Request to get CustomerLoyalty : {}", id);
        return customerLoyaltyRepository.findById(id).map(customerLoyaltyMapper::toDto);
    }

    /**
     * Delete the customerLoyalty by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete CustomerLoyalty : {}", id);
        return customerLoyaltyRepository.deleteById(id);
    }
}
