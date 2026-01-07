package com.atparui.rmsservice.tenant;

import io.r2dbc.spi.ConnectionFactory;
import javax.sql.DataSource;
import reactor.core.publisher.Mono;

/**
 * Unified interface for providing tenant-specific database connections.
 * Supports both R2DBC (ConnectionFactory) and JDBC (DataSource) based on tenant configuration.
 */
public interface TenantConnectionProvider {
    /**
     * Get R2DBC ConnectionFactory for the current tenant.
     *
     * @return Mono containing ConnectionFactory, or empty if tenant uses JDBC
     */
    Mono<ConnectionFactory> getR2dbcConnectionFactory();

    /**
     * Get JDBC DataSource for the current tenant.
     *
     * @return DataSource, or null if tenant uses R2DBC
     */
    DataSource getJdbcDataSource();

    /**
     * Get R2DBC ConnectionFactory for a specific tenant.
     *
     * @param tenantId the tenant ID
     * @return Mono containing ConnectionFactory, or empty if tenant uses JDBC
     */
    Mono<ConnectionFactory> getR2dbcConnectionFactory(String tenantId);

    /**
     * Get JDBC DataSource for a specific tenant.
     *
     * @param tenantId the tenant ID
     * @return DataSource, or null if tenant uses JDBC
     */
    DataSource getJdbcDataSource(String tenantId);

    /**
     * Check if the current tenant uses JDBC.
     *
     * @return true if tenant uses JDBC, false if R2DBC
     */
    boolean isJdbc();

    /**
     * Check if a specific tenant uses JDBC.
     *
     * @param tenantId the tenant ID
     * @return true if tenant uses JDBC, false if R2DBC
     */
    boolean isJdbc(String tenantId);
}
