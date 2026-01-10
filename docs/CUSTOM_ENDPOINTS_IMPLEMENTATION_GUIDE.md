# Custom Endpoints Implementation Guide

## Quick Reference for Code Placement

This guide shows exactly where to place custom code in JHipster-generated files.

---

## 1. REST Controller Pattern

### Generated Code Structure

```java
@RestController
@RequestMapping("/api")
public class OrderResource {

  private static final String ENTITY_NAME = "order";
  private final Logger log = LoggerFactory.getLogger(OrderResource.class);
  private final OrderService orderService;
  private final OrderMapper orderMapper;

  public OrderResource(OrderService orderService, OrderMapper orderMapper) {
    this.orderService = orderService;
    this.orderMapper = orderMapper;
  }

  /**
   * {@code GET /api/orders} : get all orders.
   */
  @GetMapping("/orders")
  public Mono<ResponseEntity<Flux<OrderDTO>>> getAllOrders(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
    // Generated code
  }

  /**
   * {@code GET /api/orders/:id} : get the "id" order.
   */
  @GetMapping("/orders/{id}")
  public Mono<ResponseEntity<OrderDTO>> getOrder(@PathVariable UUID id) {
    // Generated code
  }
  // jhipster-needle-rest-add-get-method - JHipster will add get methods here
  // jhipster-needle-rest-add-post-method - JHipster will add post methods here
  // jhipster-needle-rest-add-put-method - JHipster will add put methods here
  // jhipster-needle-rest-add-delete-method - JHipster will add delete methods here
  // jhipster-needle-rest-add-patch-method - JHipster will add patch methods here
}

```

### Add Custom Code Below Markers

```java
// jhipster-needle-rest-add-post-method - JHipster will add post methods here

/**
 * {@code POST /api/orders/create} : Create a new order
 *
 * @param orderRequest the order creation request
 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and order DTO
 */
@PostMapping("/orders/create")
public Mono<ResponseEntity<OrderDTO>> createOrder(@RequestBody OrderCreationRequestDTO orderRequest) {
  log.debug("REST request to create order : {}", orderRequest);
  return orderService.createOrder(orderRequest).map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
}

/**
 * {@code PUT /api/orders/{id}/status} : Update order status
 *
 * @param id the id of the order
 * @param request the status update request
 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and updated order DTO
 */
@PutMapping("/orders/{id}/status")
public Mono<ResponseEntity<OrderDTO>> updateOrderStatus(@PathVariable UUID id, @RequestBody OrderStatusUpdateRequestDTO request) {
  log.debug("REST request to update order status : {} - {}", id, request);
  return orderService.updateStatus(id, request).map(result -> ResponseEntity.ok().body(result));
}

```

---

## 2. Service Interface Pattern

### Generated Code Structure

```java
public interface OrderService {
  /**
   * Save a order.
   *
   * @param orderDTO the entity to save.
   * @return the persisted entity.
   */
  Mono<OrderDTO> save(OrderDTO orderDTO);

  /**
   * Get all the orders.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Flux<OrderDTO> findAll(Pageable pageable);

  /**
   * Get the "id" order.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Mono<OrderDTO> findOne(UUID id);

  /**
   * Delete the "id" order.
   *
   * @param id the id of the entity.
   * @return a Mono to signal the deletion
   */
  Mono<Void> delete(UUID id);
  // jhipster-needle-service-add-method - JHipster will add methods here
}

```

### Add Custom Methods Below Marker

```java
// jhipster-needle-service-add-method - JHipster will add methods here

/**
 * Create a new order with items
 *
 * @param request the order creation request
 * @return the created order DTO
 */
Mono<OrderDTO> createOrder(OrderCreationRequestDTO request);

/**
 * Update order status
 *
 * @param id the id of the order
 * @param request the status update request
 * @return the updated order DTO
 */
Mono<OrderDTO> updateStatus(UUID id, OrderStatusUpdateRequestDTO request);

/**
 * Cancel an order
 *
 * @param id the id of the order
 * @param request the cancellation request
 * @return the cancelled order DTO
 */
Mono<OrderDTO> cancelOrder(UUID id, OrderCancellationRequestDTO request);

/**
 * Find orders by branch and status
 *
 * @param branchId the branch ID
 * @param status the order status
 * @return the list of order DTOs
 */
Flux<OrderDTO> findByBranchIdAndStatus(UUID branchId, String status);

```

---

## 3. Service Implementation Pattern

### Generated Code Structure

```java
@Service
public class OrderServiceImpl implements OrderService {

  private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;

  public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
    this.orderRepository = orderRepository;
    this.orderMapper = orderMapper;
  }

  @Override
  public Mono<OrderDTO> save(OrderDTO orderDTO) {
    // Generated implementation
  }

  @Override
  public Flux<OrderDTO> findAll(Pageable pageable) {
    // Generated implementation
  }
  // jhipster-needle-service-impl-add-method - JHipster will add methods here
}

```

### Add Custom Implementation Below Marker

```java
// jhipster-needle-service-impl-add-method - JHipster will add methods here

@Override
public Mono<OrderDTO> createOrder(OrderCreationRequestDTO request) {
  log.debug("Request to create order : {}", request);

  // Business logic:
  // 1. Validate request
  // 2. Check menu item availability
  // 3. Calculate totals
  // 4. Create order entity
  // 5. Create order items
  // 6. Save order
  // 7. Update inventory
  // 8. Return DTO

  return orderRepository.save(order).map(orderMapper::toDto);
}

@Override
public Mono<OrderDTO> updateStatus(UUID id, OrderStatusUpdateRequestDTO request) {
  log.debug("Request to update order status : {} - {}", id, request);

  return orderRepository
    .findById(id)
    .switchIfEmpty(Mono.error(new EntityNotFoundException("Order not found")))
    .flatMap(order -> {
      // Validate status transition
      // Update status
      // Create status history entry
      // Save order
      return orderRepository.save(order);
    })
    .map(orderMapper::toDto);
}

```

---

## 4. Repository Pattern

### Generated Code Structure

```java
public interface OrderRepository extends ReactiveCrudRepository<Order, UUID> {
  // Custom query methods can be added here
}

```

### Add Custom Query Methods

```java
public interface OrderRepository extends ReactiveCrudRepository<Order, UUID> {
  /**
   * Find orders by branch ID and status
   */
  Flux<Order> findByBranchIdAndStatus(UUID branchId, String status);

  /**
   * Find orders by branch ID and date range
   */
  @Query("SELECT * FROM order WHERE branch_id = :branchId AND order_date BETWEEN :startDate AND :endDate")
  Flux<Order> findByBranchIdAndOrderDateBetween(
    @Param("branchId") UUID branchId,
    @Param("startDate") Instant startDate,
    @Param("endDate") Instant endDate
  );

  /**
   * Find orders by customer ID
   */
  Flux<Order> findByCustomerId(UUID customerId);

  /**
   * Find pending orders for a branch
   */
  Flux<Order> findByBranchIdAndStatusIn(UUID branchId, List<String> statuses);
}

```

---

## 5. Complete Example: Order Resource

### OrderResource.java (Complete)

```java
package com.atparui.rmsservice.web.rest;

import com.atparui.rmsservice.service.OrderService;
import com.atparui.rmsservice.service.dto.OrderCancellationRequestDTO;
import com.atparui.rmsservice.service.dto.OrderCreationRequestDTO;
import com.atparui.rmsservice.service.dto.OrderDTO;
import com.atparui.rmsservice.service.dto.OrderStatusUpdateRequestDTO;
import com.atparui.rmsservice.service.dto.OrderWithItemsDTO;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class OrderResource {

  private static final String ENTITY_NAME = "order";
  private static final Logger log = LoggerFactory.getLogger(OrderResource.class);
  private final OrderService orderService;

  public OrderResource(OrderService orderService) {
    this.orderService = orderService;
  }

  // ... Generated CRUD methods ...

  // jhipster-needle-rest-add-post-method - JHipster will add post methods here

  /**
   * {@code POST /api/orders/create} : Create a new order
   *
   * @param orderRequest the order creation request
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and order DTO
   */
  @PostMapping("/orders/create")
  public Mono<ResponseEntity<OrderDTO>> createOrder(@RequestBody OrderCreationRequestDTO orderRequest) {
    log.debug("REST request to create order : {}", orderRequest);
    return orderService.createOrder(orderRequest).map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
  }

  // jhipster-needle-rest-add-put-method - JHipster will add put methods here

  /**
   * {@code PUT /api/orders/{id}/status} : Update order status
   *
   * @param id the id of the order
   * @param request the status update request
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and updated order DTO
   */
  @PutMapping("/orders/{id}/status")
  public Mono<ResponseEntity<OrderDTO>> updateOrderStatus(@PathVariable UUID id, @RequestBody OrderStatusUpdateRequestDTO request) {
    log.debug("REST request to update order status : {} - {}", id, request);
    return orderService.updateStatus(id, request).map(result -> ResponseEntity.ok().body(result));
  }

  /**
   * {@code PUT /api/orders/{id}/cancel} : Cancel an order
   *
   * @param id the id of the order
   * @param request the cancellation request
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and cancelled order DTO
   */
  @PutMapping("/orders/{id}/cancel")
  public Mono<ResponseEntity<OrderDTO>> cancelOrder(@PathVariable UUID id, @RequestBody OrderCancellationRequestDTO request) {
    log.debug("REST request to cancel order : {} - {}", id, request);
    return orderService.cancelOrder(id, request).map(result -> ResponseEntity.ok().body(result));
  }

  // jhipster-needle-rest-add-get-method - JHipster will add get methods here

  /**
   * {@code GET /api/orders/branch/{branchId}/status/{status}} : Get orders by status
   *
   * @param branchId the branch ID
   * @param status the order status
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and list of orders
   */
  @GetMapping("/orders/branch/{branchId}/status/{status}")
  public Mono<ResponseEntity<List<OrderDTO>>> getOrdersByStatus(@PathVariable UUID branchId, @PathVariable String status) {
    log.debug("REST request to get orders by status : {} - {}", branchId, status);
    return orderService.findByBranchIdAndStatus(branchId, status).collectList().map(result -> ResponseEntity.ok().body(result));
  }

  /**
   * {@code GET /api/orders/branch/{branchId}/date-range} : Get orders by date range
   *
   * @param branchId the branch ID
   * @param startDate the start date
   * @param endDate the end date
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and list of orders
   */
  @GetMapping("/orders/branch/{branchId}/date-range")
  public Mono<ResponseEntity<List<OrderDTO>>> getOrdersByDateRange(
    @PathVariable UUID branchId,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
  ) {
    log.debug("REST request to get orders by date range : {} - {} to {}", branchId, startDate, endDate);
    return orderService
      .findByBranchIdAndDateRange(branchId, startDate, endDate)
      .collectList()
      .map(result -> ResponseEntity.ok().body(result));
  }

  /**
   * {@code GET /api/orders/{id}/with-items} : Get order with items
   *
   * @param id the id of the order
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and order with items DTO
   */
  @GetMapping("/orders/{id}/with-items")
  public Mono<ResponseEntity<OrderWithItemsDTO>> getOrderWithItems(@PathVariable UUID id) {
    log.debug("REST request to get order with items : {}", id);
    return orderService
      .findOrderWithItems(id)
      .map(result -> ResponseEntity.ok().body(result))
      .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }
}

```

---

## 6. Security Annotations

Add role-based security to custom endpoints:

```java
@PostMapping("/orders/create")
@PreAuthorize("hasAnyAuthority('ROLE_WAITER', 'ROLE_CUSTOMER', 'ROLE_MANAGER')")
public Mono<ResponseEntity<OrderDTO>> createOrder(@RequestBody OrderCreationRequestDTO orderRequest) {
  // Implementation
}

@PutMapping("/orders/{id}/status")
@PreAuthorize("hasAnyAuthority('ROLE_SUPERVISOR', 'ROLE_MANAGER', 'ROLE_CHEF')")
public Mono<ResponseEntity<OrderDTO>> updateOrderStatus(@PathVariable UUID id, @RequestBody OrderStatusUpdateRequestDTO request) {
  // Implementation
}

@PostMapping("/bills/generate")
@PreAuthorize("hasAnyAuthority('ROLE_CASHIER', 'ROLE_MANAGER')")
public Mono<ResponseEntity<BillDTO>> generateBill(@RequestBody BillGenerationRequestDTO request) {
  // Implementation
}

```

---

## 7. Error Handling

Use existing exception handling:

```java
@PostMapping("/orders/create")
public Mono<ResponseEntity<OrderDTO>> createOrder(@RequestBody OrderCreationRequestDTO orderRequest) {
  log.debug("REST request to create order : {}", orderRequest);
  return orderService
    .createOrder(orderRequest)
    .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result))
    .onErrorResume(EntityNotFoundException.class, ex -> {
      log.error("Order creation failed: {}", ex.getMessage());
      return Mono.just(ResponseEntity.badRequest().build());
    })
    .onErrorResume(Exception.class, ex -> {
      log.error("Unexpected error: {}", ex.getMessage(), ex);
      return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    });
}

```

---

## 8. Validation

Add validation to request DTOs:

```java
public class OrderCreationRequestDTO implements Serializable {

  @NotNull
  private UUID branchId;

  @NotNull
  private UUID customerId;

  @NotNull
  @Size(min = 1)
  private List<OrderItemRequestDTO> items;

  @NotNull
  private String orderType; // ONLINE, OFFLINE, TAKEAWAY, DELIVERY
  // Getters and setters
}

```

---

## 9. Testing Custom Endpoints

Create integration tests:

```java
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderResourceIT {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void createOrder() throws Exception {
    OrderCreationRequestDTO request = new OrderCreationRequestDTO();
    // Set request properties

    webTestClient
      .post()
      .uri("/api/orders/create")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus()
      .isCreated()
      .expectHeader()
      .exists(HttpHeaders.LOCATION)
      .expectBody(OrderDTO.class);
  }
}

```

---

## 10. Checklist for Implementation

- [ ] Add custom endpoint below JHipster marker
- [ ] Add corresponding service method in interface
- [ ] Implement service method in service implementation
- [ ] Add repository query methods if needed
- [ ] Create request/response DTOs
- [ ] Add validation annotations
- [ ] Add security annotations (`@PreAuthorize`)
- [ ] Add logging
- [ ] Add error handling
- [ ] Write integration tests
- [ ] Update API documentation (Swagger)

---

## 11. Common Patterns

### Pattern 1: Status Update

```java
@PutMapping("/{id}/status")
public Mono<ResponseEntity<EntityDTO>> updateStatus(@PathVariable UUID id, @RequestBody StatusUpdateRequestDTO request) {
  return service.updateStatus(id, request).map(result -> ResponseEntity.ok().body(result));
}

```

### Pattern 2: Find by Filter

```java
@GetMapping("/branch/{branchId}/filter")
public Mono<ResponseEntity<List<EntityDTO>>> findByFilter(@PathVariable UUID branchId, @RequestParam(required = false) String filter) {
  return service.findByFilter(branchId, filter).collectList().map(result -> ResponseEntity.ok().body(result));
}

```

### Pattern 3: Date Range Query

```java
@GetMapping("/branch/{branchId}/date-range")
public Mono<ResponseEntity<List<EntityDTO>>> findByDateRange(
  @PathVariable UUID branchId,
  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
) {
  return service.findByDateRange(branchId, startDate, endDate).collectList().map(result -> ResponseEntity.ok().body(result));
}

```

---

## Summary

1. **Always place custom code below JHipster markers**
2. **Follow reactive patterns** (Mono/Flux)
3. **Add proper logging** for debugging
4. **Add security annotations** for access control
5. **Validate inputs** using Jakarta Bean Validation
6. **Handle errors** gracefully
7. **Write tests** for all custom endpoints
8. **Document endpoints** in Swagger/OpenAPI

This ensures your custom code is preserved when regenerating entities with JHipster.
