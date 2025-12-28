# Multi-Tenant OAuth2 Configuration - Implementation Summary

## Overview

This document describes the implementation of dynamic JWT token validation for multi-tenant RMS Service. The service can now validate tokens from multiple Keycloak realms (one per tenant) dynamically.

## Architecture

### Token Flow

1. **User Authentication**: Users authenticate against their tenant-specific Keycloak realm (e.g., `tenant1_realm`)
2. **Gateway Validation**: Gateway validates tokens from tenant realms
3. **Service Validation**: RMS Service receives tokens and validates them dynamically against the correct realm based on the token's issuer claim

### Key Components

#### Gateway App (`rms`)

- **KeycloakRealmService**: Automatically creates `rms-service` client in each new tenant realm
- **Configuration**: `rms-service.client-secret` property for client secret

#### RMS Service (`rms-service`)

- **DynamicJwtDecoder**: Extracts issuer from token and validates against the correct realm
- **TenantExtractor**: Utility to extract tenant ID from JWT tokens
- **SecurityConfiguration**: Updated to use dynamic decoder

## Implementation Details

### 1. Gateway App Changes

#### KeycloakRealmService.java

- Added `createRmsServiceClient()` method
- Creates `rms-service` client in each tenant realm during realm creation
- Client configuration:
  - Client ID: `rms-service`
  - Type: Confidential
  - Service Accounts: Enabled
  - Direct Access Grants: Disabled

#### Configuration

- Added `rms-service.client-secret` property in `application-dev.yml`
- Can be set via environment variable: `RMS_SERVICE_CLIENT_SECRET`

### 2. RMS Service Changes

#### DynamicJwtDecoder.java (New)

- Implements `ReactiveJwtDecoder`
- Extracts issuer from JWT token without full validation
- Creates/caches JWT decoders per issuer (realm)
- Validates tokens against the correct realm dynamically
- Supports audience validation

#### TenantExtractor.java (New)

- Extracts tenant ID from JWT tokens
- Supports extraction from:
  - `tenant_id` claim (if present)
  - `iss` (issuer) claim by parsing realm name
- Handles gateway realm tokens

#### SecurityConfiguration.java

- Updated to use `DynamicJwtDecoder` instead of static decoder
- Removed hardcoded issuer URI dependency

#### Configuration Files

- Updated `application.yml` to use gateway realm as default issuer
- Client ID changed from `internal` to `rms-service`
- Client secret now configurable via environment variable

## Manual Keycloak Setup

### Step 1: Create rms-service Client in Gateway Realm

1. Log into Keycloak Admin Console
2. Select the `gateway` realm
3. Go to **Clients** → **Create client**
4. Configure:
   - **Client ID**: `rms-service`
   - **Client authentication**: `ON`
   - **Authorization**: `OFF`
   - **Authentication flow**: Standard flow `OFF`, Direct access grants `OFF`, Service accounts roles `ON`
5. Click **Next**
6. Configure:
   - **Valid redirect URIs**: `*` (or specific service URLs)
   - **Web origins**: `*` (or specific origins)
   - **Client scopes**: `openid`, `profile`, `email`, `offline_access`
7. Click **Save**
8. Go to **Credentials** tab
9. Copy the **Client secret** (you'll need this for RMS Service configuration)

### Step 2: Configure Gateway App

Set the environment variable or configuration property:

```bash
RMS_SERVICE_CLIENT_SECRET=<client-secret-from-step-1>
```

This secret will be used when creating `rms-service` clients in tenant realms.

### Step 3: Configure RMS Service

Set the following environment variables:

```bash
KEYCLOAK_BASE_URL=https://rmsauth.atparui.com
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_OIDC_ISSUER_URI=https://rmsauth.atparui.com/realms/gateway
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_ID=rms-service
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_SECRET=<client-secret-from-step-1>
```

## How It Works

### Token Validation Flow

1. **Request arrives at RMS Service** with JWT token in `Authorization` header
2. **DynamicJwtDecoder** extracts issuer from token:
   - Parses JWT payload (base64url decoded)
   - Extracts `iss` claim
   - Example: `https://rmsauth.atparui.com/realms/tenant1_realm`
3. **Decoder Resolution**:
   - Checks cache for existing decoder for this issuer
   - If not cached, creates new decoder:
     - Builds JWK Set URI: `{issuer}/protocol/openid-connect/certs`
     - Creates `NimbusReactiveJwtDecoder` with JWK Set URI
     - Configures validators (issuer, audience)
     - Caches decoder for future use
4. **Token Validation**:
   - Decoder validates token signature using JWK Set from correct realm
   - Validates issuer, expiration, audience
5. **Tenant Extraction**:
   - `TenantExtractor` extracts tenant ID from validated token
   - Used for database routing and tenant context

### Benefits

1. **Multi-Realm Support**: Service can validate tokens from any tenant realm
2. **Automatic Realm Detection**: No configuration needed per tenant
3. **Performance**: Decoders are cached per issuer
4. **Security**: Each token validated against its issuing realm
5. **Scalability**: Works with unlimited number of tenants

## Testing

### Test Token Validation

1. **Get token from tenant realm**:

   ```bash
   curl -X POST "https://rmsauth.atparui.com/realms/tenant1_realm/protocol/openid-connect/token" \
     -d "client_id=tenant1_web" \
     -d "client_secret=<secret>" \
     -d "grant_type=client_credentials"
   ```

2. **Call RMS Service with token**:

   ```bash
   curl -H "Authorization: Bearer <token>" \
     http://localhost:8083/api/orders
   ```

3. **Verify**:
   - Token should be validated successfully
   - Tenant ID should be extracted correctly
   - Request should route to correct tenant database

### Troubleshooting

#### Token Validation Fails

- Check that `rms-service` client exists in the token's issuer realm
- Verify client secret matches in both Keycloak and service configuration
- Check token expiration
- Verify issuer URI format

#### Tenant ID Not Extracted

- Check token has `iss` claim
- Verify realm name follows pattern: `{tenantId}_realm`
- Check logs for extraction errors

#### Decoder Creation Fails

- Verify Keycloak is accessible from service
- Check JWK Set URI is correct: `{issuer}/protocol/openid-connect/certs`
- Verify network connectivity

## Environment Variables Summary

### Gateway App (`rms`)

```bash
RMS_SERVICE_CLIENT_SECRET=<secret-from-gateway-realm>
```

### RMS Service (`rms-service`)

```bash
KEYCLOAK_BASE_URL=https://rmsauth.atparui.com
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_OIDC_ISSUER_URI=https://rmsauth.atparui.com/realms/gateway
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_ID=rms-service
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_SECRET=<secret-from-gateway-realm>
```

## Future Enhancements

1. **Per-Tenant Client Secrets**: Support different secrets per tenant realm
2. **Secret Rotation**: Automatic secret rotation support
3. **Metrics**: Add metrics for decoder cache hits/misses
4. **Health Checks**: Add health check for Keycloak connectivity per realm
