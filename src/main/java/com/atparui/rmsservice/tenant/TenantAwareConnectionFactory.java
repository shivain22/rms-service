package com.atparui.rmsservice.tenant;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Tenant-aware ConnectionFactory that delegates to the appropriate tenant-specific ConnectionFactory
 * based on the current tenant context.
 */
public class TenantAwareConnectionFactory implements ConnectionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TenantAwareConnectionFactory.class);

    private final TenantConnectionFactoryProvider connectionFactoryProvider;
    private final ConnectionFactory defaultConnectionFactory;

    public TenantAwareConnectionFactory(
        TenantConnectionFactoryProvider connectionFactoryProvider,
        ConnectionFactory defaultConnectionFactory
    ) {
        this.connectionFactoryProvider = connectionFactoryProvider;
        this.defaultConnectionFactory = defaultConnectionFactory;
    }

    @Override
    public Publisher<? extends Connection> create() {
        return Mono.from(connectionFactoryProvider.getConnectionFactory())
            .switchIfEmpty(Mono.just(defaultConnectionFactory))
            .flatMap(factory -> {
                LOG.debug("Creating connection using tenant-specific factory");
                return Mono.from(factory.create());
            })
            .onErrorResume(error -> {
                LOG.error("Error creating tenant-specific connection, falling back to default: {}", error.getMessage());
                return Mono.from(defaultConnectionFactory.create());
            });
    }

    @Override
    public ConnectionFactoryMetadata getMetadata() {
        // Return metadata from default factory as fallback
        return defaultConnectionFactory.getMetadata();
    }
}
