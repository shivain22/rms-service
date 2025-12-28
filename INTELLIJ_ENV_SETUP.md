# IntelliJ IDEA Environment Variables Setup Guide

This guide provides the environment variables needed to run the RMS Service in IntelliJ IDEA with:

- Gateway running on `localhost:8082`
- Keycloak running on `rmsauth.atparui.com`

## Quick Setup

### Option 1: Copy-Paste Single Line (Recommended for IntelliJ)

Copy this entire line and paste it into IntelliJ's Run Configuration > Environment Variables field:

```
SPRING_PROFILES_ACTIVE=dev,api-docs;KEYCLOAK_BASE_URL=https://rmsauth.atparui.com;SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_OIDC_ISSUER_URI=https://rmsauth.atparui.com/realms/gateway;SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_ID=rms-service;SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_SECRET=<your-rms-service-client-secret>;MULTI_TENANT_GATEWAY_BASE_URL=http://localhost:8082;SPRING_CLOUD_CONSUL_HOST=localhost;SPRING_CLOUD_CONSUL_PORT=8500;SERVER_PORT=8083
```

### Option 2: Individual Variables (Better for readability)

In IntelliJ Run Configuration > Environment Variables, add each variable:

| Variable Name                                                   | Value                                        |
| --------------------------------------------------------------- | -------------------------------------------- |
| `SPRING_PROFILES_ACTIVE`                                        | `dev,api-docs`                               |
| `KEYCLOAK_BASE_URL`                                             | `https://rmsauth.atparui.com`                |
| `SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_OIDC_ISSUER_URI`        | `https://rmsauth.atparui.com/realms/gateway` |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_ID`     | `rms-service`                                |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_SECRET` | `<your-rms-service-client-secret>`           |
| `MULTI_TENANT_GATEWAY_BASE_URL`                                 | `http://localhost:8082`                      |
| `SPRING_CLOUD_CONSUL_HOST`                                      | `localhost`                                  |
| `SPRING_CLOUD_CONSUL_PORT`                                      | `8500`                                       |
| `SERVER_PORT`                                                   | `8083`                                       |

## Step-by-Step Instructions

1. **Open Run Configuration**

   - Go to `Run` > `Edit Configurations...`
   - Select your application configuration (or create a new one)

2. **Add Environment Variables**

   - In the configuration dialog, find the "Environment variables" field
   - Click the folder icon to open the environment variables editor
   - Either:
     - Paste the single-line string from Option 1, OR
     - Add each variable individually from Option 2

3. **Verify Configuration**

   - Ensure the following are set:
     - **Main class**: Your application's main class (e.g., `com.atparui.rmsservice.RmsserviceApp`)
     - **VM options**: (optional) `-Dspring.profiles.active=dev,api-docs`
     - **Working directory**: Your project root

4. **Run/Debug**
   - Click `Run` or `Debug`
   - The service should start on port `8083`
   - It will register with Consul (if running) and be discoverable by the gateway on `localhost:8082`

## Important Notes

### Keycloak Configuration

- **Base URL**: `https://rmsauth.atparui.com`
  - If your Keycloak uses HTTP instead of HTTPS, change accordingly
- **Default Issuer URI**: `https://rmsauth.atparui.com/realms/gateway`
  - This is used as a fallback. The service uses dynamic JWT decoding to validate tokens from any tenant realm.
- **Client ID**: `rms-service`
  - This client must exist in the `gateway` realm in Keycloak
  - The same client will be automatically created in each tenant realm by the gateway app
- **Client Secret**: Get this from Keycloak Admin Console
  1. Go to Keycloak Admin Console → `gateway` realm → Clients → `rms-service`
  2. Copy the client secret from the "Credentials" tab
  3. Set this value in `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_OIDC_CLIENT_SECRET`

### Gateway Configuration

- **Base URL**: `http://localhost:8082`
  - This is where your Spring Cloud Gateway is running
  - The service will use this URL to communicate with the gateway for tenant configuration

### Service Discovery

- **Consul**: Running on `localhost:8500`
  - Ensure Consul is running if you're using service discovery
  - The service will register itself with Consul so the gateway can discover it

### Service Port

- **Default Port**: `8083`
  - The service runs on port 8083 by default
  - The gateway should route requests to this port

## Troubleshooting

### Service Not Appearing in Gateway

1. **Check Consul**: Ensure Consul is running and accessible at `localhost:8500`
2. **Check Service Registration**: Visit `http://localhost:8500/ui` to see if `rmsservice` is registered
3. **Check Gateway Configuration**: Ensure the gateway is configured to discover services from Consul

### Keycloak Authentication Issues

1. **Check Keycloak URL**: Verify `rmsauth.atparui.com` is accessible
2. **Check Gateway Realm**: Ensure the `gateway` realm exists in Keycloak
3. **Check Client Configuration**:
   - Verify the client `rms-service` exists in the `gateway` realm
   - Ensure the client secret matches the value in your environment variables
   - The service uses dynamic JWT decoding, so it can validate tokens from any tenant realm
4. **Check Token Issuer**: If tokens are from tenant realms, ensure the tenant realm exists and has the `rms-service` client

### Port Conflicts

- If port 8083 is in use, change `SERVER_PORT` to an available port
- Update gateway routing configuration if you change the port

## Additional Environment Variables (Optional)

If you need to configure database, Elasticsearch, or Kafka locally, add these:

```
SPRING_R2DBC_URL=r2dbc:postgresql://localhost:5432/rmsservice
SPRING_R2DBC_USERNAME=rmsservice
SPRING_R2DBC_PASSWORD=
SPRING_LIQUIBASE_URL=jdbc:postgresql://localhost:5432/rmsservice
SPRING_LIQUIBASE_USER=rmsservice
SPRING_LIQUIBASE_PASSWORD=
SPRING_ELASTICSEARCH_URIS=http://localhost:9200
SPRING_CLOUD_STREAM_KAFKA_BINDER_BROKERS=localhost:9092
```

## Gateway Endpoint Discovery

Once the service is running:

1. The service will register with Consul (if Consul is running)
2. The gateway on `localhost:8082` should discover the service automatically
3. Check the gateway's admin interface (typically at `http://localhost:8082/actuator/gateway/routes`) to see exposed endpoints
4. API documentation should be available at `http://localhost:8083/v3/api-docs` or through the gateway
