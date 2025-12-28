-- ============================================================================
-- Restaurant Management System - Database Schema Reference
-- ============================================================================
-- This file contains SQL DDL statements for reference purposes.
-- DO NOT execute directly - use Liquibase changelogs for actual migrations.
-- ============================================================================

-- Enable UUID extension (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- CORE ENTITIES
-- ============================================================================

-- RESTAURANT
CREATE TABLE restaurant (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    logo_url VARCHAR(500),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    timezone VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_restaurant_code ON restaurant(code);
CREATE INDEX idx_restaurant_active ON restaurant(is_active);

-- BRANCH
CREATE TABLE branch (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL REFERENCES restaurant(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    opening_time TIME,
    closing_time TIME,
    timezone VARCHAR(50),
    max_capacity INTEGER,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    version INTEGER DEFAULT 0,
    CONSTRAINT uk_branch_restaurant_code UNIQUE (restaurant_id, code)
);

CREATE INDEX idx_branch_restaurant ON branch(restaurant_id);
CREATE INDEX idx_branch_code ON branch(restaurant_id, code);
CREATE INDEX idx_branch_active ON branch(is_active);

-- ============================================================================
-- USER MANAGEMENT
-- ============================================================================

-- USER
CREATE TABLE "user" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_user_id VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    display_name VARCHAR(255),
    profile_image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    last_sync_at TIMESTAMP,
    sync_status VARCHAR(50) DEFAULT 'PENDING',
    sync_error TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_user_external_id ON "user"(external_user_id);
CREATE INDEX idx_user_username ON "user"(username);
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_sync_status ON "user"(sync_status);

-- USER_BRANCH_ROLE
CREATE TABLE user_branch_role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    branch_id UUID NOT NULL REFERENCES branch(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(255),
    revoked_at TIMESTAMP,
    revoked_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_branch_role UNIQUE (user_id, branch_id, role)
);

CREATE INDEX idx_user_branch_role_user ON user_branch_role(user_id);
CREATE INDEX idx_user_branch_role_branch ON user_branch_role(branch_id);
CREATE INDEX idx_user_branch_role_active ON user_branch_role(user_id, branch_id, is_active);
CREATE INDEX idx_user_branch_role_role ON user_branch_role(role);

-- USER_SYNC_LOG
CREATE TABLE user_sync_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    sync_type VARCHAR(50) NOT NULL,
    sync_status VARCHAR(50) NOT NULL,
    external_user_id VARCHAR(255),
    request_payload JSONB,
    response_payload JSONB,
    error_message TEXT,
    synced_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    synced_by VARCHAR(255)
);

CREATE INDEX idx_user_sync_log_user ON user_sync_log(user_id);
CREATE INDEX idx_user_sync_log_status ON user_sync_log(sync_status);
CREATE INDEX idx_user_sync_log_type ON user_sync_log(sync_type);
CREATE INDEX idx_user_sync_log_date ON user_sync_log(synced_at);

-- ============================================================================
-- TABLE MANAGEMENT
-- ============================================================================

-- BRANCH_TABLE
CREATE TABLE branch_table (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL REFERENCES branch(id) ON DELETE CASCADE,
    table_number VARCHAR(50) NOT NULL,
    table_name VARCHAR(255),
    capacity INTEGER NOT NULL,
    floor VARCHAR(50),
    section VARCHAR(100),
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    qr_code VARCHAR(500),
    qr_code_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    version INTEGER DEFAULT 0,
    CONSTRAINT uk_branch_table_number UNIQUE (branch_id, table_number),
    CONSTRAINT chk_branch_table_status CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'RESERVED', 'OUT_OF_SERVICE')),
    CONSTRAINT chk_branch_table_capacity CHECK (capacity > 0)
);

CREATE INDEX idx_branch_table_branch ON branch_table(branch_id);
CREATE INDEX idx_branch_table_number ON branch_table(branch_id, table_number);
CREATE INDEX idx_branch_table_status ON branch_table(status);
CREATE INDEX idx_branch_table_active ON branch_table(is_active);

-- SHIFT
CREATE TABLE shift (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL REFERENCES branch(id) ON DELETE CASCADE,
    shift_name VARCHAR(100) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    shift_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shift_branch ON shift(branch_id);
CREATE INDEX idx_shift_date ON shift(shift_date);
CREATE INDEX idx_shift_branch_date ON shift(branch_id, shift_date);

-- TABLE_ASSIGNMENT
CREATE TABLE table_assignment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_table_id UUID NOT NULL REFERENCES branch_table(id) ON DELETE CASCADE,
    shift_id UUID NOT NULL REFERENCES shift(id) ON DELETE CASCADE,
    supervisor_id UUID NOT NULL REFERENCES "user"(id) ON DELETE RESTRICT,
    assignment_date DATE NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_table_assignment_table ON table_assignment(branch_table_id);
CREATE INDEX idx_table_assignment_shift ON table_assignment(shift_id);
CREATE INDEX idx_table_assignment_supervisor ON table_assignment(supervisor_id);
CREATE INDEX idx_table_assignment_date ON table_assignment(assignment_date);
CREATE INDEX idx_table_assignment_active ON table_assignment(is_active, assignment_date);

-- TABLE_WAITER_ASSIGNMENT
CREATE TABLE table_waiter_assignment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    table_assignment_id UUID NOT NULL REFERENCES table_assignment(id) ON DELETE CASCADE,
    waiter_id UUID NOT NULL REFERENCES "user"(id) ON DELETE RESTRICT,
    assignment_date DATE NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_table_waiter_assignment_table ON table_waiter_assignment(table_assignment_id);
CREATE INDEX idx_table_waiter_assignment_waiter ON table_waiter_assignment(waiter_id);
CREATE INDEX idx_table_waiter_assignment_date ON table_waiter_assignment(assignment_date);
CREATE INDEX idx_table_waiter_assignment_active ON table_waiter_assignment(is_active, assignment_date);

-- ============================================================================
-- MENU MANAGEMENT
-- ============================================================================

-- MENU_CATEGORY
CREATE TABLE menu_category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL REFERENCES restaurant(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT,
    display_order INTEGER DEFAULT 0,
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    version INTEGER DEFAULT 0,
    CONSTRAINT uk_menu_category_restaurant_code UNIQUE (restaurant_id, code)
);

CREATE INDEX idx_menu_category_restaurant ON menu_category(restaurant_id);
CREATE INDEX idx_menu_category_code ON menu_category(restaurant_id, code);
CREATE INDEX idx_menu_category_active ON menu_category(is_active);
CREATE INDEX idx_menu_category_order ON menu_category(display_order);

-- MENU_ITEM
CREATE TABLE menu_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL REFERENCES branch(id) ON DELETE CASCADE,
    menu_category_id UUID NOT NULL REFERENCES menu_category(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT,
    item_type VARCHAR(50) NOT NULL,
    cuisine_type VARCHAR(100),
    is_vegetarian BOOLEAN DEFAULT false,
    is_vegan BOOLEAN DEFAULT false,
    is_alcoholic BOOLEAN DEFAULT false,
    spice_level INTEGER,
    preparation_time INTEGER,
    base_price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(500),
    is_available BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    version INTEGER DEFAULT 0,
    CONSTRAINT uk_menu_item_branch_code UNIQUE (branch_id, code),
    CONSTRAINT chk_menu_item_item_type CHECK (item_type IN ('EATABLE', 'BEVERAGE')),
    CONSTRAINT chk_menu_item_base_price CHECK (base_price >= 0)
);

CREATE INDEX idx_menu_item_branch ON menu_item(branch_id);
CREATE INDEX idx_menu_item_category ON menu_item(menu_category_id);
CREATE INDEX idx_menu_item_code ON menu_item(branch_id, code);
CREATE INDEX idx_menu_item_type ON menu_item(item_type);
CREATE INDEX idx_menu_item_available ON menu_item(is_available, is_active);
CREATE INDEX idx_menu_item_vegetarian ON menu_item(is_vegetarian);
CREATE INDEX idx_menu_item_alcoholic ON menu_item(is_alcoholic);

-- MENU_ITEM_VARIANT
CREATE TABLE menu_item_variant (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    menu_item_id UUID NOT NULL REFERENCES menu_item(id) ON DELETE CASCADE,
    variant_name VARCHAR(100) NOT NULL,
    variant_code VARCHAR(50) NOT NULL,
    price_modifier DECIMAL(10,2) DEFAULT 0,
    is_default BOOLEAN DEFAULT false,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_menu_item_variant_code UNIQUE (menu_item_id, variant_code)
);

CREATE INDEX idx_menu_item_variant_item ON menu_item_variant(menu_item_id);
CREATE INDEX idx_menu_item_variant_code ON menu_item_variant(menu_item_id, variant_code);
CREATE INDEX idx_menu_item_variant_default ON menu_item_variant(menu_item_id, is_default);

-- MENU_ITEM_ADDON
CREATE TABLE menu_item_addon (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    menu_item_id UUID NOT NULL REFERENCES menu_item(id) ON DELETE CASCADE,
    addon_name VARCHAR(100) NOT NULL,
    addon_code VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_menu_item_addon_code UNIQUE (menu_item_id, addon_code),
    CONSTRAINT chk_menu_item_addon_price CHECK (price >= 0)
);

CREATE INDEX idx_menu_item_addon_item ON menu_item_addon(menu_item_id);
CREATE INDEX idx_menu_item_addon_code ON menu_item_addon(menu_item_id, addon_code);

-- INVENTORY
CREATE TABLE inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL REFERENCES branch(id) ON DELETE CASCADE,
    menu_item_id UUID NOT NULL REFERENCES menu_item(id) ON DELETE CASCADE,
    current_stock DECIMAL(10,2) DEFAULT 0,
    unit VARCHAR(50),
    min_stock_level DECIMAL(10,2),
    max_stock_level DECIMAL(10,2),
    last_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_inventory_branch_item UNIQUE (branch_id, menu_item_id)
);

CREATE INDEX idx_inventory_branch ON inventory(branch_id);
CREATE INDEX idx_inventory_item ON inventory(menu_item_id);
CREATE INDEX idx_inventory_branch_item ON inventory(branch_id, menu_item_id);
CREATE INDEX idx_inventory_stock ON inventory(current_stock);

-- ============================================================================
-- CUSTOMER MANAGEMENT
-- ============================================================================

-- CUSTOMER
CREATE TABLE customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES "user"(id) ON DELETE SET NULL,
    customer_code VARCHAR(50) UNIQUE,
    phone VARCHAR(20),
    email VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    date_of_birth DATE,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_customer_user ON customer(user_id);
CREATE INDEX idx_customer_code ON customer(customer_code);
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_customer_email ON customer(email);

-- CUSTOMER_LOYALTY
CREATE TABLE customer_loyalty (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    restaurant_id UUID NOT NULL REFERENCES restaurant(id) ON DELETE CASCADE,
    loyalty_points DECIMAL(10,2) DEFAULT 0,
    tier VARCHAR(50) DEFAULT 'BRONZE',
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_points_earned_at TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_customer_loyalty UNIQUE (customer_id, restaurant_id)
);

CREATE INDEX idx_customer_loyalty_customer ON customer_loyalty(customer_id);
CREATE INDEX idx_customer_loyalty_restaurant ON customer_loyalty(restaurant_id);
CREATE INDEX idx_customer_loyalty_active ON customer_loyalty(customer_id, restaurant_id, is_active);

-- ============================================================================
-- ORDER MANAGEMENT
-- ============================================================================

-- ORDER
CREATE TABLE "order" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL REFERENCES branch(id) ON DELETE RESTRICT,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id UUID REFERENCES customer(id) ON DELETE SET NULL,
    user_id UUID REFERENCES "user"(id) ON DELETE SET NULL,
    branch_table_id UUID REFERENCES branch_table(id) ON DELETE SET NULL,
    order_type VARCHAR(50) NOT NULL,
    order_source VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estimated_ready_time TIMESTAMP,
    actual_ready_time TIMESTAMP,
    special_instructions TEXT,
    subtotal DECIMAL(10,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2) DEFAULT 0,
    is_paid BOOLEAN DEFAULT false,
    cancelled_at TIMESTAMP,
    cancelled_by VARCHAR(255),
    cancellation_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    version INTEGER DEFAULT 0,
    CONSTRAINT chk_order_order_type CHECK (order_type IN ('ONLINE', 'OFFLINE', 'TAKEAWAY', 'DELIVERY')),
    CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'SERVED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT chk_order_amounts CHECK (subtotal >= 0 AND tax_amount >= 0 AND discount_amount >= 0 AND total_amount >= 0)
);

CREATE INDEX idx_order_branch ON "order"(branch_id);
CREATE INDEX idx_order_customer ON "order"(customer_id);
CREATE INDEX idx_order_user ON "order"(user_id);
CREATE INDEX idx_order_table ON "order"(branch_table_id);
CREATE INDEX idx_order_number ON "order"(order_number);
CREATE INDEX idx_order_status ON "order"(status);
CREATE INDEX idx_order_type ON "order"(order_type);
CREATE INDEX idx_order_date ON "order"(order_date);
CREATE INDEX idx_order_paid ON "order"(is_paid);
CREATE INDEX idx_order_branch_date ON "order"(branch_id, order_date);

-- ORDER_ITEM
CREATE TABLE order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
    menu_item_id UUID NOT NULL REFERENCES menu_item(id) ON DELETE RESTRICT,
    menu_item_variant_id UUID REFERENCES menu_item_variant(id) ON DELETE SET NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    item_total DECIMAL(10,2) NOT NULL,
    special_instructions TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_order_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_item_prices CHECK (unit_price >= 0 AND item_total >= 0)
);

CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_order_item_menu_item ON order_item(menu_item_id);
CREATE INDEX idx_order_item_status ON order_item(status);

-- ORDER_ITEM_CUSTOMIZATION
CREATE TABLE order_item_customization (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_item_id UUID NOT NULL REFERENCES order_item(id) ON DELETE CASCADE,
    menu_item_addon_id UUID NOT NULL REFERENCES menu_item_addon(id) ON DELETE RESTRICT,
    quantity INTEGER DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_order_item_customization_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_item_customization_prices CHECK (unit_price >= 0 AND total_price >= 0)
);

CREATE INDEX idx_order_item_customization_order_item ON order_item_customization(order_item_id);
CREATE INDEX idx_order_item_customization_addon ON order_item_customization(menu_item_addon_id);

-- ORDER_STATUS_HISTORY
CREATE TABLE order_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(255),
    notes TEXT
);

CREATE INDEX idx_order_status_history_order ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_date ON order_status_history(changed_at);

-- ============================================================================
-- BILLING & PAYMENTS
-- ============================================================================

-- TAX_CONFIG
CREATE TABLE tax_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL REFERENCES restaurant(id) ON DELETE RESTRICT,
    tax_name VARCHAR(100) NOT NULL,
    tax_code VARCHAR(50) NOT NULL,
    tax_rate DECIMAL(5,2) NOT NULL,
    tax_type VARCHAR(50) NOT NULL,
    is_applicable_to_food BOOLEAN DEFAULT true,
    is_applicable_to_beverage BOOLEAN DEFAULT true,
    is_applicable_to_alcohol BOOLEAN DEFAULT false,
    effective_from DATE NOT NULL,
    effective_to DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    CONSTRAINT uk_tax_config_restaurant_code UNIQUE (restaurant_id, tax_code),
    CONSTRAINT chk_tax_config_tax_type CHECK (tax_type IN ('PERCENTAGE', 'FIXED')),
    CONSTRAINT chk_tax_config_tax_rate CHECK (tax_rate >= 0)
);

CREATE INDEX idx_tax_config_restaurant ON tax_config(restaurant_id);
CREATE INDEX idx_tax_config_code ON tax_config(restaurant_id, tax_code);
CREATE INDEX idx_tax_config_active ON tax_config(is_active, effective_from, effective_to);
CREATE INDEX idx_tax_config_effective ON tax_config(effective_from, effective_to);

-- DISCOUNT
CREATE TABLE discount (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL REFERENCES restaurant(id) ON DELETE RESTRICT,
    discount_code VARCHAR(50) UNIQUE,
    discount_name VARCHAR(255) NOT NULL,
    discount_type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2),
    max_discount_amount DECIMAL(10,2),
    applicable_to VARCHAR(50),
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP,
    max_uses INTEGER,
    current_uses INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    CONSTRAINT chk_discount_discount_type CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT', 'BUY_X_GET_Y')),
    CONSTRAINT chk_discount_value CHECK (discount_value >= 0)
);

CREATE INDEX idx_discount_restaurant ON discount(restaurant_id);
CREATE INDEX idx_discount_code ON discount(discount_code);
CREATE INDEX idx_discount_active ON discount(is_active, valid_from, valid_to);
CREATE INDEX idx_discount_valid ON discount(valid_from, valid_to);

-- BILL
CREATE TABLE bill (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES "order"(id) ON DELETE RESTRICT,
    branch_id UUID NOT NULL REFERENCES branch(id) ON DELETE RESTRICT,
    bill_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id UUID REFERENCES customer(id) ON DELETE SET NULL,
    bill_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    service_charge DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    amount_paid DECIMAL(10,2) DEFAULT 0,
    amount_due DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    generated_by VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    CONSTRAINT chk_bill_status CHECK (status IN ('PENDING', 'PARTIALLY_PAID', 'PAID', 'CANCELLED', 'REFUNDED')),
    CONSTRAINT chk_bill_amounts CHECK (subtotal >= 0 AND tax_amount >= 0 AND discount_amount >= 0 AND total_amount >= 0 AND amount_paid >= 0 AND amount_due >= 0)
);

CREATE INDEX idx_bill_order ON bill(order_id);
CREATE INDEX idx_bill_branch ON bill(branch_id);
CREATE INDEX idx_bill_customer ON bill(customer_id);
CREATE INDEX idx_bill_number ON bill(bill_number);
CREATE INDEX idx_bill_status ON bill(status);
CREATE INDEX idx_bill_date ON bill(bill_date);
CREATE INDEX idx_bill_branch_date ON bill(branch_id, bill_date);

-- BILL_ITEM
CREATE TABLE bill_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES bill(id) ON DELETE CASCADE,
    order_item_id UUID NOT NULL REFERENCES order_item(id) ON DELETE RESTRICT,
    item_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    item_total DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_bill_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_bill_item_prices CHECK (unit_price >= 0 AND item_total >= 0)
);

CREATE INDEX idx_bill_item_bill ON bill_item(bill_id);
CREATE INDEX idx_bill_item_order_item ON bill_item(order_item_id);

-- BILL_TAX
CREATE TABLE bill_tax (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES bill(id) ON DELETE CASCADE,
    tax_config_id UUID NOT NULL REFERENCES tax_config(id) ON DELETE RESTRICT,
    tax_name VARCHAR(100) NOT NULL,
    tax_rate DECIMAL(5,2) NOT NULL,
    taxable_amount DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_bill_tax_amounts CHECK (taxable_amount >= 0 AND tax_amount >= 0)
);

CREATE INDEX idx_bill_tax_bill ON bill_tax(bill_id);
CREATE INDEX idx_bill_tax_config ON bill_tax(tax_config_id);

-- BILL_DISCOUNT
CREATE TABLE bill_discount (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES bill(id) ON DELETE CASCADE,
    discount_id UUID NOT NULL REFERENCES discount(id) ON DELETE RESTRICT,
    discount_code VARCHAR(50),
    discount_type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_bill_discount_amount CHECK (discount_amount >= 0)
);

CREATE INDEX idx_bill_discount_bill ON bill_discount(bill_id);
CREATE INDEX idx_bill_discount_discount ON bill_discount(discount_id);

-- PAYMENT_METHOD
CREATE TABLE payment_method (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    method_code VARCHAR(50) UNIQUE NOT NULL,
    method_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_method_code ON payment_method(method_code);
CREATE INDEX idx_payment_method_active ON payment_method(is_active);

-- PAYMENT
CREATE TABLE payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES bill(id) ON DELETE RESTRICT,
    payment_method_id UUID NOT NULL REFERENCES payment_method(id) ON DELETE RESTRICT,
    payment_number VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_id VARCHAR(255),
    payment_gateway_response JSONB,
    status VARCHAR(50) DEFAULT 'PENDING',
    processed_by VARCHAR(255),
    notes TEXT,
    refunded_at TIMESTAMP,
    refunded_by VARCHAR(255),
    refund_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    CONSTRAINT chk_payment_amount CHECK (amount > 0)
);

CREATE INDEX idx_payment_bill ON payment(bill_id);
CREATE INDEX idx_payment_method ON payment(payment_method_id);
CREATE INDEX idx_payment_number ON payment(payment_number);
CREATE INDEX idx_payment_status ON payment(status);
CREATE INDEX idx_payment_date ON payment(payment_date);
CREATE INDEX idx_payment_transaction ON payment(transaction_id);

-- ============================================================================
-- COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TABLE restaurant IS 'Top-level entity representing a restaurant chain/brand';
COMMENT ON TABLE branch IS 'Physical location/branch of a restaurant';
COMMENT ON TABLE "user" IS 'System users synchronized from Gateway/Keycloak';
COMMENT ON TABLE user_branch_role IS 'Maps users to branches with specific roles';
COMMENT ON TABLE user_sync_log IS 'Tracks synchronization events with Gateway/Keycloak';
COMMENT ON TABLE branch_table IS 'Physical tables in a branch';
COMMENT ON TABLE shift IS 'Work shifts for employees';
COMMENT ON TABLE table_assignment IS 'Maps tables to supervisors for specific shifts';
COMMENT ON TABLE table_waiter_assignment IS 'Maps waiters to tables for specific shifts';
COMMENT ON TABLE menu_category IS 'Categorizes menu items';
COMMENT ON TABLE menu_item IS 'Individual menu items (food and beverages)';
COMMENT ON TABLE menu_item_variant IS 'Size/variant options for menu items';
COMMENT ON TABLE menu_item_addon IS 'Add-on options for menu items';
COMMENT ON TABLE inventory IS 'Tracks inventory/stock levels for menu items';
COMMENT ON TABLE customer IS 'Customer profiles (registered or anonymous)';
COMMENT ON TABLE customer_loyalty IS 'Customer loyalty program enrollment and points';
COMMENT ON TABLE "order" IS 'Customer orders (online or offline)';
COMMENT ON TABLE order_item IS 'Individual items in an order';
COMMENT ON TABLE order_item_customization IS 'Add-ons and customizations for order items';
COMMENT ON TABLE order_status_history IS 'Tracks order status changes for audit trail';
COMMENT ON TABLE tax_config IS 'Configures tax rates for restaurants';
COMMENT ON TABLE discount IS 'Discount/promotion configurations';
COMMENT ON TABLE bill IS 'Bills generated from orders';
COMMENT ON TABLE bill_item IS 'Items in a bill (denormalized from order items)';
COMMENT ON TABLE bill_tax IS 'Tax breakdown for bills';
COMMENT ON TABLE bill_discount IS 'Discounts applied to bills';
COMMENT ON TABLE payment_method IS 'Available payment methods';
COMMENT ON TABLE payment IS 'Payments made against bills';

