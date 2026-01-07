# JDBC and R2DBC Support Implementation Summary

## Overview

Successfully implemented support for both JDBC and R2DBC database drivers on a per-tenant basis. Tenants can now choose their preferred driver type (JDBC or R2DBC) through their database configuration.

## Files Created

### 1. Tenant Configuration

- **TenantDatabaseConfig.java** (Modified)
  - Added `driverType` field (defaults to "R2DBC")
  - Added `isJdbc()` and `isR2dbc()` helper methods

### 2. Connection Management

- **TenantJdbcConnectionManager.java** (New)

  - Manages JDBC DataSource connections per tenant
  - Uses HikariCP for connection pooling
  - Caches DataSource instances per tenant

- **TenantConnectionProvider.java** (New)

  - Unified interface for providing both JDBC and R2DBC connections
  - Methods to check driver type and get appropriate connections

- **TenantConnectionProviderImpl.java** (New)
  - Implementation of TenantConnectionProvider
  - Routes to JDBC or R2DBC based on tenant configuration

### 3. Repository Support

- **JdbcRepository.java** (New)

  - Base interface for JDBC repositories
  - Provides standard CRUD operations

- **TenantAwareJdbcRepository.java** (New)
  - Base implementation for tenant-aware JDBC repositories
  - Automatically uses correct DataSource based on tenant context
  - Provides template methods for entity mapping

### 4. Updated Components

- **TenantConnectionFactoryProvider.java** (Modified)
  - Now uses TenantConnectionProvider
  - Supports both JDBC and R2DBC tenants

## Files Modified

1. **TenantDatabaseConfig.java**

   - Added `driverType` field with getters/setters
   - Added `isJdbc()` and `isR2dbc()` helper methods

2. **TenantConnectionFactoryProvider.java**
   - Updated to use TenantConnectionProvider
   - Removed unused connectionManager field

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│              Tenant Database Configuration               │
│  (from Gateway: /api/tenants/{tenantId}/database-config)│
│                                                          │
│  {                                                       │
│    "driverType": "JDBC" | "R2DBC",  // NEW FIELD       │
│    "databaseUrl": "...",                                │
│    ...                                                   │
│  }                                                       │
└─────────────────────────────────────────────────────────┘
                        │
                        ▼
        ┌───────────────────────────────┐
        │  TenantConnectionProvider      │
        │  (Unified Interface)           │
        └───────────────────────────────┘
                        │
        ┌───────────────┴───────────────┐
        │                               │
        ▼                               ▼
┌──────────────────┐          ┌──────────────────┐
│  JDBC Tenants    │          │  R2DBC Tenants    │
│                  │          │                   │
│ TenantJdbc       │          │ TenantConnection  │
│ ConnectionManager│          │ Manager           │
│                  │          │                   │
│ → DataSource     │          │ → ConnectionFactory│
│   (HikariCP)     │          │   (R2DBC Pool)    │
└──────────────────┘          └──────────────────┘
        │                               │
        ▼                               ▼
┌──────────────────┐          ┌──────────────────┐
│ JDBC Repositories│          │ R2DBC Repositories│
│                  │          │                   │
│ JdbcRepository   │          │ R2dbcRepository   │
│ TenantAwareJdbc  │          │ (Existing)        │
│ Repository       │          │                   │
└──────────────────┘          └──────────────────┘
```

## Key Features

### 1. Per-Tenant Driver Selection

- Each tenant can specify `driverType: "JDBC"` or `"R2DBC"` in their configuration
- Defaults to "R2DBC" for backward compatibility

### 2. Unified Connection Provider

- `TenantConnectionProvider` provides a single interface for both driver types
- Automatically routes to correct connection manager based on tenant config

### 3. JDBC Repository Support

- Base classes for creating JDBC repositories
- Automatic tenant-aware DataSource selection
- Template methods for easy entity mapping

### 4. Backward Compatibility

- Existing R2DBC repositories continue to work
- Existing tenants without `driverType` default to R2DBC
- No breaking changes to existing code

## Usage Example

### Gateway Configuration Response

```json
{
  "tenantId": "acme-corp",
  "databaseUrl": "jdbc:postgresql://db.acme.com:5432/acme_db",
  "username": "acme_user",
  "password": "acme_pass",
  "driverType": "JDBC", // ← NEW: Specifies JDBC driver
  "maxPoolSize": 20,
  "connectionTimeout": 30000
}
```

### Creating a JDBC Repository

```java
@Repository
public class UserJdbcRepositoryImpl extends TenantAwareJdbcRepository<User, String> implements UserJdbcRepository {

  public UserJdbcRepositoryImpl(TenantConnectionProvider provider) {
    super(provider);
  }
  // Implement abstract methods...
}

```

### Using in Services

```java
@Service
public class UserService {

  private final TenantConnectionProvider connectionProvider;
  private final UserJdbcRepository jdbcRepo;
  private final UserRepository r2dbcRepo; // R2DBC

  public User findById(String id) {
    if (connectionProvider.isJdbc()) {
      return jdbcRepo.findById(id).orElse(null);
    } else {
      return r2dbcRepo.findById(id).block();
    }
  }
}

```

## Configuration Requirements

### Gateway API

The Gateway must return `driverType` in the tenant database configuration response:

**Endpoint**: `GET /api/tenants/{tenantId}/database-config`

**Response must include**:

```json
{
  "driverType": "JDBC" | "R2DBC"
}
```

### Application Properties

No changes required. Existing configuration works for both driver types.

## Dependencies

All required dependencies are already present:

- `spring-boot-starter-data-jpa` (includes JDBC support)
- `spring-boot-starter-data-r2dbc` (R2DBC support)
- `HikariCP` (JDBC connection pooling)
- `r2dbc-pool` (R2DBC connection pooling)

## Testing Recommendations

1. **Test JDBC tenants**: Create a tenant with `driverType: "JDBC"` and verify JDBC repositories work
2. **Test R2DBC tenants**: Verify existing R2DBC functionality still works
3. **Test mixed tenants**: Have some tenants use JDBC and others use R2DBC
4. **Test fallback**: Verify default tenant behavior when `driverType` is not specified

## Migration Path

### For Existing Tenants

1. **No action required** - They continue using R2DBC by default
2. **To switch to JDBC**: Update Gateway configuration to include `"driverType": "JDBC"`

### For New Tenants

1. Specify `driverType` in Gateway tenant configuration
2. Choose appropriate repository implementation (JDBC or R2DBC)
3. Ensure Gateway API returns `driverType` in configuration response

## Notes

- JDBC operations are blocking (synchronous)
- R2DBC operations are reactive (non-blocking)
- Both use connection pooling for performance
- Connection pools are cached per tenant for efficiency
- Driver type is checked at runtime based on tenant context

## Future Enhancements

Potential improvements:

1. Auto-detect driver type from database URL format
2. Support for hybrid repositories (same entity, different drivers)
3. Metrics and monitoring for both driver types
4. Connection pool tuning per tenant
