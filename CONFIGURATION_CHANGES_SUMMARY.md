# Configuration Changes Summary

## Overview

This document summarizes all configuration changes made to set up the RMS Service with:

- Database schema: `rms-service`
- Database user: `rms-service`
- Default realm: `rms-service`
- OAuth2 clients: `rms-service-web` and `rms-service-mobile`
- Default tenant: `default` with subdomains `rms.atparui.com` (UI) and `rmsgw.atparui.com` (backend)

## Files Modified

### 1. Application Configuration Files

#### `src/main/resources/config/application.yml`

- **OAuth2 Configuration:**
  - Changed issuer URI from `/realms/gateway` to `/realms/rms-service`
  - Changed default client ID from `rms-service` to `rms-service-web`
- **Multi-Tenant Configuration:**
  - Updated gateway base URL to use `https://rmsgw.atparui.com` (configurable via `GATEWAY_BASE_URL`)
  - Enabled fallback mechanism (`fallback.enabled: true`)
  - Added default tenant configuration with subdomain settings

#### `src/main/resources/config/application-dev.yml`

- **Database Configuration:**
  - Updated Liquibase URL to include schema: `jdbc:postgresql://localhost:5432/rmsservice?currentSchema=rms-service`
  - Added `default-schema: rms-service` for Liquibase
  - Added `user: rmsservice` for Liquibase
  - Updated R2DBC URL to include search path: `r2dbc:postgresql://localhost:5432/rmsservice?options=--search_path%3Drms-service`
  - Added password configuration using `DB_PASSWORD` environment variable

#### `src/main/resources/config/application-prod.yml`

- **Database Configuration:**
  - Updated Liquibase URL to include schema: `jdbc:postgresql://localhost:5432/rmsservice?currentSchema=rms-service`
  - Added `default-schema: rms-service` for Liquibase
  - Added `user: rmsservice` for Liquibase
  - Updated R2DBC URL to include search path: `r2dbc:postgresql://localhost:5432/rmsservice?options=--search_path%3Drms-service`
  - Added password configuration using `DB_PASSWORD` environment variable

### 2. Environment Variable Files

#### `intellij-env-variables.txt`

- Updated OAuth2 configuration:
  - Added `KEYCLOAK_BASE_URL=https://rmsauth.atparui.com`
  - Changed issuer URI to `/realms/rms-service`
  - Changed client ID to `rms-service-web`
- Updated Gateway configuration:
  - Added `GATEWAY_BASE_URL=https://rmsgw.atparui.com`
  - Updated `MULTI_TENANT_GATEWAY_BASE_URL` to use `https://rmsgw.atparui.com`
- Updated database configuration:
  - Added `DB_PASSWORD` variable
  - Updated R2DBC and Liquibase URLs to include schema parameters

#### `intellij-env-variables-single-line.txt`

- Updated with all the same changes in single-line format

### 3. Documentation Files

#### `DATABASE_SETUP.md` (New)

- Created comprehensive database setup guide
- Includes SQL scripts for creating database, user, and schema
- Provides troubleshooting tips
- Documents connection URL formats

## Configuration Details

### Database Configuration

**Schema:** `rms-service`
**User:** `rms-service`
**Database:** `rmsservice`

**R2DBC Connection URL:**

```
r2dbc:postgresql://localhost:5432/rmsservice?options=--search_path%3Drms-service
```

**JDBC Connection URL (Liquibase):**

```
jdbc:postgresql://localhost:5432/rmsservice?currentSchema=rms-service
```

### OAuth2 Configuration

**Realm:** `rms-service`
**Default Client:** `rms-service-web`
**Additional Client:** `rms-service-mobile` (to be configured in Keycloak)

**Issuer URI:**

```
https://rmsauth.atparui.com/realms/rms-service
```

### Multi-Tenant Configuration

**Gateway Backend URL:** `https://rmsgw.atparui.com`
**Gateway UI URL:** `https://rms.atparui.com`
**Default Tenant ID:** `default`
**Fallback:** Enabled

### Environment Variables

Key environment variables that can be overridden:

- `DB_PASSWORD` - Database password (optional for local dev)
- `KEYCLOAK_BASE_URL` - Keycloak base URL (default: `https://rmsauth.atparui.com`)
- `GATEWAY_BASE_URL` - Gateway backend URL (default: `https://rmsgw.atparui.com`)
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_ID` - OAuth2 client ID (default: `rms-service-web`)
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_SECRET` - OAuth2 client secret

## Next Steps

### 1. Database Setup

1. Create the PostgreSQL database: `rmsservice`
2. Create the user/role: `rms-service`
3. Create the schema: `rms-service` (or let Liquibase create it)
4. Grant appropriate permissions

See `DATABASE_SETUP.md` for detailed SQL scripts.

### 2. Keycloak Configuration

1. Create realm: `rms-service`
2. Create clients:
   - `rms-service-web` (for web application)
   - `rms-service-mobile` (for mobile application)
3. Configure client secrets and redirect URIs
4. Set up appropriate roles and permissions

### 3. Gateway Configuration

1. Ensure Gateway is accessible at `https://rmsgw.atparui.com`
2. Configure default tenant in Gateway with:
   - Tenant ID: `default`
   - Database configuration pointing to `rms-service` schema
   - Subdomain: `rms.atparui.com` (UI) and `rmsgw.atparui.com` (backend)

### 4. Testing

1. Start the application
2. Verify Liquibase creates all tables in the `rms-service` schema
3. Test OAuth2 authentication with `rms-service-web` client
4. Verify multi-tenant fallback works for default tenant
5. Test database connections using the `rms-service` schema

## Important Notes

1. **Schema Naming:** PostgreSQL is case-sensitive for quoted identifiers. The schema `rms-service` must be created with quotes: `CREATE SCHEMA "rms-service"`

2. **R2DBC Schema Parameter:** The R2DBC PostgreSQL driver uses the `options` parameter with URL-encoded `--search_path` to set the schema: `?options=--search_path%3Drms-service`

3. **Liquibase Schema:** Liquibase uses the `currentSchema` parameter in JDBC URL and the `default-schema` property to set the schema.

4. **Fallback Mechanism:** The fallback is enabled, so if a tenant ID is not found in the request context, it will use the default tenant (`default`). This allows the service to start and work even if the Gateway is not available.

5. **Default Connection Factory:** When fallback is used, the default connection factory (configured with `rms-service` schema) will be used, ensuring the service can operate without Gateway dependency for the default tenant.

## Verification Checklist

- [ ] Database `rmsservice` created
- [ ] User `rms-service` created with appropriate permissions
- [ ] Schema `rms-service` created
- [ ] Keycloak realm `rms-service` created
- [ ] Keycloak clients `rms-service-web` and `rms-service-mobile` created
- [ ] Gateway accessible at `https://rmsgw.atparui.com`
- [ ] Default tenant configured in Gateway
- [ ] Application starts without errors
- [ ] Liquibase migrations run successfully
- [ ] Database tables created in `rms-service` schema
- [ ] OAuth2 authentication works
- [ ] Multi-tenant fallback works for default tenant
