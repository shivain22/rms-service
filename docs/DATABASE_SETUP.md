# Database Setup Guide

## Overview

This guide explains how to set up the PostgreSQL database for the RMS Service with the `rms-service` schema and user.

## Prerequisites

- PostgreSQL installed and running locally (default port 5432)
- Access to PostgreSQL as a superuser (typically `postgres`)

## Database Setup Steps

### 1. Create Database

Connect to PostgreSQL as a superuser and create the database:

```sql
CREATE DATABASE rmsservice;
```

### 2. Create User/Role

Create the `rms-service` user/role with appropriate permissions:

```sql
-- Create user
CREATE USER "rms-service" WITH PASSWORD 'your_password_here';

-- Grant privileges on database
GRANT ALL PRIVILEGES ON DATABASE rmsservice TO "rms-service";

-- Connect to the database
\c rmsservice

-- Grant schema creation privileges
GRANT CREATE ON DATABASE rmsservice TO "rms-service";
```

### 3. Create Schema

The schema will be created automatically by Liquibase on first startup, but you can also create it manually:

```sql
-- Connect to the database
\c rmsservice

-- Create schema
CREATE SCHEMA IF NOT EXISTS "rms-service";

-- Grant privileges on schema
GRANT ALL PRIVILEGES ON SCHEMA "rms-service" TO "rms-service";

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA "rms-service" GRANT ALL ON TABLES TO "rms-service";
ALTER DEFAULT PRIVILEGES IN SCHEMA "rms-service" GRANT ALL ON SEQUENCES TO "rms-service";
ALTER DEFAULT PRIVILEGES IN SCHEMA "rms-service" GRANT ALL ON FUNCTIONS TO "rms-service";
```

### 4. Verify Setup

Verify the setup by checking the database, schema, and user:

```sql
-- List databases
\l

-- Connect to database
\c rmsservice

-- List schemas
\dn

-- List users
\du
```

## Configuration

### Application Configuration

The application is configured to use:

- **Database**: `rmsservice`
- **Schema**: `rms-service`
- **User**: `rms-service`
- **Password**: Set via `DB_PASSWORD` environment variable or leave empty for local development

### Connection URLs

**R2DBC (Runtime):**

```
r2dbc:postgresql://localhost:5432/rmsservice?options=--search_path%3Drms-service
```

**JDBC (Liquibase):**

```
jdbc:postgresql://localhost:5432/rmsservice?currentSchema=rms-service
```

## Liquibase Migration

Liquibase will automatically:

1. Create the `rms-service` schema if it doesn't exist (when `default-schema` is set)
2. Create all tables and constraints in the schema
3. Run all migrations on application startup

The Liquibase changelog is located at: `src/main/resources/config/liquibase/master.xml`

## Environment Variables

Set the following environment variables (or use the defaults in configuration files):

```bash
# Database password (optional for local development)
DB_PASSWORD=your_password_here

# Or use Spring Boot properties
SPRING_R2DBC_URL=r2dbc:postgresql://localhost:5432/rmsservice?options=--search_path%3Drms-service
SPRING_R2DBC_USERNAME=rms-service
SPRING_R2DBC_PASSWORD=your_password_here
SPRING_LIQUIBASE_URL=jdbc:postgresql://localhost:5432/rmsservice?currentSchema=rms-service
SPRING_LIQUIBASE_USER=rms-service
SPRING_LIQUIBASE_PASSWORD=your_password_here
```

## Troubleshooting

### Schema Not Found

If you get errors about the schema not existing:

1. Ensure the schema is created (manually or by Liquibase)
2. Check that the user has permissions on the schema
3. Verify the connection URL includes the schema parameter

### Permission Denied

If you get permission errors:

1. Ensure the user has been granted appropriate privileges
2. Check that the user owns the schema or has been granted access
3. Verify the password is correct

### Connection Issues

If you cannot connect:

1. Verify PostgreSQL is running: `pg_isready`
2. Check the connection URL format
3. Ensure the database exists
4. Verify firewall settings if connecting remotely

## Multi-Tenant Setup

For multi-tenant scenarios:

- The default tenant uses the `rms-service` schema
- Additional tenants will have their own database configurations fetched from the Gateway
- The fallback mechanism will use the default connection factory if tenant-specific configuration is unavailable
