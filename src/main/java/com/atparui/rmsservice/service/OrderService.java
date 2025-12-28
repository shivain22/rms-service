package com.atparui.rmsservice.service;

import com.atparui.rmsservice.domain.Order;
import com.atparui.rmsservice.repository.OrderItemRepository;
import com.atparui.rmsservice.repository.OrderRepository;
import com.atparui.rmsservice.repository.search.OrderSearchRepository;
import com.atparui.rmsservice.service.dto.OrderCancellationRequestDTO;
import com.atparui.rmsservice.service.dto.OrderCreationRequestDTO;
import com.atparui.rmsservice.service.dto.OrderDTO;
import com.atparui.rmsservice.service.dto.OrderItemDTO;
import com.atparui.rmsservice.service.dto.OrderStatusUpdateRequestDTO;
import com.atparui.rmsservice.service.dto.OrderWithItemsDTO;
import com.atparui.rmsservice.service.mapper.OrderItemMapper;
import com.atparui.rmsservice.service.mapper.OrderMapper;
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
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Order}.
 */
@Service
@Transactional
public class OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final OrderSearchRepository orderSearchRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderItemMapper orderItemMapper;

    public OrderService(
        OrderRepository orderRepository,
        OrderMapper orderMapper,
        OrderSearchRepository orderSearchRepository,
        OrderItemRepository orderItemRepository,
        OrderItemMapper orderItemMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderSearchRepository = orderSearchRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * Save a order.
     *
     * @param orderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderDTO> save(OrderDTO orderDTO) {
        LOG.debug("Request to save Order : {}", orderDTO);
        return orderRepository.save(orderMapper.toEntity(orderDTO)).flatMap(orderSearchRepository::save).map(orderMapper::toDto);
    }

    /**
     * Update a order.
     *
     * @param orderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderDTO> update(OrderDTO orderDTO) {
        LOG.debug("Request to update Order : {}", orderDTO);
        return orderRepository
            .save(orderMapper.toEntity(orderDTO).setIsPersisted())
            .flatMap(orderSearchRepository::save)
            .map(orderMapper::toDto);
    }

    /**
     * Partially update a order.
     *
     * @param orderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrderDTO> partialUpdate(OrderDTO orderDTO) {
        LOG.debug("Request to partially update Order : {}", orderDTO);

        return orderRepository
            .findById(orderDTO.getId())
            .map(existingOrder -> {
                orderMapper.partialUpdate(existingOrder, orderDTO);

                return existingOrder;
            })
            .flatMap(orderRepository::save)
            .flatMap(savedOrder -> {
                orderSearchRepository.save(savedOrder);
                return Mono.just(savedOrder);
            })
            .map(orderMapper::toDto);
    }

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Orders");
        return orderRepository.findAllBy(pageable).map(orderMapper::toDto);
    }

    /**
     * Returns the number of orders available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return orderRepository.count();
    }

    /**
     * Returns the number of orders available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return orderSearchRepository.count();
    }

    /**
     * Get one order by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrderDTO> findOne(UUID id) {
        LOG.debug("Request to get Order : {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    /**
     * Delete the order by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Order : {}", id);
        return orderRepository.deleteById(id).then(orderSearchRepository.deleteById(id));
    }

    /**
     * Search for the order corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Orders for query {}", query);
        return orderSearchRepository.search(query, pageable).map(orderMapper::toDto);
    }

    // jhipster-needle-service-impl-add-method - JHipster will add methods here

    /**
     * Find orders by branch ID and status
     *
     * @param branchId the branch ID
     * @param status the order status
     * @return the list of order DTOs
     */
    @Transactional(readOnly = true)
    public Flux<OrderDTO> findByBranchIdAndStatus(UUID branchId, String status) {
        LOG.debug("Request to find Orders by branch ID and status : {} - {}", branchId, status);
        return orderRepository.findByBranchIdAndStatus(branchId, status).map(orderMapper::toDto);
    }

    /**
     * Find orders by branch ID and date range
     *
     * @param branchId the branch ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of order DTOs
     */
    @Transactional(readOnly = true)
    public Flux<OrderDTO> findByBranchIdAndDateRange(UUID branchId, Instant startDate, Instant endDate) {
        LOG.debug("Request to find Orders by branch ID and date range : {} - {} to {}", branchId, startDate, endDate);
        return orderRepository.findByBranchIdAndOrderDateBetween(branchId, startDate, endDate).map(orderMapper::toDto);
    }

    /**
     * Find order with all items and customizations
     *
     * @param id the order ID
     * @return the order with items DTO
     */
    @Transactional(readOnly = true)
    public Mono<OrderWithItemsDTO> findOrderWithItems(UUID id) {
        LOG.debug("Request to find Order with items : {}", id);
        return orderRepository
            .findById(id)
            .map(orderMapper::toDto)
            .flatMap(orderDTO -> {
                OrderWithItemsDTO orderWithItems = new OrderWithItemsDTO();
                // Copy all fields from OrderDTO to OrderWithItemsDTO
                orderWithItems.setId(orderDTO.getId());
                orderWithItems.setOrderNumber(orderDTO.getOrderNumber());
                orderWithItems.setOrderType(orderDTO.getOrderType());
                orderWithItems.setOrderSource(orderDTO.getOrderSource());
                orderWithItems.setStatus(orderDTO.getStatus());
                orderWithItems.setOrderDate(orderDTO.getOrderDate());
                orderWithItems.setEstimatedReadyTime(orderDTO.getEstimatedReadyTime());
                orderWithItems.setActualReadyTime(orderDTO.getActualReadyTime());
                orderWithItems.setSpecialInstructions(orderDTO.getSpecialInstructions());
                orderWithItems.setSubtotal(orderDTO.getSubtotal());
                orderWithItems.setTaxAmount(orderDTO.getTaxAmount());
                orderWithItems.setDiscountAmount(orderDTO.getDiscountAmount());
                orderWithItems.setTotalAmount(orderDTO.getTotalAmount());
                orderWithItems.setIsPaid(orderDTO.getIsPaid());
                orderWithItems.setCancelledAt(orderDTO.getCancelledAt());
                orderWithItems.setCancelledBy(orderDTO.getCancelledBy());
                orderWithItems.setCancellationReason(orderDTO.getCancellationReason());
                orderWithItems.setBranch(orderDTO.getBranch());
                orderWithItems.setCustomer(orderDTO.getCustomer());
                orderWithItems.setUser(orderDTO.getUser());
                orderWithItems.setBranchTable(orderDTO.getBranchTable());

                return orderItemRepository
                    .findByOrder(id)
                    .map(orderItem -> {
                        OrderWithItemsDTO.OrderItemWithDetailsDTO itemDTO = new OrderWithItemsDTO.OrderItemWithDetailsDTO();
                        OrderItemDTO baseDTO = orderItemMapper.toDto(orderItem);
                        itemDTO.setId(baseDTO.getId());
                        itemDTO.setQuantity(baseDTO.getQuantity());
                        itemDTO.setUnitPrice(baseDTO.getUnitPrice());
                        itemDTO.setItemTotal(baseDTO.getItemTotal());
                        itemDTO.setSpecialInstructions(baseDTO.getSpecialInstructions());
                        itemDTO.setStatus(baseDTO.getStatus());
                        itemDTO.setOrder(baseDTO.getOrder());
                        itemDTO.setMenuItem(baseDTO.getMenuItem());
                        itemDTO.setMenuItemVariant(baseDTO.getMenuItemVariant());
                        return itemDTO;
                    })
                    .collectList()
                    .map(items -> {
                        orderWithItems.setItems(items);
                        return orderWithItems;
                    });
            });
    }

    /**
     * Create a new order with items
     *
     * @param request the order creation request
     * @return the created order DTO
     */
    public Mono<OrderDTO> createOrder(OrderCreationRequestDTO request) {
        LOG.debug("Request to create order : {}", request);
        // TODO: Implement full order creation logic with items
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(UUID.randomUUID());

        // Set nested DTOs with IDs
        if (request.getBranchId() != null) {
            com.atparui.rmsservice.service.dto.BranchDTO branchDTO = new com.atparui.rmsservice.service.dto.BranchDTO();
            branchDTO.setId(request.getBranchId());
            orderDTO.setBranch(branchDTO);
        }
        if (request.getCustomerId() != null) {
            com.atparui.rmsservice.service.dto.CustomerDTO customerDTO = new com.atparui.rmsservice.service.dto.CustomerDTO();
            customerDTO.setId(request.getCustomerId());
            orderDTO.setCustomer(customerDTO);
        }
        if (request.getUserId() != null) {
            com.atparui.rmsservice.service.dto.RmsUserDTO userDTO = new com.atparui.rmsservice.service.dto.RmsUserDTO();
            userDTO.setId(request.getUserId());
            orderDTO.setUser(userDTO);
        }
        if (request.getBranchTableId() != null) {
            com.atparui.rmsservice.service.dto.BranchTableDTO branchTableDTO = new com.atparui.rmsservice.service.dto.BranchTableDTO();
            branchTableDTO.setId(request.getBranchTableId());
            orderDTO.setBranchTable(branchTableDTO);
        }

        orderDTO.setOrderType(request.getOrderType());
        orderDTO.setOrderDate(Instant.now());
        orderDTO.setStatus("PENDING");
        orderDTO.setOrderSource("MANUAL");
        orderDTO.setSpecialInstructions(request.getSpecialInstructions());
        // Generate order number
        orderDTO.setOrderNumber("ORD-" + System.currentTimeMillis());
        return save(orderDTO);
    }

    /**
     * Update order status
     *
     * @param id the id of the order
     * @param request the status update request
     * @return the updated order DTO
     */
    public Mono<OrderDTO> updateStatus(UUID id, OrderStatusUpdateRequestDTO request) {
        LOG.debug("Request to update order status : {} - {}", id, request);
        return orderRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Order not found")))
            .map(order -> {
                order.setStatus(request.getStatus());
                return order;
            })
            .flatMap(orderRepository::save)
            .flatMap(savedOrder -> {
                orderSearchRepository.save(savedOrder);
                return Mono.just(savedOrder);
            })
            .map(orderMapper::toDto);
    }

    /**
     * Cancel an order
     *
     * @param id the id of the order
     * @param request the cancellation request
     * @return the cancelled order DTO
     */
    public Mono<OrderDTO> cancelOrder(UUID id, OrderCancellationRequestDTO request) {
        LOG.debug("Request to cancel order : {} - {}", id, request);
        return orderRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Order not found")))
            .map(order -> {
                order.setStatus("CANCELLED");
                order.setCancelledAt(Instant.now());
                order.setCancellationReason(request.getCancellationReason());
                return order;
            })
            .flatMap(orderRepository::save)
            .flatMap(savedOrder -> {
                orderSearchRepository.save(savedOrder);
                return Mono.just(savedOrder);
            })
            .map(orderMapper::toDto);
    }
}
