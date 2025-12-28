# Multi-Tenant Implementation Plan for RMS Service

## Overview

This document outlines the implementation plan for multi-tenancy in the RMS Service. The service will support multiple tenants, each with their own database. The gateway maintains a registry of all tenants and their database connection details.

## Architecture Overview

### Current Architecture

- **Framework**: Spring Boot 3.4.5 with JHipster 8.11.0
- **Database**: PostgreSQL with R2DBC (Reactive)
- **Authentication**: OAuth2/JWT via Keycloak
- **Service Discovery**: Consul
- **Stack**: Reactive WebFlux

### Multi-Tenant Architecture

```
┌─────────────┐
│   Gateway   │ (Maintains tenant registry)
└──────┬──────┘
       │
       │ HTTP Request with Tenant ID (Header/JWT)
       │
┌──────▼──────────────────────────────────────┐
│         RMS Service                         │
│  ┌──────────────────────────────────────┐  │
│  │  Tenant Context Resolver             │  │
│  │  - Extracts tenant ID from request   │  │
│  └──────────────┬───────────────────────┘  │
│                 │                           │
│  ┌──────────────▼───────────────────────┐  │
│  │  Tenant Connection Manager           │  │
│  │  - Fetches DB config from Gateway    │  │
│  │  - Manages connection pool per tenant│  │
│  │  - Caches connections                │  │
│  └──────────────┬───────────────────────┘  │
│                 │                           │
│  ┌──────────────▼───────────────────────┐  │
│  │  Dynamic DataSource Router           │  │
│  │  - Routes to tenant-specific DB      │  │
│  └──────────────────────────────────────┘  │
└────────────────────────────────────────────┘
       │
       ├─────────┬─────────┬─────────┐
       │         │         │         │
┌──────▼──┐ ┌───▼───┐ ┌───▼───┐ ┌───▼───┐
│Tenant 1 │ │Tenant2│ │Tenant3│ │TenantN│
│   DB    │ │  DB   │ │  DB   │ │  DB   │
└─────────┘ └───────┘ └───────┘ └───────┘
```

## Implementation Strategy

### 1. Tenant Identification

**Option A: HTTP Header (Recommended)**

- Gateway adds `X-Tenant-ID` header to all requests
- Simple and explicit
- Easy to debug and test

**Option B: JWT Claim**

- Tenant ID embedded in JWT token claims
- More secure (can't be spoofed)
- Requires gateway to add claim during token issuance

**Option C: Subdomain**

- Extract tenant from subdomain (e.g., `tenant1.api.example.com`)
- Requires DNS configuration

**Recommendation**: Use **Option A (HTTP Header)** for simplicity, with **Option B (JWT)** as fallback.

### 2. Tenant Context Management

#### Components Needed:

1. **TenantContextHolder**

   - Thread-local/Reactor Context storage for current tenant ID
   - Provides reactive context propagation

2. **TenantContextFilter/Interceptor**

   - Extracts tenant ID from request (header or JWT)
   - Validates tenant ID exists
   - Sets tenant context for downstream processing

3. **TenantConnectionService**
   - Fetches tenant database configuration from Gateway
   - Manages connection pool per tenant
   - Implements connection caching/eviction

### 3. Database Connection Management

#### Approach: Dynamic Connection Factory Routing

**Key Components:**

1. **TenantConnectionFactory**

   - Interface for tenant-specific connection factories
   - Wraps R2DBC ConnectionFactory

2. **TenantConnectionManager**

   - Maintains a map of tenant ID → ConnectionFactory
   - Lazy initialization (create on first use)
   - Connection pooling per tenant
   - Health checks and connection validation

3. **Gateway Client**

   - REST client to fetch tenant configuration from Gateway
   - Caches tenant configurations
   - Handles retries and failures

4. **TenantConnectionFactoryProvider**
   - Provides the correct ConnectionFactory based on current tenant context
   - Used by Spring Data R2DBC repositories

### 4. Gateway Integration

#### Gateway API Contract

**Endpoint**: `GET /api/tenants/{tenantId}/database-config`

**Response**:

```json
{
  "tenantId": "tenant-123",
  "databaseUrl": "r2dbc:postgresql://db-host:5432/tenant_db",
  "username": "tenant_user",
  "password": "encrypted_password",
  "maxPoolSize": 20,
  "connectionTimeout": 30000,
  "validationQuery": "SELECT 1"
}
```

**Caching Strategy**:

- Cache tenant configurations in-memory (Caffeine/Guava)
- TTL: 1 hour (configurable)
- Invalidate on tenant update events (if Gateway supports webhooks)

### 5. Request Flow

```
1. Request arrives at RMS Service
   ↓
2. TenantContextFilter extracts tenant ID from header/JWT
   ↓
3. Tenant ID validated (exists in cache or fetched from Gateway)
   ↓
4. Tenant ID stored in Reactor Context
   ↓
5. Repository method invoked
   ↓
6. TenantConnectionFactoryProvider reads tenant ID from context
   ↓
7. Returns appropriate ConnectionFactory for tenant
   ↓
8. Database query executed on tenant-specific database
   ↓
9. Response returned
```

### 6. Implementation Components

#### 6.1 Domain Models

```java
// Tenant database configuration
public class TenantDatabaseConfig {

  private String tenantId;
  private String databaseUrl;
  private String username;
  private String password;
  private Integer maxPoolSize;
  private Integer connectionTimeout;
  private String validationQuery;
}

// Tenant context
public class TenantContext {

  private String tenantId;
  private TenantDatabaseConfig databaseConfig;
}

```

#### 6.2 Core Classes

1. **TenantContextHolder**

   - Reactive context management
   - Thread-safe tenant ID storage

2. **TenantContextFilter**

   - WebFilter for extracting tenant ID
   - Sets context before request processing

3. **TenantConnectionManager**

   - Manages tenant connection factories
   - Connection pooling and lifecycle

4. **GatewayTenantService**

   - Client for Gateway tenant API
   - Caching and error handling

5. **TenantConnectionFactoryProvider**

   - Provides ConnectionFactory based on tenant context
   - Integrates with Spring R2DBC

6. **TenantAwareDatabaseConfiguration**
   - Custom database configuration
   - Replaces default ConnectionFactory bean

### 7. Configuration

#### Application Properties

```yaml
multi-tenant:
  enabled: true
  tenant-id-header: X-Tenant-ID
  gateway:
    base-url: http://gateway-service:8080
    tenant-config-endpoint: /api/tenants/{tenantId}/database-config
    connection-timeout: 5000
    read-timeout: 10000
  connection:
    cache-ttl: 3600 # seconds
    max-pool-size: 20
    min-pool-size: 5
    connection-timeout: 30000
    validation-query: SELECT 1
  fallback:
    enabled: false
    default-tenant-id: default
```

### 8. Security Considerations

1. **Tenant Isolation**

   - Ensure no cross-tenant data access
   - Validate tenant ID on every request
   - Log tenant context for audit

2. **Connection Security**

   - Encrypt database passwords in Gateway
   - Use connection pooling limits
   - Implement connection leak detection

3. **Gateway Communication**

   - Use mutual TLS for Gateway communication
   - Implement retry with exponential backoff
   - Handle Gateway unavailability gracefully

4. **JWT Validation**
   - If using JWT claims, validate tenant ID matches authenticated user's tenant
   - Prevent tenant ID spoofing

### 9. Error Handling

1. **Missing Tenant ID**

   - Return 400 Bad Request
   - Clear error message

2. **Invalid Tenant ID**

   - Return 404 Not Found
   - Log security event

3. **Gateway Unavailable**

   - Use cached configuration if available
   - Return 503 Service Unavailable
   - Implement circuit breaker

4. **Database Connection Failure**
   - Return 503 Service Unavailable
   - Log error with tenant ID
   - Health check should reflect tenant-specific status

### 10. Monitoring & Observability

1. **Metrics**

   - Tenant request count
   - Tenant connection pool usage
   - Gateway API call latency
   - Database query performance per tenant

2. **Logging**

   - Include tenant ID in all log statements
   - MDC/Context propagation for tenant ID
   - Log tenant context changes

3. **Health Checks**
   - Per-tenant database health
   - Gateway connectivity
   - Connection pool status

### 11. Migration Strategy

#### Phase 1: Foundation

- Implement TenantContextHolder
- Create TenantContextFilter
- Add Gateway client

#### Phase 2: Connection Management

- Implement TenantConnectionManager
- Create TenantConnectionFactoryProvider
- Integrate with Spring R2DBC

#### Phase 3: Testing

- Unit tests for all components
- Integration tests with multiple tenants
- Load testing

#### Phase 4: Deployment

- Deploy to dev environment
- Validate with Gateway
- Gradual rollout to production

### 12. Testing Strategy

1. **Unit Tests**

   - Tenant context extraction
   - Connection factory routing
   - Gateway client mocking

2. **Integration Tests**

   - Multiple tenant scenarios
   - Connection pooling
   - Error scenarios

3. **Contract Tests**

   - Gateway API contract
   - Tenant configuration format

4. **Performance Tests**
   - Connection pool sizing
   - Gateway API caching effectiveness
   - Concurrent tenant requests

## Implementation Checklist

- [ ] Create TenantContextHolder for reactive context management
- [ ] Implement TenantContextFilter to extract tenant ID
- [ ] Create GatewayTenantService client
- [ ] Implement TenantConnectionManager
- [ ] Create TenantConnectionFactoryProvider
- [ ] Configure TenantAwareDatabaseConfiguration
- [ ] Add application properties for multi-tenancy
- [ ] Implement error handling and validation
- [ ] Add monitoring and logging
- [ ] Write unit and integration tests
- [ ] Update documentation
- [ ] Create migration scripts if needed

## Dependencies

No additional major dependencies required. Will use:

- Spring WebFlux (already present)
- Spring R2DBC (already present)
- Reactor Context (already present)
- Caffeine or Spring Cache for caching
- Spring Cloud OpenFeign or WebClient for Gateway client

## Next Steps

1. Review and approve this plan
2. Set up Gateway API endpoint for tenant configuration
3. Begin Phase 1 implementation
4. Coordinate with Gateway team for API contract
