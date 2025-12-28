# JDL Structure Notes and Considerations

## Important Notes

### 1. Entity Naming

- **RmsUser**: Named to avoid conflict with JHipster's built-in `User` entity
- All entity names follow PascalCase convention
- Table names will be converted to snake_case automatically (e.g., `BranchTable` → `branch_table`)

### 2. Field Types Mapping

| JDL Type     | Java Type    | Database Type   | Notes                       |
| ------------ | ------------ | --------------- | --------------------------- |
| `String`     | `String`     | `VARCHAR(n)`    | Use `maxlength(n)` for size |
| `Integer`    | `Integer`    | `INTEGER`       | For whole numbers           |
| `BigDecimal` | `BigDecimal` | `DECIMAL(10,2)` | For monetary values         |
| `Boolean`    | `Boolean`    | `BOOLEAN`       | Defaults to `false`         |
| `Instant`    | `Instant`    | `TIMESTAMP`     | For timestamps              |
| `LocalDate`  | `LocalDate`  | `DATE`          | For dates only              |
| `LocalTime`  | `LocalTime`  | `TIME`          | For time only               |
| `TextBlob`   | `String`     | `TEXT`          | For large text fields       |

### 3. Relationships

**R2DBC Limitations:**

- R2DBC doesn't support JPA-style bidirectional relationships
- All relationships are unidirectional `ManyToOne`
- Junction tables (like `UserBranchRole`) are separate entities
- Foreign keys are managed through `@Column` annotations in generated code

**Relationship Pattern:**

```jdl
relationship ManyToOne {
    Child{parent} to Parent
}
```

This creates:

- `parentId` field in `Child` entity
- Foreign key constraint in database
- Manual join queries in repositories (not automatic like JPA)

### 4. Audit Fields

**Not in JDL:**

- `createdAt` / `createdDate`
- `updatedAt` / `lastModifiedDate`
- `createdBy`
- `updatedBy` / `lastModifiedBy`
- `version` (for optimistic locking)

**Why:**

- These are inherited from `AbstractAuditingEntity`
- JHipster automatically adds audit fields for entities
- Custom fields can be added manually after generation

### 5. Unique Constraints

**In JDL:**

```jdl
code String required unique maxlength(50)
```

**Composite Unique Constraints:**
Not directly supported in JDL. Add manually in Liquibase changelog:

```xml
<addUniqueConstraint
    tableName="branch"
    columnNames="restaurant_id, code"
    constraintName="uk_branch_restaurant_code"/>
```

### 6. Check Constraints

**Not in JDL:**
Add manually in Liquibase changelog:

```xml
<addCheckConstraint
    tableName="order"
    constraintName="chk_order_status"
    checkCondition="status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'SERVED', 'CANCELLED', 'COMPLETED')"/>
```

### 7. Default Values

**In JDL:**

```jdl
isActive Boolean  // Defaults to false
```

**For other defaults:**
Add manually in Liquibase changelog:

```xml
<addDefaultValue
    tableName="restaurant"
    columnName="is_active"
    defaultValueBoolean="true"/>
```

### 8. Indexes

**Not in JDL:**
Add manually in Liquibase changelog:

```xml
<createIndex
    tableName="order"
    indexName="idx_order_branch_date">
    <column name="branch_id"/>
    <column name="order_date"/>
</createIndex>
```

## Generated Code Structure

### Entity Class Example

```java
@Table("restaurant")
public class Restaurant extends AbstractAuditingEntity<UUID> implements Serializable, Persistable<UUID> {

  @Id
  private UUID id;

  @Column("name")
  @NotNull
  @Size(max = 255)
  private String name;
  // ... other fields
}

```

### Repository Example

```java
public interface RestaurantRepository extends ReactiveCrudRepository<Restaurant, UUID> {
  // Custom queries can be added here
  Flux<Restaurant> findByIsActiveTrue();
}

```

### Service Example

```java
public interface RestaurantService {
  Mono<RestaurantDTO> save(RestaurantDTO restaurantDTO);
  Flux<RestaurantDTO> findAll(Pageable pageable);
  Mono<RestaurantDTO> findOne(UUID id);
  Mono<Void> delete(UUID id);
}

```

### Controller Example

```java
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantResource {

  @GetMapping
  public Mono<ResponseEntity<Flux<RestaurantDTO>>> getAllRestaurants(Pageable pageable) {
    // Implementation
  }
}

```

## Post-Generation Tasks

### 1. Add Composite Unique Constraints

Edit the generated Liquibase changelog:

```xml
<!-- Add after table creation -->
<addUniqueConstraint
    tableName="branch"
    columnNames="restaurant_id, code"
    constraintName="uk_branch_restaurant_code"/>
```

### 2. Add Check Constraints

```xml
<addCheckConstraint
    tableName="branch_table"
    constraintName="chk_branch_table_status"
    checkCondition="status IN ('AVAILABLE', 'OCCUPIED', 'RESERVED', 'OUT_OF_SERVICE')"/>
```

### 3. Add Indexes

```xml
<createIndex
    tableName="order"
    indexName="idx_order_branch_date">
    <column name="branch_id"/>
    <column name="order_date"/>
</createIndex>
```

### 4. Add Default Values

```xml
<addDefaultValue
    tableName="restaurant"
    columnName="is_active"
    defaultValueBoolean="true"/>
```

### 5. Customize Repositories

Add custom query methods:

```java
public interface OrderRepository extends ReactiveCrudRepository<Order, UUID> {
  Flux<Order> findByBranchIdAndStatus(UUID branchId, String status);
  Flux<Order> findByBranchIdAndOrderDateBetween(UUID branchId, Instant startDate, Instant endDate);
}

```

### 6. Add Business Logic

In service implementations:

```java
public Mono<OrderDTO> createOrder(OrderDTO orderDTO) {
  // Custom business logic
  // Validation
  // Calculations
  // etc.
}

```

## R2DBC-Specific Considerations

### 1. No Lazy Loading

- All relationships must be explicitly joined
- Use repository methods with joins:

```java
Flux<Order> findByBranchId(UUID branchId);
// Then manually fetch related entities if needed

```

### 2. Manual Joins

```java
// In repository
@Query("SELECT o.* FROM order o WHERE o.branch_id = :branchId")
Flux<Order> findByBranchIdWithDetails(@Param("branchId") UUID branchId);

```

### 3. Transaction Management

- Use `@Transactional` for reactive transactions
- Transactions are managed per operation

### 4. Batch Operations

- Use `Flux` for batch inserts/updates
- R2DBC supports reactive batch operations

## Validation

### JDL Validation

```jdl
name String required maxlength(255)  // @NotNull, @Size(max = 255)
email String unique                  // @Column(unique = true)
price BigDecimal required min(0)     // @NotNull, @Min(0)
```

### Custom Validation

Add after generation:

```java
@Column("email")
@Email
@Size(max = 255)
private String email;

```

## Testing

### Generated Tests

- `EntityNameTest.java` - Entity validation tests
- `EntityNameResourceIT.java` - REST API integration tests
- `EntityNameServiceIT.java` - Service integration tests

### Custom Tests

Add domain-specific tests:

```java
@Test
void testOrderStatusWorkflow() {
  // Test order status transitions
}

```

## Migration Strategy

### Phase 1: Core Entities

1. Restaurant
2. Branch
3. RmsUser

### Phase 2: Supporting Entities

4. UserBranchRole
5. UserSyncLog
6. BranchTable
7. Shift

### Phase 3: Menu & Inventory

8. MenuCategory
9. MenuItem
10. MenuItemVariant
11. MenuItemAddon
12. Inventory

### Phase 4: Customer & Orders

13. Customer
14. CustomerLoyalty
15. Order
16. OrderItem
17. OrderItemCustomization
18. OrderStatusHistory

### Phase 5: Billing & Payments

19. TaxConfig
20. Discount
21. Bill
22. BillItem
23. BillTax
24. BillDiscount
25. PaymentMethod
26. Payment

## Gateway Integration

Since the gateway creates database schemas:

1. **Export Changelogs**: Generated Liquibase changelogs are ready
2. **Gateway API**: Gateway calls Liquibase API with changelogs
3. **Schema Creation**: Database schema is created per tenant
4. **Service Ready**: Service can use entities once schema exists

## Common Issues and Solutions

### Issue: Entity not extending AbstractAuditingEntity

**Solution**: Manually edit entity class to extend `AbstractAuditingEntity<UUID>`

### Issue: Foreign key relationships not working

**Solution**: R2DBC requires manual joins. Add custom repository methods.

### Issue: Unique constraints not applied

**Solution**: Add composite unique constraints manually in Liquibase changelog

### Issue: Default values not set

**Solution**: Add default values manually in Liquibase changelog

### Issue: Indexes missing

**Solution**: Add indexes manually in Liquibase changelog for performance

## Best Practices

1. **Review Generated Code**: Always review before committing
2. **Incremental Generation**: Generate entities in phases
3. **Version Control**: Commit JDL files and generated code separately
4. **Custom Code**: Keep custom code separate to avoid overwrites
5. **Testing**: Write tests for custom business logic
6. **Documentation**: Update API docs after adding entities

## References

- [JHipster JDL Syntax](https://www.jhipster.tech/jdl/)
- [Spring Data R2DBC](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/)
- [Liquibase Documentation](https://docs.liquibase.com/)
