# Multi-Tenant OAuth2/OIDC Implementation

## Overview

This document describes the implementation of dynamic OAuth2/OIDC client registration for multi-tenant support in the RMS Service. The system supports:

- **Separate Keycloak realms per tenant** (e.g., `tenant1_realm`, `tenant2_realm`)
- **Separate OAuth2 clients per tenant and client type** (web and mobile)
- **Dynamic client registration** resolved at runtime based on tenant ID and client type

## Architecture

### Components

1. **TenantDatabaseConfig** - Extended to include OAuth2 realm and client information
2. **TenantOAuth2ClientService** - Service to fetch and cache OAuth2 client configurations
3. **DynamicOAuth2ClientRegistrationRepository** - Dynamic client registration repository
4. **ClientTypeFilter** - Filter to extract client type from request headers
5. **GatewayTenantService** - Existing service that fetches tenant configs from Gateway

### Flow

```
Request with X-Tenant-ID and X-Client-Type headers
    ↓
ClientTypeFilter extracts client type → Reactor Context
    ↓
TenantContextFilter extracts tenant ID → Reactor Context
    ↓
DynamicOAuth2ClientRegistrationRepository.findByRegistrationId("oidc")
    ↓
Resolves tenant ID and client type from context
    ↓
TenantOAuth2ClientService.getOAuth2ClientConfig(tenantId, clientType)
    ↓
Fetches from GatewayTenantService (cached)
    ↓
Builds ClientRegistration with tenant-specific OAuth2 client
    ↓
Spring Security uses the client registration for OAuth2 flows
```

## Implementation Details

### 1. TenantDatabaseConfig Updates

**File**: `src/main/java/com/atparui/rmsservice/tenant/domain/TenantDatabaseConfig.java`

**New Fields**:

- `keycloakBaseUrl` - Base URL of Keycloak server
- `realmName` - Name of the Keycloak realm for this tenant

**New Methods**:

- `getIssuerUri()` - Returns the full issuer URI: `{keycloakBaseUrl}/realms/{realmName}`
- `getClientByType(String clientType)` - Returns client config for web or mobile

### 2. TenantOAuth2ClientService

**File**: `src/main/java/com/atparui/rmsservice/tenant/TenantOAuth2ClientService.java`

**Purpose**: Fetches and caches OAuth2 client configurations for tenants.

**Key Features**:

- Caches OAuth2 configs separately from database configs
- Provides `getOAuth2ClientConfig(tenantId, clientType)` method
- Cache invalidation support

### 3. DynamicOAuth2ClientRegistrationRepository

**File**: `src/main/java/com/atparui/rmsservice/config/DynamicOAuth2ClientRegistrationRepository.java`

**Purpose**: Implements `ReactiveClientRegistrationRepository` to provide dynamic OAuth2 client registrations.

**Key Features**:

- Resolves client registrations based on tenant ID and client type
- Caches client registrations to avoid recreation
- Extracts tenant ID from Reactor context
- Extracts client type from Reactor context (set by ClientTypeFilter)

**Registration ID Format**: `{tenantId}-{clientType}` (e.g., `acme-corp-web`)

### 4. ClientTypeFilter

**File**: `src/main/java/com/atparui/rmsservice/tenant/ClientTypeFilter.java`

**Purpose**: Extracts client type from `X-Client-Type` header and sets it in Reactor context.

**Behavior**:

- Reads `X-Client-Type` header
- Validates value is "web" or "mobile"
- Defaults to "web" if not provided or invalid
- Sets value in Reactor context as `CLIENT_TYPE`

**Order**: `-99` (executes after TenantContextFilter which is `-100`)

### 5. SecurityConfiguration

**File**: `src/main/java/com/atparui/rmsservice/config/SecurityConfiguration.java`

**Changes**: No changes needed! Spring Security automatically uses `DynamicOAuth2ClientRegistrationRepository` because it's annotated with `@Component` and implements `ReactiveClientRegistrationRepository`.

## Request Headers

### Required

| Header        | Description       | Example     |
| ------------- | ----------------- | ----------- |
| `X-Tenant-ID` | Tenant identifier | `acme-corp` |

### Optional

| Header          | Description                 | Example           | Default |
| --------------- | --------------------------- | ----------------- | ------- |
| `X-Client-Type` | Client type (web or mobile) | `web` or `mobile` | `web`   |

## Gateway API Requirements

The Gateway must provide the following endpoint:

```
GET /api/tenants/{tenantId}/database-config
```

**Response must include**:

- `keycloakBaseUrl` - Base URL of Keycloak
- `realmName` - Realm name (format: `{tenantId}_realm`)
- `clients` - Array of client configurations:
  - `clientId` - OAuth2 client ID
  - `clientSecret` - OAuth2 client secret
  - `clientType` - Either "web" or "mobile"

See `GATEWAY_OAUTH2_API_REQUIREMENTS.md` for complete API specification.

## Configuration

### Application Properties

No additional configuration needed! The system uses existing multi-tenant configuration:

```yaml
multi-tenant:
  enabled: true
  tenant-id-header: X-Tenant-ID
  jwt-tenant-claim: tenant_id
  gateway:
    base-url: http://localhost:8082
    tenant-config-endpoint: /api/tenants/{tenantId}/database-config
```

### Environment Variables

No new environment variables required. The system uses:

- Existing `MULTI_TENANT_GATEWAY_BASE_URL` for Gateway URL
- Existing tenant config endpoint configuration

## Caching

### Cache Strategy

1. **Tenant Config Cache** (GatewayTenantService)

   - TTL: Configurable (default: 3600 seconds)
   - Key: `tenantId`
   - Contains: Full tenant config including OAuth2 clients

2. **OAuth2 Client Config Cache** (TenantOAuth2ClientService)

   - TTL: Same as tenant config cache
   - Key: `tenantId:clientType`
   - Contains: Tenant config (reused from GatewayTenantService)

3. **Client Registration Cache** (DynamicOAuth2ClientRegistrationRepository)
   - TTL: 1 hour
   - Key: `tenantId:clientType`
   - Contains: Built `ClientRegistration` objects

### Cache Invalidation

To invalidate caches:

```java
// Invalidate tenant config
gatewayTenantService.invalidateCache(tenantId);

// Invalidate OAuth2 client config
tenantOAuth2ClientService.invalidateCache(tenantId);

// Invalidate client registration
dynamicOAuth2ClientRegistrationRepository.invalidateCache(tenantId);
```

## Usage Examples

### OAuth2 Login Flow

When a user initiates OAuth2 login:

1. Request arrives with `X-Tenant-ID: acme-corp` and `X-Client-Type: web`
2. `ClientTypeFilter` sets `CLIENT_TYPE=web` in context
3. `TenantContextFilter` sets `TENANT_ID=acme-corp` in context
4. Spring Security calls `findByRegistrationId("oidc")`
5. `DynamicOAuth2ClientRegistrationRepository` resolves:
   - Tenant ID: `acme-corp`
   - Client Type: `web`
6. Fetches OAuth2 client config from Gateway (cached)
7. Builds `ClientRegistration` with:
   - Issuer: `https://rmsauth.atparui.com/realms/acme-corp_realm`
   - Client ID: `rms-service-web`
   - Client Secret: (from Gateway)
8. Spring Security uses this registration for OAuth2 flow

### JWT Token Validation

JWT token validation uses `DynamicJwtDecoder` (existing implementation):

- Extracts issuer from JWT token
- Creates/retrieves JWT decoder for that issuer
- Validates token against the correct Keycloak realm

## Testing

### Unit Tests

Test the components individually:

```java
// Test TenantOAuth2ClientService
@Test
void testGetOAuth2ClientConfig() {
  Mono<TenantDatabaseConfig> config = service.getOAuth2ClientConfig("tenant-1", "web");
  // Assertions...
}

// Test DynamicOAuth2ClientRegistrationRepository
@Test
void testResolveClientRegistration() {
  Mono<ClientRegistration> registration = repository.resolveClientRegistration("tenant-1", "web");
  // Assertions...
}

```

### Integration Tests

Test the full flow:

```java
@Test
void testOAuth2LoginFlow() {
  // Make request with X-Tenant-ID and X-Client-Type headers
  // Verify OAuth2 redirect uses correct client registration
}

```

## Troubleshooting

### Issue: "No OAuth2 client configuration found"

**Cause**: Gateway response missing client config or wrong client type.

**Solution**:

1. Verify Gateway endpoint returns `clients` array
2. Ensure at least one client with requested `clientType` exists
3. Check Gateway logs for errors

### Issue: "No issuer URI found"

**Cause**: Gateway response missing `keycloakBaseUrl` or `realmName`.

**Solution**:

1. Verify Gateway response includes both fields
2. Check that realm name follows naming convention

### Issue: Client registration not found

**Cause**: Tenant ID or client type not in Reactor context.

**Solution**:

1. Verify `X-Tenant-ID` header is present
2. Check `ClientTypeFilter` is executing (order: -99)
3. Verify `TenantContextFilter` is executing (order: -100)

## Security Considerations

1. **Client Secrets**: Stored securely in Gateway, cached in RMS Service
2. **HTTPS**: Always use HTTPS for Keycloak base URL in production
3. **Secret Rotation**: Implement cache invalidation when secrets rotate
4. **Access Control**: Gateway endpoint must be properly secured
5. **Audit Logging**: Log all tenant configuration access

## Future Enhancements

1. **Cache Invalidation Endpoint**: Add endpoint to invalidate caches remotely
2. **Client Secret Rotation**: Automatic handling of secret rotation
3. **Multiple Client Types**: Support additional client types beyond web/mobile
4. **Fallback Configuration**: Default OAuth2 config if Gateway unavailable

## Related Documentation

- `GATEWAY_OAUTH2_API_REQUIREMENTS.md` - Gateway API specification
- `MULTI_TENANT_IMPLEMENTATION_PLAN.md` - Overall multi-tenant architecture
- `MULTI_TENANT_OAUTH2_SETUP.md` - OAuth2 setup guide
