package com.atparui.rmsservice.tenant;

import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Provides the appropriate ConnectionFactory based on the current tenant context.
 */
@Component
public class TenantConnectionFactoryProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TenantConnectionFactoryProvider.class);

    private final TenantConnectionManager connectionManager;
    private final MultiTenantProperties properties;

    public TenantConnectionFactoryProvider(TenantConnectionManager connectionManager, MultiTenantProperties properties) {
        this.connectionManager = connectionManager;
        this.properties = properties;
    }

    /**
     * Get ConnectionFactory for the current tenant from Reactor Context.
     *
     * @return Mono containing ConnectionFactory
     */
    public Mono<ConnectionFactory> getConnectionFactory() {
        return TenantContextHolder.getCurrentTenantId()
            .flatMap(tenantId -> {
                LOG.debug("Getting connection factory for tenant: {}", tenantId);
                return connectionManager.getConnectionFactory(tenantId);
            })
            .switchIfEmpty(handleMissingTenant());
    }

    /**
     * Get ConnectionFactory for a specific tenant ID.
     *
     * @param tenantId the tenant ID
     * @return Mono containing ConnectionFactory
     */
    public Mono<ConnectionFactory> getConnectionFactory(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return handleMissingTenant();
        }
        return connectionManager.getConnectionFactory(tenantId);
    }

    private Mono<ConnectionFactory> handleMissingTenant() {
        if (properties.getFallback().isEnabled()) {
            String defaultTenantId = properties.getFallback().getDefaultTenantId();
            LOG.warn("Tenant ID not found in context, using fallback tenant: {}", defaultTenantId);
            return connectionManager.getConnectionFactory(defaultTenantId);
        } else {
            LOG.error("Tenant ID not found in context and fallback is disabled");
            return Mono.error(new IllegalStateException("Tenant ID is required but not found in request context"));
        }
    }
}
