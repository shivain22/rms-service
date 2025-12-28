package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.CustomerRepository;
import com.atparui.rmsservice.repository.OrderRepository;
import com.atparui.rmsservice.repository.search.CustomerSearchRepository;
import com.atparui.rmsservice.service.dto.CustomerDTO;
import com.atparui.rmsservice.service.dto.CustomerLoyaltyDTO;
import com.atparui.rmsservice.service.dto.LoyaltyPointsRequestDTO;
import com.atparui.rmsservice.service.dto.OrderDTO;
import com.atparui.rmsservice.service.mapper.CustomerMapper;
import com.atparui.rmsservice.service.mapper.OrderMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Customer}.
 */
@Service
@Transactional
public class CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final CustomerSearchRepository customerSearchRepository;

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final CustomerLoyaltyService customerLoyaltyService;

    public CustomerService(
        CustomerRepository customerRepository,
        CustomerMapper customerMapper,
        CustomerSearchRepository customerSearchRepository,
        OrderRepository orderRepository,
        OrderMapper orderMapper,
        CustomerLoyaltyService customerLoyaltyService
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerSearchRepository = customerSearchRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.customerLoyaltyService = customerLoyaltyService;
    }

    /**
     * Save a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerDTO> save(CustomerDTO customerDTO) {
        LOG.debug("Request to save Customer : {}", customerDTO);
        return customerRepository
            .save(customerMapper.toEntity(customerDTO))
            .flatMap(customerSearchRepository::save)
            .map(customerMapper::toDto);
    }

    /**
     * Update a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerDTO> update(CustomerDTO customerDTO) {
        LOG.debug("Request to update Customer : {}", customerDTO);
        return customerRepository
            .save(customerMapper.toEntity(customerDTO).setIsPersisted())
            .flatMap(customerSearchRepository::save)
            .map(customerMapper::toDto);
    }

    /**
     * Partially update a customer.
     *
     * @param customerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CustomerDTO> partialUpdate(CustomerDTO customerDTO) {
        LOG.debug("Request to partially update Customer : {}", customerDTO);

        return customerRepository
            .findById(customerDTO.getId())
            .map(existingCustomer -> {
                customerMapper.partialUpdate(existingCustomer, customerDTO);

                return existingCustomer;
            })
            .flatMap(customerRepository::save)
            .flatMap(savedCustomer -> {
                customerSearchRepository.save(savedCustomer);
                return Mono.just(savedCustomer);
            })
            .map(customerMapper::toDto);
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Customers");
        return customerRepository.findAllBy(pageable).map(customerMapper::toDto);
    }

    /**
     * Returns the number of customers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return customerRepository.count();
    }

    /**
     * Returns the number of customers available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return customerSearchRepository.count();
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CustomerDTO> findOne(UUID id) {
        LOG.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id).map(customerMapper::toDto);
    }

    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Customer : {}", id);
        return customerRepository.deleteById(id).then(customerSearchRepository.deleteById(id));
    }

    /**
     * Search for the customer corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Customers for query {}", query);
        return customerSearchRepository.search(query, pageable).map(customerMapper::toDto);
    }

    // jhipster-needle-service-impl-add-method - JHipster will add methods here

    /**
     * Get customer orders
     *
     * @param customerId the customer ID
     * @return the list of order DTOs
     */
    @Transactional(readOnly = true)
    public Flux<OrderDTO> getCustomerOrders(UUID customerId) {
        LOG.debug("Request to get customer orders : {}", customerId);
        return orderRepository.findByCustomer(customerId).map(orderMapper::toDto);
    }

    /**
     * Get customer loyalty information
     *
     * @param customerId the customer ID
     * @return the customer loyalty DTO
     */
    @Transactional(readOnly = true)
    public Mono<CustomerLoyaltyDTO> getCustomerLoyalty(UUID customerId) {
        LOG.debug("Request to get customer loyalty : {}", customerId);
        return customerLoyaltyService
            .findByCustomer(customerId)
            .next()
            .switchIfEmpty(Mono.error(new RuntimeException("Customer loyalty not found")));
    }

    /**
     * Add loyalty points to a customer
     *
     * @param customerId the customer ID
     * @param request the points addition request
     * @return the updated customer loyalty DTO
     */
    public Mono<CustomerLoyaltyDTO> addLoyaltyPoints(UUID customerId, LoyaltyPointsRequestDTO request) {
        LOG.debug("Request to add loyalty points : {} - {}", customerId, request);
        return customerLoyaltyService
            .findByCustomer(customerId)
            .next()
            .switchIfEmpty(Mono.error(new RuntimeException("Customer loyalty not found")))
            .flatMap(loyalty -> {
                java.math.BigDecimal currentPoints = loyalty.getLoyaltyPoints() != null
                    ? loyalty.getLoyaltyPoints()
                    : java.math.BigDecimal.ZERO;
                loyalty.setLoyaltyPoints(currentPoints.add(request.getPoints()));
                loyalty.setLastPointsEarnedAt(java.time.Instant.now());
                return customerLoyaltyService.update(loyalty);
            });
    }
}
