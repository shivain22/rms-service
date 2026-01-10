# Database Quick Reference Guide

## Table Index

### Core Entities (8)

1. **RESTAURANT** - Restaurant chain/brand
2. **BRANCH** - Physical restaurant location
3. **USER** - System users (synced from Gateway/Keycloak)
4. **CUSTOMER** - Restaurant customers
5. **BRANCH_TABLE** - Physical tables in branches
6. **MENU_ITEM** - Menu items (food & beverages)
7. **ORDER** - Customer orders
8. **BILL** - Generated bills from orders

### User & Role Management (3)

- **USER** - User accounts
- **USER_BRANCH_ROLE** - User-branch-role assignments
- **USER_SYNC_LOG** - Gateway/Keycloak sync logs

### Table Management (4)

- **BRANCH_TABLE** - Physical tables
- **SHIFT** - Work shifts
- **TABLE_ASSIGNMENT** - Table-supervisor assignments
- **TABLE_WAITER_ASSIGNMENT** - Table-waiter assignments

### Menu Management (5)

- **MENU_CATEGORY** - Menu categories
- **MENU_ITEM** - Menu items
- **MENU_ITEM_VARIANT** - Size/variant options
- **MENU_ITEM_ADDON** - Add-on options
- **INVENTORY** - Stock levels

### Order Management (4)

- **ORDER** - Orders
- **ORDER_ITEM** - Order line items
- **ORDER_ITEM_CUSTOMIZATION** - Item customizations
- **ORDER_STATUS_HISTORY** - Order status changes

### Billing & Payments (8)

- **TAX_CONFIG** - Tax configurations
- **DISCOUNT** - Discount/promotion rules
- **BILL** - Bills
- **BILL_ITEM** - Bill line items
- **BILL_TAX** - Tax breakdown
- **BILL_DISCOUNT** - Applied discounts
- **PAYMENT_METHOD** - Payment methods
- **PAYMENT** - Payment transactions

### Customer Management (2)

- **CUSTOMER** - Customer profiles
- **CUSTOMER_LOYALTY** - Loyalty program data

**Total: 28 Tables**

---

## Common Queries

### 1. Get Active Users for a Branch

```sql
SELECT u.*, ubr.role
FROM USER u
JOIN USER_BRANCH_ROLE ubr ON u.id = ubr.user_id
WHERE ubr.branch_id = :branchId
  AND u.is_active = true
  AND ubr.is_active = true;
```

### 2. Get Available Tables for a Branch

```sql
SELECT bt.*
FROM BRANCH_TABLE bt
WHERE bt.branch_id = :branchId
  AND bt.status = 'AVAILABLE'
  AND bt.is_active = true;
```

### 3. Get Active Menu Items for a Branch

```sql
SELECT mi.*, mc.name as category_name
FROM MENU_ITEM mi
JOIN MENU_CATEGORY mc ON mi.menu_category_id = mc.id
WHERE mi.branch_id = :branchId
  AND mi.is_available = true
  AND mi.is_active = true
  AND mc.is_active = true
ORDER BY mc.display_order, mi.display_order;
```

### 4. Get Pending Orders for a Branch

```sql
SELECT o.*, bt.table_number, c.first_name, c.last_name
FROM ORDER o
LEFT JOIN BRANCH_TABLE bt ON o.branch_table_id = bt.id
LEFT JOIN CUSTOMER c ON o.customer_id = c.id
WHERE o.branch_id = :branchId
  AND o.status IN ('PENDING', 'CONFIRMED', 'PREPARING')
  AND o.order_date >= CURRENT_DATE
ORDER BY o.order_date ASC;
```

### 5. Get Today's Sales Summary

```sql
SELECT
    COUNT(DISTINCT o.id) as total_orders,
    COUNT(DISTINCT b.id) as total_bills,
    SUM(b.total_amount) as total_revenue,
    SUM(b.amount_paid) as total_collected
FROM ORDER o
JOIN BILL b ON o.id = b.order_id
WHERE o.branch_id = :branchId
  AND DATE(o.order_date) = CURRENT_DATE
  AND b.status = 'PAID';
```

### 6. Get Table Assignments for Today

```sql
SELECT
    bt.table_number,
    u_supervisor.display_name as supervisor_name,
    u_waiter.display_name as waiter_name,
    ta.assignment_date,
    ta.start_time,
    ta.end_time
FROM TABLE_ASSIGNMENT ta
JOIN BRANCH_TABLE bt ON ta.branch_table_id = bt.id
JOIN USER u_supervisor ON ta.supervisor_id = u_supervisor.id
LEFT JOIN TABLE_WAITER_ASSIGNMENT twa ON ta.id = twa.table_assignment_id
LEFT JOIN USER u_waiter ON twa.waiter_id = u_waiter.id
WHERE bt.branch_id = :branchId
  AND ta.assignment_date = CURRENT_DATE
  AND ta.is_active = true
  AND (twa.is_active = true OR twa.id IS NULL);
```

### 7. Get Order Items with Customizations

```sql
SELECT
    oi.*,
    mi.name as item_name,
    miv.variant_name,
    oic.addon_name,
    oic.quantity as addon_quantity,
    oic.total_price as addon_price
FROM ORDER_ITEM oi
JOIN MENU_ITEM mi ON oi.menu_item_id = mi.id
LEFT JOIN MENU_ITEM_VARIANT miv ON oi.menu_item_variant_id = miv.id
LEFT JOIN ORDER_ITEM_CUSTOMIZATION oic ON oi.id = oic.order_item_id
WHERE oi.order_id = :orderId;
```

### 8. Get Bill with Tax and Discount Breakdown

```sql
SELECT
    b.*,
    bt.tax_name,
    bt.tax_rate,
    bt.tax_amount as tax_breakdown_amount,
    bd.discount_name,
    bd.discount_amount as discount_breakdown_amount
FROM BILL b
LEFT JOIN BILL_TAX bt ON b.id = bt.bill_id
LEFT JOIN BILL_DISCOUNT bd ON b.id = bd.bill_id
WHERE b.id = :billId;
```

### 9. Get Payment Summary for a Date Range

```sql
SELECT
    pm.method_name,
    COUNT(p.id) as transaction_count,
    SUM(p.amount) as total_amount
FROM PAYMENT p
JOIN PAYMENT_METHOD pm ON p.payment_method_id = pm.id
WHERE p.bill_id IN (
    SELECT id FROM BILL
    WHERE branch_id = :branchId
    AND bill_date BETWEEN :startDate AND :endDate
)
AND p.status = 'SUCCESS'
GROUP BY pm.method_name
ORDER BY total_amount DESC;
```

### 10. Get Low Stock Items

```sql
SELECT
    mi.name,
    i.current_stock,
    i.min_stock_level,
    i.unit,
    b.name as branch_name
FROM INVENTORY i
JOIN MENU_ITEM mi ON i.menu_item_id = mi.id
JOIN BRANCH b ON i.branch_id = b.id
WHERE i.branch_id = :branchId
  AND i.current_stock <= i.min_stock_level
  AND mi.is_active = true
ORDER BY (i.current_stock - i.min_stock_level) ASC;
```

---

## Status Values Reference

### BRANCH_TABLE.status

- `AVAILABLE` - Table is available
- `OCCUPIED` - Table is currently occupied
- `RESERVED` - Table is reserved
- `OUT_OF_SERVICE` - Table is out of service

### ORDER.status

- `PENDING` - Order is pending confirmation
- `CONFIRMED` - Order is confirmed
- `PREPARING` - Order is being prepared
- `READY` - Order is ready for serving
- `SERVED` - Order has been served
- `CANCELLED` - Order is cancelled
- `COMPLETED` - Order is completed

### ORDER.order_type

- `ONLINE` - Online order (mobile app/web)
- `OFFLINE` - Offline order (at restaurant)
- `TAKEAWAY` - Takeaway order
- `DELIVERY` - Delivery order

### BILL.status

- `PENDING` - Bill is pending payment
- `PARTIALLY_PAID` - Bill is partially paid
- `PAID` - Bill is fully paid
- `CANCELLED` - Bill is cancelled
- `REFUNDED` - Bill is refunded

### PAYMENT.status

- `PENDING` - Payment is pending
- `SUCCESS` - Payment is successful
- `FAILED` - Payment failed
- `REFUNDED` - Payment is refunded

### USER.sync_status

- `PENDING` - User not yet synced
- `SYNCED` - User successfully synced
- `FAILED` - Sync failed
- `OUT_OF_SYNC` - Local changes not synced

### USER_SYNC_LOG.sync_type

- `CREATE` - New user creation
- `UPDATE` - User update
- `DELETE` - User deletion

---

## Role Codes Reference

### User Roles

- `ROLE_ADMIN` - Restaurant administrator
- `ROLE_MANAGER` - Branch manager
- `ROLE_SUPERVISOR` - Floor supervisor
- `ROLE_WAITER` - Waiter/server
- `ROLE_CASHIER` - Cashier
- `ROLE_CHEF` - Kitchen chef
- `ROLE_CUSTOMER` - Registered customer
- `ROLE_ANONYMOUS` - Anonymous customer

---

## Payment Method Codes

### Standard Payment Methods

- `CASH` - Cash payment
- `CARD` - Credit/Debit card
- `GPAY` - Google Pay
- `PHONEPE` - PhonePe
- `PAYTM` - Paytm
- `UPI` - UPI payment
- `NETBANKING` - Net banking
- `WALLET` - Digital wallet

---

## Menu Item Types

### Item Types

- `EATABLE` - Food items
- `BEVERAGE` - Beverage items

### Cuisine Types (Examples)

- `INDIAN` - Indian cuisine
- `CHINESE` - Chinese cuisine
- `ITALIAN` - Italian cuisine
- `MEXICAN` - Mexican cuisine
- `CONTINENTAL` - Continental cuisine
- `FAST_FOOD` - Fast food
- `DESSERT` - Desserts
- `BEVERAGE` - Beverages

---

## Discount Types

### Discount Types

- `PERCENTAGE` - Percentage discount (e.g., 10% off)
- `FIXED_AMOUNT` - Fixed amount discount (e.g., ₹50 off)
- `BUY_X_GET_Y` - Buy X get Y free

---

## Tax Types

### Tax Types

- `PERCENTAGE` - Percentage-based tax (e.g., 18% GST)
- `FIXED` - Fixed amount tax

---

## Common Indexes

### Performance-Critical Indexes

```sql
-- Order queries by branch and date
CREATE INDEX idx_order_branch_date ON ORDER(branch_id, order_date);

-- Bill queries by branch and date
CREATE INDEX idx_bill_branch_date ON BILL(branch_id, bill_date);

-- Active user roles
CREATE INDEX idx_user_branch_role_active ON USER_BRANCH_ROLE(user_id, branch_id, is_active);

-- Available menu items
CREATE INDEX idx_menu_item_available ON MENU_ITEM(is_available, is_active);

-- Table assignments by date
CREATE INDEX idx_table_assignment_active ON TABLE_ASSIGNMENT(is_active, assignment_date);
```

---

## Data Types Reference

### Common Data Types

- `UUID` - Primary keys (e.g., `gen_random_uuid()` in PostgreSQL)
- `VARCHAR(n)` - Variable length string
- `TEXT` - Unlimited length string
- `DECIMAL(p,s)` - Fixed precision decimal (e.g., `DECIMAL(10,2)` for money)
- `INTEGER` - 32-bit integer
- `BOOLEAN` - True/false
- `TIMESTAMP` - Date and time
- `DATE` - Date only
- `TIME` - Time only
- `JSONB` - JSON data (PostgreSQL)

---

## Foreign Key Cascade Rules

### CASCADE on Delete

- `USER_BRANCH_ROLE` → `USER`
- `USER_BRANCH_ROLE` → `BRANCH`
- `BRANCH_TABLE` → `BRANCH`
- `TABLE_ASSIGNMENT` → `BRANCH_TABLE`
- `TABLE_WAITER_ASSIGNMENT` → `TABLE_ASSIGNMENT`
- `MENU_ITEM` → `BRANCH`
- `ORDER_ITEM` → `ORDER`
- `ORDER_ITEM_CUSTOMIZATION` → `ORDER_ITEM`
- `BILL_ITEM` → `BILL`
- `BILL_TAX` → `BILL`
- `BILL_DISCOUNT` → `BILL`
- `PAYMENT` → `BILL`

### RESTRICT on Delete

- `BRANCH` → `RESTAURANT`
- `ORDER` → `BRANCH`
- `BILL` → `ORDER`
- `TABLE_ASSIGNMENT` → `USER` (supervisor)
- `TABLE_WAITER_ASSIGNMENT` → `USER` (waiter)

### SET NULL on Delete

- `ORDER` → `CUSTOMER`
- `ORDER` → `BRANCH_TABLE`

---

## Audit Fields

### Standard Audit Fields

All tables include:

- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp
- `created_by` - Creator user ID (where applicable)
- `updated_by` - Last updater user ID (where applicable)

### Version Field (Optimistic Locking)

Critical tables include:

- `version` - Integer version for optimistic locking
  - RESTAURANT
  - BRANCH
  - MENU_ITEM
  - ORDER
  - BILL
  - PAYMENT
  - CUSTOMER

---

## Unique Constraints

### Key Unique Constraints

- `RESTAURANT.code` - Unique restaurant code
- `BRANCH.restaurant_id + code` - Unique branch code per restaurant
- `USER.external_user_id` - Unique external user ID
- `USER.username` - Unique username
- `USER.email` - Unique email
- `BRANCH_TABLE.branch_id + table_number` - Unique table number per branch
- `MENU_ITEM.branch_id + code` - Unique menu item code per branch
- `ORDER.order_number` - Unique order number
- `BILL.bill_number` - Unique bill number
- `PAYMENT.payment_number` - Unique payment number
- `CUSTOMER.customer_code` - Unique customer code

---

## Best Practices

### 1. Always Use Tenant Context

- All queries should be scoped to tenant (via connection routing)
- Never query across tenants

### 2. Use Soft Deletes

- Check `is_active = true` in queries
- Use soft deletes instead of hard deletes

### 3. Optimistic Locking

- Check `version` field before updates
- Increment `version` on updates

### 4. Status Workflows

- Follow defined status workflows
- Log status changes in history tables

### 5. Financial Data

- Use `DECIMAL(10,2)` for all monetary values
- Never update bills after generation (immutable)
- Store denormalized data in bills for historical accuracy

### 6. Indexes

- Use indexes on foreign keys
- Use composite indexes for common query patterns
- Monitor index usage and adjust as needed

---

## Common Patterns

### Pattern 1: Get Active Records

```sql
WHERE is_active = true
```

### Pattern 2: Date Range Queries

```sql
WHERE date_column BETWEEN :startDate AND :endDate
```

### Pattern 3: Join with Status Filter

```sql
JOIN table1 t1 ON t1.id = t2.foreign_id
WHERE t1.is_active = true
  AND t2.status = 'ACTIVE'
```

### Pattern 4: Latest Record

```sql
ORDER BY created_at DESC
LIMIT 1
```

### Pattern 5: Count with Group By

```sql
SELECT column, COUNT(*) as count
FROM table
WHERE conditions
GROUP BY column
```

---

## Notes

- All timestamps are in UTC (convert to local timezone in application)
- All monetary values are in base currency (e.g., INR, USD)
- UUIDs are generated using `gen_random_uuid()` in PostgreSQL
- JSONB fields store flexible data structures
- Soft deletes preserve data for audit and reporting
