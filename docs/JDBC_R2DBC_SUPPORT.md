# JDBC and R2DBC Support for Multi-Tenant Configuration

This document explains how to configure tenants to use either JDBC or R2DBC database drivers based on their preference.

## Overview

The `rms-service` now supports both **JDBC** and **R2DBC** database drivers on a per-tenant basis. Each tenant can specify their preferred driver type in their database configuration.

## Architecture

### Components

1. **TenantDatabaseConfig** - Extended with `driverType` field (JDBC or R2DBC)
2. **TenantConnectionProvider** - Unified interface for getting connections (both JDBC and R2DBC)
3. **TenantJdbcConnectionManager** - Manages JDBC DataSource connections per tenant
4. **TenantConnectionManager** - Manages R2DBC ConnectionFactory connections per tenant (existing)
5. **TenantAwareJdbcRepository** - Base class for JDBC repositories
6. **JdbcRepository** - Base interface for JDBC repositories

### How It Works

1. When a tenant configuration is fetched from the Gateway, it includes a `driverType` field
2. Based on the `driverType`, the system creates either:
   - **JDBC**: A `DataSource` (HikariCP connection pool) via `TenantJdbcConnectionManager`
   - **R2DBC**: A `ConnectionFactory` (R2DBC connection pool) via `TenantConnectionManager`
3. Repositories automatically use the correct connection type based on tenant configuration

## Configuration

### Gateway API Response

The Gateway endpoint `/api/tenants/{tenantId}/database-config` should return a JSON response that includes the `driverType` field:

```json
{
  "tenantId": "tenant-1",
  "databaseUrl": "r2dbc:postgresql://localhost:5432/tenant1_db",
  "username": "tenant1_user",
  "password": "tenant1_pass",
  "driverType": "R2DBC",  // or "JDBC"
  "maxPoolSize": 20,
  "connectionTimeout": 30000,
  "validationQuery": "SELECT 1",
  "clients": [...],
  "keycloakBaseUrl": "...",
  "realmName": "..."
}
```

### Driver Type Values

- **"R2DBC"** (default) - Uses reactive R2DBC driver
- **"JDBC"** - Uses traditional JDBC driver

### Default Behavior

- If `driverType` is not specified, it defaults to **"R2DBC"** for backward compatibility
- Existing tenants without `driverType` will continue to work with R2DBC

## Using JDBC Repositories

### Creating a JDBC Repository

1. Create a repository interface extending `JdbcRepository`:

```java
package com.atparui.rmsservice.repository.jdbc;

import com.atparui.rmsservice.domain.YourEntity;
import java.util.UUID;

public interface YourEntityJdbcRepository extends JdbcRepository<YourEntity, UUID> {
  // Add custom query methods here
}

```

2. Create an implementation extending `TenantAwareJdbcRepository`:

```java
package com.atparui.rmsservice.repository.jdbc;

import com.atparui.rmsservice.domain.YourEntity;
import com.atparui.rmsservice.tenant.TenantConnectionProvider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class YourEntityJdbcRepositoryImpl extends TenantAwareJdbcRepository<YourEntity, UUID> implements YourEntityJdbcRepository {

  public YourEntityJdbcRepositoryImpl(TenantConnectionProvider connectionProvider) {
    super(connectionProvider);
  }

  @Override
  protected RowMapper<YourEntity> getRowMapper() {
    return (rs, rowNum) -> {
      YourEntity entity = new YourEntity();
      entity.setId(UUID.fromString(rs.getString("id")));
      entity.setName(rs.getString("name"));
      // Map other fields...
      return entity;
    };
  }

  @Override
  protected String getTableName() {
    return "your_entity";
  }

  @Override
  protected String getIdColumnName() {
    return "id";
  }

  @Override
  protected UUID getId(YourEntity entity) {
    return entity.getId();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO your_entity (id, name, ...) VALUES (?, ?, ...)";
  }

  @Override
  protected Object[] getInsertParameters(YourEntity entity) {
    return new Object[] {
      entity.getId(),
      entity.getName(),
      // ... other fields
    };
  }

  @Override
  protected String getUpdateSql() {
    return "UPDATE your_entity SET name = ?, ... WHERE id = ?";
  }

  @Override
  protected Object[] getUpdateParameters(YourEntity entity) {
    return new Object[] {
      entity.getName(),
      // ... other fields
      entity.getId(),
    };
  }
}

```

### Using JDBC Repository in Services

```java
@Service
public class YourEntityService {

  private final TenantConnectionProvider connectionProvider;
  private final YourEntityJdbcRepository jdbcRepository;
  private final YourEntityR2dbcRepository r2dbcRepository; // R2DBC version

  public YourEntityService(
    TenantConnectionProvider connectionProvider,
    YourEntityJdbcRepository jdbcRepository,
    YourEntityR2dbcRepository r2dbcRepository
  ) {
    this.connectionProvider = connectionProvider;
    this.jdbcRepository = jdbcRepository;
    this.r2dbcRepository = r2dbcRepository;
  }

  public YourEntity findById(UUID id) {
    if (connectionProvider.isJdbc()) {
      // Use JDBC repository
      return jdbcRepository.findById(id).orElse(null);
    } else {
      // Use R2DBC repository (reactive)
      return r2dbcRepository.findById(id).block();
    }
  }
}

```

## Using R2DBC Repositories (Existing)

R2DBC repositories continue to work as before. They automatically use the correct connection based on tenant configuration.

```java
@Repository
public interface YourEntityRepository extends R2dbcRepository<YourEntity, UUID> {
  // R2DBC methods
}

```

## Connection Management

### Automatic Connection Selection

The system automatically selects the correct connection type based on tenant configuration:

- **R2DBC tenants**: Use `TenantConnectionManager` → `ConnectionFactory`
- **JDBC tenants**: Use `TenantJdbcConnectionManager` → `DataSource`

### Connection Pooling

- **R2DBC**: Uses `r2dbc-pool` for connection pooling
- **JDBC**: Uses `HikariCP` for connection pooling

Both connection pools are configured with:

- `maxPoolSize` from tenant configuration
- `minPoolSize` from application properties
- `connectionTimeout` from tenant configuration
- `validationQuery` from tenant configuration

## Migration Guide

### For Existing Tenants

1. **No changes required** - Existing tenants will continue to use R2DBC by default
2. **To switch to JDBC**: Update tenant configuration in Gateway to include `"driverType": "JDBC"`

### For New Tenants

1. Specify `driverType` in tenant database configuration
2. Choose appropriate repository type (JDBC or R2DBC) based on driver type
3. Ensure Gateway returns the correct `driverType` in the configuration response

## Best Practices

1. **Consistency**: Use the same driver type for all repositories of a tenant
2. **Performance**:
   - R2DBC is better for reactive, non-blocking operations
   - JDBC is better for traditional blocking operations
3. **Testing**: Test both JDBC and R2DBC paths if your service supports both
4. **Error Handling**: Always check `connectionProvider.isJdbc()` before using JDBC-specific code

## Troubleshooting

### Issue: "Current tenant does not use JDBC"

**Solution**: Check tenant configuration. The tenant's `driverType` must be set to "JDBC" in the Gateway configuration.

### Issue: "Tenant ID not found in context"

**Solution**: Ensure tenant ID is set in the request context (via header or JWT claim).

### Issue: Connection pool errors

**Solution**:

- Check `maxPoolSize` in tenant configuration
- Verify database credentials
- Check network connectivity to database

## Example: Complete Service with Both Drivers

```java
@Service
public class FlexibleEntityService {

  private final TenantConnectionProvider connectionProvider;
  private final YourEntityJdbcRepository jdbcRepo;
  private final YourEntityR2dbcRepository r2dbcRepo;

  public YourEntity findById(UUID id) {
    if (connectionProvider.isJdbc()) {
      return jdbcRepo.findById(id).orElse(null);
    } else {
      return r2dbcRepo.findById(id).block();
    }
  }

  public List<YourEntity> findAll() {
    if (connectionProvider.isJdbc()) {
      return jdbcRepo.findAll();
    } else {
      return r2dbcRepo.findAll().collectList().block();
    }
  }
}

```

## API Reference

### TenantConnectionProvider

```java
// Check if current tenant uses JDBC
boolean isJdbc = connectionProvider.isJdbc();

// Get JDBC DataSource for current tenant
DataSource dataSource = connectionProvider.getJdbcDataSource();

// Get R2DBC ConnectionFactory for current tenant (reactive)
Mono<ConnectionFactory> factory = connectionProvider.getR2dbcConnectionFactory();

```

### TenantDatabaseConfig

```java
// Check driver type
boolean isJdbc = config.isJdbc();

boolean isR2dbc = config.isR2dbc();

// Get driver type
String driverType = config.getDriverType(); // "JDBC" or "R2DBC"

```
