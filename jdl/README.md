# Restaurant Management System - JHipster JDL Files

## Quick Start

### Generate All Entities

```bash
jhipster import-jdl jdl/rms-entities.jdl
```

### Generate by Module

```bash
# Import in order (dependencies first)
jhipster import-jdl jdl/01-restaurant-branch.jdl
jhipster import-jdl jdl/02-user-management.jdl
jhipster import-jdl jdl/03-table-management.jdl
jhipster import-jdl jdl/04-menu-management.jdl
jhipster import-jdl jdl/05-customer-management.jdl
jhipster import-jdl jdl/06-order-management.jdl
jhipster import-jdl jdl/07-billing-payments.jdl
```

## File Organization

| File                         | Entities                                                                           | Count |
| ---------------------------- | ---------------------------------------------------------------------------------- | ----- |
| `rms-entities.jdl`           | All entities (main file)                                                           | 28    |
| `01-restaurant-branch.jdl`   | Restaurant, Branch                                                                 | 2     |
| `02-user-management.jdl`     | RmsUser, UserBranchRole, UserSyncLog                                               | 3     |
| `03-table-management.jdl`    | BranchTable, Shift, TableAssignment, TableWaiterAssignment                         | 4     |
| `04-menu-management.jdl`     | MenuCategory, MenuItem, MenuItemVariant, MenuItemAddon, Inventory                  | 5     |
| `05-customer-management.jdl` | Customer, CustomerLoyalty                                                          | 2     |
| `06-order-management.jdl`    | Order, OrderItem, OrderItemCustomization, OrderStatusHistory                       | 4     |
| `07-billing-payments.jdl`    | TaxConfig, Discount, Bill, BillItem, BillTax, BillDiscount, PaymentMethod, Payment | 8     |

## Entity List

### Core Entities (8)

1. Restaurant
2. Branch
3. RmsUser
4. Customer
5. BranchTable
6. MenuItem
7. Order
8. Bill

### Supporting Entities (20)

9. UserBranchRole
10. UserSyncLog
11. Shift
12. TableAssignment
13. TableWaiterAssignment
14. MenuCategory
15. MenuItemVariant
16. MenuItemAddon
17. Inventory
18. CustomerLoyalty
19. OrderItem
20. OrderItemCustomization
21. OrderStatusHistory
22. TaxConfig
23. Discount
24. BillItem
25. BillTax
26. BillDiscount
27. PaymentMethod
28. Payment

## What Gets Generated

For each entity:

- ✅ Entity class with R2DBC annotations
- ✅ Repository interface (ReactiveCrudRepository)
- ✅ DTO class
- ✅ MapStruct mapper
- ✅ Service interface
- ✅ Service implementation
- ✅ REST controller
- ✅ Liquibase changelog
- ✅ Unit tests
- ✅ Integration tests

## Features Enabled

- ✅ **DTOs**: All entities with MapStruct
- ✅ **Services**: All entities with service classes
- ✅ **Pagination**: Main entities (Restaurant, Branch, Order, Bill, etc.)
- ✅ **Search**: Frequently searched entities (Elasticsearch)

## Important Notes

1. **RmsUser**: Named to avoid conflict with JHipster's User entity
2. **Audit Fields**: Automatically added via AbstractAuditingEntity
3. **Relationships**: All ManyToOne (R2DBC limitation)
4. **Post-Generation**: Add indexes, constraints, defaults in Liquibase

## Documentation

- [JDL Usage Guide](JDL_USAGE_GUIDE.md) - Detailed usage instructions
- [JDL Structure Notes](JDL_STRUCTURE_NOTES.md) - Technical details and considerations

## Next Steps

After generating entities:

1. Review generated code
2. Add composite unique constraints (Liquibase)
3. Add check constraints (Liquibase)
4. Add indexes (Liquibase)
5. Add default values (Liquibase)
6. Customize repositories with custom queries
7. Add business logic to services
8. Run tests: `mvn test`
9. Update API documentation

## Gateway Integration

The gateway will:

1. Receive Liquibase changelogs
2. Create database schema per tenant
3. Service can then use entities

Generated changelogs are ready for gateway integration.
