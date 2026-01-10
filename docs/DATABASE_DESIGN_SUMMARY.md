# Database Design Summary - Restaurant Management System

## Executive Summary

This document provides a high-level summary of the database design for a multi-tenant Restaurant Management System (RMS). The design supports comprehensive restaurant operations including multi-branch management, role-based access control, menu management, table assignments, ordering (online/offline), POS billing, and payment processing.

## Design Principles

### 1. Multi-Tenancy

- **Approach**: Separate database per tenant (database-level isolation)
- **Benefit**: Complete data isolation, better security, independent scaling
- **Implementation**: Tenant context routing at application layer (no tenant_id columns needed)

### 2. Data Integrity

- **Primary Keys**: UUID for all tables (distributed system friendly)
- **Foreign Keys**: Enforced relationships with appropriate cascade rules
- **Constraints**: Check constraints for status values, positive amounts, etc.
- **Optimistic Locking**: Version columns in critical tables

### 3. Audit Trail

- **Standard Fields**: `created_at`, `updated_at`, `created_by`, `updated_by` in all tables
- **Status History**: `ORDER_STATUS_HISTORY` tracks order state changes
- **Sync Logging**: `USER_SYNC_LOG` tracks Gateway/Keycloak synchronization

### 4. Performance Optimization

- **Indexes**: Strategic indexes on foreign keys, status fields, date ranges
- **Denormalization**: Bill items store denormalized data for historical accuracy
- **Composite Indexes**: For common query patterns (branch + date, user + branch + role)

### 5. Soft Deletes

- **Approach**: `is_active` flags instead of hard deletes
- **Benefit**: Preserves historical data for reporting and audit

## Core Modules

### 1. Restaurant & Branch Management

**Tables**: `RESTAURANT`, `BRANCH`

- Multi-branch restaurant support
- Location tracking (GPS coordinates)
- Operating hours management
- Capacity tracking

### 2. User & Role Management

**Tables**: `USER`, `USER_BRANCH_ROLE`, `USER_SYNC_LOG`

- User synchronization with Gateway/Keycloak
- Role-based access control (8 roles: ADMIN, MANAGER, SUPERVISOR, WAITER, CASHIER, CHEF, CUSTOMER, ANONYMOUS)
- Branch-specific role assignments
- Sync status tracking and error handling

### 3. Table Management

**Tables**: `BRANCH_TABLE`, `SHIFT`, `TABLE_ASSIGNMENT`, `TABLE_WAITER_ASSIGNMENT`

- Physical table tracking with QR codes
- Daily shift-based assignments
- Supervisor and waiter mapping to tables
- Table status management (AVAILABLE, OCCUPIED, RESERVED, OUT_OF_SERVICE)

### 4. Menu Management

**Tables**: `MENU_CATEGORY`, `MENU_ITEM`, `MENU_ITEM_VARIANT`, `MENU_ITEM_ADDON`, `INVENTORY`

- Hierarchical menu structure (Restaurant → Category → Item)
- Support for eatables and beverages
- Cuisine types, vegetarian/non-vegetarian, alcoholic/non-alcoholic flags
- Size variants (Small, Medium, Large)
- Add-ons and customizations
- Inventory tracking per branch

### 5. Customer Management

**Tables**: `CUSTOMER`, `CUSTOMER_LOYALTY`

- Customer profiles (linked to USER for registered customers)
- Anonymous customer support
- Loyalty program tracking
- Points and tier management

### 6. Order Management

**Tables**: `ORDER`, `ORDER_ITEM`, `ORDER_ITEM_CUSTOMIZATION`, `ORDER_STATUS_HISTORY`

- Online and offline ordering
- Order types: ONLINE, OFFLINE, TAKEAWAY, DELIVERY
- Order status workflow: PENDING → CONFIRMED → PREPARING → READY → SERVED → COMPLETED
- Item customizations and variants
- Status change audit trail

### 7. Billing & Tax Management

**Tables**: `BILL`, `BILL_ITEM`, `TAX_CONFIG`, `BILL_TAX`, `DISCOUNT`, `BILL_DISCOUNT`

- Bill generation from orders
- Multiple tax configurations (GST, VAT, Service Tax)
- Discount and promotion support
- Denormalized bill data for historical accuracy
- Bill status: PENDING → PARTIALLY_PAID → PAID

### 8. Payment Processing

**Tables**: `PAYMENT`, `PAYMENT_METHOD`

- Multiple payment methods (Cash, Card, GPay, PhonePe, etc.)
- Payment status tracking
- Transaction ID storage
- Refund support
- Payment gateway response storage (JSONB)

## Key Design Decisions

### 1. Denormalization Strategy

**Decision**: Store denormalized data in `BILL_ITEM`, `BILL_TAX`, `BILL_DISCOUNT`
**Rationale**:

- Bills are immutable financial documents
- Source data (menu items, taxes) may change over time
- Historical accuracy is critical for financial reporting
- Performance: No joins needed for bill display

### 2. Table Assignment Model

**Decision**: Separate `TABLE_ASSIGNMENT` and `TABLE_WAITER_ASSIGNMENT` tables
**Rationale**:

- One supervisor per table assignment
- Multiple waiters can be assigned to same table
- Daily assignments with shift-based tracking
- Flexible reassignment without data loss

### 3. Order-Bill Relationship

**Decision**: One-to-one relationship (one order = one bill)
**Rationale**:

- Simplifies billing logic
- Clear audit trail
- Supports future split-billing if needed (via separate orders)

### 4. User Synchronization

**Decision**: Separate `USER` table with sync status tracking
**Rationale**:

- Decouples from Gateway/Keycloak availability
- Supports offline operations
- Tracks sync failures for retry
- Maintains local user data for performance

### 5. Menu Item Variants

**Decision**: Separate `MENU_ITEM_VARIANT` table instead of JSON
**Rationale**:

- Better query performance
- Easier filtering and searching
- Type-safe database constraints
- Supports variant-specific pricing

## Data Synchronization Strategy

### User Sync with Gateway/Keycloak

#### Sync Methods:

1. **Bulk Import**: Initial sync of all users
2. **Event-Driven**: Real-time webhook/event processing
3. **Scheduled Reconciliation**: Periodic sync to catch missed events

#### Sync Status:

- `PENDING`: Created locally, not synced
- `SYNCED`: Successfully synced
- `FAILED`: Sync failed, retry required
- `OUT_OF_SYNC`: Local changes not synced

#### Sync Logging:

- All sync operations logged in `USER_SYNC_LOG`
- Request/response payloads stored (JSONB)
- Error messages captured for debugging

## Scalability Considerations

### 1. Indexing Strategy

- **Foreign Key Indexes**: All foreign keys indexed
- **Status Indexes**: Frequently filtered status columns
- **Date Range Indexes**: Time-series queries (orders, bills, payments)
- **Composite Indexes**: Common query patterns

### 2. Partitioning (Future)

- Consider partitioning `ORDER`, `BILL`, `PAYMENT` by date
- Archive old data to separate tables/archives

### 3. Read Replicas

- Use read replicas for reporting queries
- Separate read/write workloads

### 4. Caching Strategy

- Cache menu items (frequently accessed, rarely changed)
- Cache user roles (reduce database queries)
- Cache tax configurations

## Security Considerations

### 1. Multi-Tenant Isolation

- Database-level isolation (separate database per tenant)
- No cross-tenant data access possible
- Tenant context validation on every request

### 2. Data Access Control

- Role-based access at application layer
- Branch-level data scoping
- User can only access assigned branches

### 3. Financial Data Protection

- Audit trail for all financial transactions
- Immutable bills (no updates after generation)
- Payment reconciliation tracking

### 4. User Data Privacy

- Soft deletes preserve data for compliance
- Sync logs for audit purposes
- GDPR considerations (data retention policies)

## Reporting & Analytics Support

### 1. Time-Series Queries

- Date-based indexes on ORDER, BILL, PAYMENT
- Efficient daily/monthly/yearly reports

### 2. Denormalized Data

- Bill data stored for historical reporting
- No need to join to historical menu/tax data

### 3. Status Tracking

- Order status history for workflow analysis
- Payment status for reconciliation

### 4. Customer Analytics

- Customer loyalty tracking
- Order history per customer
- Customer lifetime value calculations

## Migration Strategy

### Phase 1: Foundation (Week 1-2)

- RESTAURANT, BRANCH
- USER, USER_BRANCH_ROLE, USER_SYNC_LOG

### Phase 2: Menu & Inventory (Week 3-4)

- MENU_CATEGORY, MENU_ITEM, MENU_ITEM_VARIANT, MENU_ITEM_ADDON
- INVENTORY

### Phase 3: Table Management (Week 5)

- BRANCH_TABLE, SHIFT, TABLE_ASSIGNMENT, TABLE_WAITER_ASSIGNMENT

### Phase 4: Ordering (Week 6-7)

- CUSTOMER, CUSTOMER_LOYALTY
- ORDER, ORDER_ITEM, ORDER_ITEM_CUSTOMIZATION, ORDER_STATUS_HISTORY

### Phase 5: Billing & Payments (Week 8-9)

- TAX_CONFIG, DISCOUNT
- BILL, BILL_ITEM, BILL_TAX, BILL_DISCOUNT
- PAYMENT_METHOD, PAYMENT

## Testing Considerations

### 1. Data Integrity Tests

- Foreign key constraints
- Check constraints
- Unique constraints
- Cascade delete behavior

### 2. Performance Tests

- Index effectiveness
- Query performance
- Concurrent access patterns
- Large dataset handling

### 3. Sync Tests

- User synchronization scenarios
- Failure and retry handling
- Reconciliation accuracy

## Maintenance & Operations

### 1. Monitoring

- Database connection pool usage
- Query performance metrics
- Index usage statistics
- Table growth trends

### 2. Backup & Recovery

- Regular backups per tenant database
- Point-in-time recovery capability
- Disaster recovery procedures

### 3. Data Retention

- Archive old orders/bills after N months
- Retain active data for performance
- Compliance with data retention policies

## Future Enhancements

### 1. Advanced Features

- Table reservations system
- Kitchen display system (KDS) integration
- Delivery tracking
- Customer feedback/reviews
- Employee scheduling and attendance

### 2. Analytics Enhancements

- Real-time dashboards
- Predictive analytics
- Inventory optimization
- Demand forecasting

### 3. Integration Points

- Third-party payment gateways
- Inventory management systems
- Accounting software
- Marketing platforms

## Conclusion

This database design provides a robust, scalable, and maintainable foundation for a multi-tenant Restaurant Management System. The design balances normalization with performance, ensures data integrity, and supports comprehensive restaurant operations while maintaining flexibility for future enhancements.

**Key Strengths:**

- ✅ Complete multi-tenancy support
- ✅ Comprehensive role-based access control
- ✅ Flexible menu and inventory management
- ✅ Robust ordering and billing system
- ✅ Multiple payment method support
- ✅ Strong audit trail and data integrity
- ✅ Performance-optimized with strategic indexing
- ✅ Scalable architecture

**Total Tables**: 28
**Total Relationships**: 50+
**Estimated Initial Data Size**: ~100MB per tenant (with sample data)
**Growth Rate**: ~10-50MB per month per tenant (depending on order volume)
