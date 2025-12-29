# Gateway OAuth2 API Requirements

This document describes what the Gateway needs to provide for the RMS Service to support multi-tenant OAuth2/OIDC authentication with separate realms and clients per tenant.

## Overview

The RMS Service supports:

- **Separate Keycloak realms per tenant** (e.g., `tenant1_realm`, `tenant2_realm`)
- **Separate OAuth2 clients per tenant and client type** (web and mobile)
- **Dynamic client registration** resolved at runtime based on tenant ID and client type

## Gateway Endpoint

The Gateway must provide an endpoint that returns tenant configuration including OAuth2 client information.

### Endpoint

```
GET /api/tenants/{tenantId}/database-config
```

### Request Headers

- `Authorization: Bearer <token>` (optional, if Gateway requires authentication)

### Response Format

The response must be a JSON object with the following structure:

```json
{
  "tenantId": "tenant-123",
  "databaseUrl": "r2dbc:postgresql://localhost:5432/tenant_123_db",
  "username": "tenant_123_user",
  "password": "tenant_123_password",
  "maxPoolSize": 20,
  "connectionTimeout": 30000,
  "validationQuery": "SELECT 1",
  "keycloakBaseUrl": "https://rmsauth.atparui.com",
  "realmName": "tenant-123_realm",
  "clients": [
    {
      "clientId": "rms-service-web",
      "clientSecret": "web-client-secret-here",
      "clientType": "web"
    },
    {
      "clientId": "rms-service-mobile",
      "clientSecret": "mobile-client-secret-here",
      "clientType": "mobile"
    }
  ]
}
```

### Response Fields

#### Required Fields

| Field             | Type   | Description                                                           |
| ----------------- | ------ | --------------------------------------------------------------------- |
| `tenantId`        | string | The tenant identifier (must match the `{tenantId}` path parameter)    |
| `keycloakBaseUrl` | string | Base URL of Keycloak server (e.g., `https://rmsauth.atparui.com`)     |
| `realmName`       | string | Name of the Keycloak realm for this tenant (e.g., `tenant-123_realm`) |
| `clients`         | array  | Array of OAuth2 client configurations                                 |

#### Optional Fields (for database configuration)

| Field               | Type    | Description                        |
| ------------------- | ------- | ---------------------------------- |
| `databaseUrl`       | string  | R2DBC database URL                 |
| `username`          | string  | Database username                  |
| `password`          | string  | Database password                  |
| `maxPoolSize`       | integer | Maximum connection pool size       |
| `connectionTimeout` | integer | Connection timeout in milliseconds |
| `validationQuery`   | string  | SQL query to validate connections  |

#### Client Configuration Object

Each object in the `clients` array must have:

| Field          | Type   | Required | Description                  |
| -------------- | ------ | -------- | ---------------------------- |
| `clientId`     | string | Yes      | OAuth2 client ID in Keycloak |
| `clientSecret` | string | Yes      | OAuth2 client secret         |
| `clientType`   | string | Yes      | Either `"web"` or `"mobile"` |

### Example Responses

#### Success Response (200 OK)

```json
{
  "tenantId": "acme-corp",
  "databaseUrl": "r2dbc:postgresql://db.example.com:5432/acme_corp_db",
  "username": "acme_corp_user",
  "password": "secure_password_123",
  "maxPoolSize": 20,
  "connectionTimeout": 30000,
  "validationQuery": "SELECT 1",
  "keycloakBaseUrl": "https://rmsauth.atparui.com",
  "realmName": "acme-corp_realm",
  "clients": [
    {
      "clientId": "rms-service-web",
      "clientSecret": "web-secret-abc123",
      "clientType": "web"
    },
    {
      "clientId": "rms-service-mobile",
      "clientSecret": "mobile-secret-xyz789",
      "clientType": "mobile"
    }
  ]
}
```

#### Error Response (404 Not Found)

```json
{
  "error": "Tenant not found",
  "message": "Tenant with ID 'invalid-tenant' does not exist",
  "status": 404
}
```

#### Error Response (500 Internal Server Error)

```json
{
  "error": "Internal Server Error",
  "message": "Failed to retrieve tenant configuration",
  "status": 500
}
```

## Gateway Implementation Requirements

### 1. Tenant Creation Flow

When a new tenant is created in the Gateway:

1. **Create Keycloak Realm**

   - Realm name format: `{tenantId}_realm`
   - Example: For tenant `acme-corp`, create realm `acme-corp_realm`

2. **Create OAuth2 Clients in Realm**

   - Create a **web client**:
     - Client ID: `rms-service-web` (or your naming convention)
     - Client authentication: `ON` (confidential client)
     - Standard flow: `ON`
     - Valid redirect URIs: Include RMS Service callback URLs
     - Generate and store the client secret
   - Create a **mobile client**:
     - Client ID: `rms-service-mobile` (or your naming convention)
     - Client authentication: `ON` (confidential client)
     - Standard flow: `ON`
     - Valid redirect URIs: Include mobile app callback URLs
     - Generate and store the client secret

3. **Store Configuration**
   - Store the tenant configuration in your database/storage
   - Include:
     - Tenant ID
     - Keycloak base URL
     - Realm name
     - Client IDs and secrets for both web and mobile
     - Database configuration (if applicable)

### 2. Keycloak Client Configuration

For each OAuth2 client created in Keycloak:

#### Required Settings

- **Client ID**: Unique identifier (e.g., `rms-service-web`, `rms-service-mobile`)
- **Client authentication**: `ON` (confidential client)
- **Authorization**: `OFF` (unless you need fine-grained authorization)
- **Standard flow**: `ON` (for authorization code flow)
- **Direct access grants**: `OFF` (unless needed)
- **Service accounts roles**: `ON` (if service-to-service communication is needed)

#### Redirect URIs

For **web client**:

```
http://localhost:8083/login/oauth2/code/oidc
https://your-rms-service-domain.com/login/oauth2/code/oidc
```

For **mobile client**:

```
your-mobile-app://oauth2/callback
```

#### Client Scopes

Ensure these scopes are available:

- `openid`
- `profile`
- `email`
- `offline_access` (for refresh tokens)

#### Client Secret

- Generate a strong, random client secret
- Store it securely in your database
- Include it in the API response (the RMS Service will cache it securely)

### 3. Realm Naming Convention

**Recommended pattern**: `{tenantId}_realm`

Examples:

- Tenant ID: `acme-corp` → Realm: `acme-corp_realm`
- Tenant ID: `tenant-123` → Realm: `tenant-123_realm`

**Important**: The RMS Service's `TenantExtractor` expects this pattern to extract tenant ID from JWT issuer URIs.

### 4. Caching Considerations

The RMS Service caches tenant configurations (including OAuth2 client secrets) for performance. Cache TTL is configurable (default: 3600 seconds).

**When to invalidate cache**:

- When tenant configuration is updated
- When OAuth2 client secrets are rotated
- When realm is deleted or renamed

**Cache invalidation endpoint** (optional):

```
POST /api/tenants/{tenantId}/invalidate-cache
```

This endpoint can notify the RMS Service to clear its cache for a specific tenant.

## RMS Service Request Headers

The RMS Service uses the following headers to determine tenant and client type:

### Required Headers

| Header        | Description       | Example     |
| ------------- | ----------------- | ----------- |
| `X-Tenant-ID` | Tenant identifier | `acme-corp` |

### Optional Headers

| Header          | Description                 | Example           | Default |
| --------------- | --------------------------- | ----------------- | ------- |
| `X-Client-Type` | Client type (web or mobile) | `web` or `mobile` | `web`   |

### Fallback Behavior

If `X-Tenant-ID` is not provided, the RMS Service will attempt to extract tenant ID from:

1. JWT token `tenant_id` claim
2. JWT token `iss` (issuer) claim by parsing realm name

## Testing

### Test the Gateway Endpoint

```bash
# Test with curl
curl -X GET "http://gateway:8082/api/tenants/acme-corp/database-config" \
  -H "Authorization: Bearer <token>"
```

### Verify Response Structure

Ensure the response includes:

- ✅ `tenantId` matches the path parameter
- ✅ `keycloakBaseUrl` is a valid URL
- ✅ `realmName` follows the naming convention
- ✅ `clients` array contains at least one client
- ✅ Each client has `clientId`, `clientSecret`, and `clientType`
- ✅ At least one client with `clientType: "web"` exists

## Security Considerations

1. **Client Secrets**: Store client secrets securely in the Gateway database
2. **HTTPS**: Always use HTTPS for Keycloak base URL in production
3. **Secret Rotation**: Implement a process to rotate client secrets periodically
4. **Access Control**: Ensure the Gateway endpoint is properly secured
5. **Audit Logging**: Log all tenant configuration access

## Troubleshooting

### Common Issues

1. **"No OAuth2 client configuration found"**

   - Verify the `clients` array in the response
   - Ensure at least one client with the requested `clientType` exists

2. **"No issuer URI found"**

   - Verify `keycloakBaseUrl` and `realmName` are present in the response
   - Check that the realm name follows the naming convention

3. **"Tenant not found"**

   - Verify the tenant exists in the Gateway
   - Check that the tenant ID matches exactly (case-sensitive)

4. **JWT validation fails**
   - Verify the realm exists in Keycloak
   - Check that the realm name matches exactly
   - Ensure Keycloak is accessible from the RMS Service

## Next Steps

1. Implement the Gateway endpoint `/api/tenants/{tenantId}/database-config`
2. Create Keycloak realms and clients when tenants are created
3. Store tenant configurations with OAuth2 client details
4. Test the endpoint with sample tenant data
5. Configure cache invalidation (optional)
