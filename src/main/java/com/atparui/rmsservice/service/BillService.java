package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.BillRepository;
import com.atparui.rmsservice.repository.search.BillSearchRepository;
import com.atparui.rmsservice.service.dto.BillDTO;
import com.atparui.rmsservice.service.mapper.BillMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Bill}.
 */
@Service
@Transactional
public class BillService {

    private static final Logger LOG = LoggerFactory.getLogger(BillService.class);

    private final BillRepository billRepository;

    private final BillMapper billMapper;

    private final BillSearchRepository billSearchRepository;

    public BillService(BillRepository billRepository, BillMapper billMapper, BillSearchRepository billSearchRepository) {
        this.billRepository = billRepository;
        this.billMapper = billMapper;
        this.billSearchRepository = billSearchRepository;
    }

    /**
     * Save a bill.
     *
     * @param billDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillDTO> save(BillDTO billDTO) {
        LOG.debug("Request to save Bill : {}", billDTO);
        return billRepository.save(billMapper.toEntity(billDTO)).flatMap(billSearchRepository::save).map(billMapper::toDto);
    }

    /**
     * Update a bill.
     *
     * @param billDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillDTO> update(BillDTO billDTO) {
        LOG.debug("Request to update Bill : {}", billDTO);
        return billRepository
            .save(billMapper.toEntity(billDTO).setIsPersisted())
            .flatMap(billSearchRepository::save)
            .map(billMapper::toDto);
    }

    /**
     * Partially update a bill.
     *
     * @param billDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BillDTO> partialUpdate(BillDTO billDTO) {
        LOG.debug("Request to partially update Bill : {}", billDTO);

        return billRepository
            .findById(billDTO.getId())
            .map(existingBill -> {
                billMapper.partialUpdate(existingBill, billDTO);

                return existingBill;
            })
            .flatMap(billRepository::save)
            .flatMap(savedBill -> {
                billSearchRepository.save(savedBill);
                return Mono.just(savedBill);
            })
            .map(billMapper::toDto);
    }

    /**
     * Get all the bills.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BillDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Bills");
        return billRepository.findAllBy(pageable).map(billMapper::toDto);
    }

    /**
     * Returns the number of bills available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return billRepository.count();
    }

    /**
     * Returns the number of bills available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return billSearchRepository.count();
    }

    /**
     * Get one bill by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BillDTO> findOne(UUID id) {
        LOG.debug("Request to get Bill : {}", id);
        return billRepository.findById(id).map(billMapper::toDto);
    }

    /**
     * Delete the bill by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Bill : {}", id);
        return billRepository.deleteById(id).then(billSearchRepository.deleteById(id));
    }

    /**
     * Search for the bill corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BillDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Bills for query {}", query);
        return billSearchRepository.search(query, pageable).map(billMapper::toDto);
    }
}
