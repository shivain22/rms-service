package com.atparui.rmsservice.tenant;

import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
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
    private final BeanFactory beanFactory;

    /**
     * Constructor that receives the BeanFactory to look up the auto-configured ConnectionFactory.
     * We need to explicitly find the non-primary ConnectionFactory to avoid circular dependency.
     */
    public TenantAwareDatabaseConfiguration(TenantConnectionFactoryProvider connectionFactoryProvider, BeanFactory beanFactory) {
        this.connectionFactoryProvider = connectionFactoryProvider;
        this.beanFactory = beanFactory;
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

        // Get the auto-configured ConnectionFactory (not the tenant-aware one)
        ConnectionFactory defaultConnectionFactory = getDefaultConnectionFactory();

        return new TenantAwareConnectionFactory(connectionFactoryProvider, defaultConnectionFactory);
    }

    /**
     * Gets the default (auto-configured) ConnectionFactory, excluding the tenant-aware one.
     * This avoids circular dependencies during bean initialization.
     */
    private ConnectionFactory getDefaultConnectionFactory() {
        ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
        String[] beanNames = listableBeanFactory.getBeanNamesForType(ConnectionFactory.class);

        // Find the ConnectionFactory that's not the tenant-aware one
        for (String beanName : beanNames) {
            if (!beanName.equals("tenantAwareConnectionFactory")) {
                try {
                    ConnectionFactory factory = beanFactory.getBean(beanName, ConnectionFactory.class);
                    LOG.debug("Using default ConnectionFactory bean: {}", beanName);
                    return factory;
                } catch (Exception e) {
                    // Bean might be in creation, skip it and try next
                    LOG.debug("Skipping bean {} due to creation state: {}", beanName, e.getMessage());
                    continue;
                }
            }
        }

        throw new IllegalStateException("Could not find default ConnectionFactory. " + "Make sure R2DBC auto-configuration is enabled.");
    }
}
