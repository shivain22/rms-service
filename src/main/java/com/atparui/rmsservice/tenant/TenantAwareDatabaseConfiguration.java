package com.atparui.rmsservice.tenant;

import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for tenant-aware database connections.
 * Replaces the default ConnectionFactory with a tenant-aware one when multi-tenancy is enabled.
 * The default ConnectionFactory is auto-configured by Spring Boot's R2dbcAutoConfiguration.
 */
@Configuration
@ConditionalOnProperty(prefix = "multi-tenant", name = "enabled", havingValue = "true", matchIfMissing = false)
public class TenantAwareDatabaseConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(TenantAwareDatabaseConfiguration.class);

    private final TenantConnectionFactoryProvider connectionFactoryProvider;
    private final ConnectionFactory defaultConnectionFactory;

    /**
     * Constructor that receives the auto-configured ConnectionFactory from R2dbcAutoConfiguration.
     */
    public TenantAwareDatabaseConfiguration(
        TenantConnectionFactoryProvider connectionFactoryProvider,
        ConnectionFactory connectionFactory
    ) {
        this.connectionFactoryProvider = connectionFactoryProvider;
        this.defaultConnectionFactory = connectionFactory;
        LOG.info("Multi-tenant database configuration enabled");
    }

    /**
     * Provides a tenant-aware ConnectionFactory as the primary bean.
     * This will be used by all R2DBC repositories.
     */
    @Bean
    @Primary
    public ConnectionFactory tenantAwareConnectionFactory() {
        LOG.info("Creating tenant-aware ConnectionFactory");
        return new TenantAwareConnectionFactory(connectionFactoryProvider, defaultConnectionFactory);
    }
}
