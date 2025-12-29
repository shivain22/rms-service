package com.atparui.rmsservice.tenant;

import com.atparui.rmsservice.tenant.domain.TenantDatabaseConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service to fetch and cache OAuth2 client configurations for tenants.
 * This service extends GatewayTenantService to provide OAuth2-specific functionality.
 */
@Service
public class TenantOAuth2ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantOAuth2ClientService.class);

    private final GatewayTenantService gatewayTenantService;
    private final Cache<String, TenantDatabaseConfig> oauth2ConfigCache;

    public TenantOAuth2ClientService(GatewayTenantService gatewayTenantService, MultiTenantProperties properties) {
        this.gatewayTenantService = gatewayTenantService;
        // Cache OAuth2 configs separately with same TTL as database configs
        this.oauth2ConfigCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(properties.getConnection().getCacheTtl()))
            .maximumSize(1000)
            .build();
    }

    /**
     * Get OAuth2 client configuration for a tenant and client type.
     *
     * @param tenantId the tenant ID
     * @param clientType the client type (web or mobile)
     * @return Mono containing TenantDatabaseConfig with OAuth2 client info
     */
    public Mono<TenantDatabaseConfig> getOAuth2ClientConfig(String tenantId, String clientType) {
        if (tenantId == null || tenantId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Tenant ID cannot be null or blank"));
        }
        if (clientType == null || clientType.isBlank()) {
            return Mono.error(new IllegalArgumentException("Client type cannot be null or blank"));
        }

        // Create cache key
        String cacheKey = tenantId + ":" + clientType.toLowerCase();

        // Check cache first
        TenantDatabaseConfig cached = oauth2ConfigCache.getIfPresent(cacheKey);
        if (cached != null) {
            LOG.debug("Retrieved OAuth2 client config from cache for tenant: {}, clientType: {}", tenantId, clientType);
            return Mono.just(cached);
        }

        // Fetch from gateway (this will get the full tenant config)
        return gatewayTenantService
            .getTenantDatabaseConfig(tenantId)
            .doOnNext(config -> {
                // Validate that the config has OAuth2 information
                if (config.getKeycloakBaseUrl() == null || config.getRealmName() == null) {
                    LOG.warn("Tenant config for {} missing OAuth2 realm information", tenantId);
                }
                if (config.getClients() == null || config.getClients().isEmpty()) {
                    LOG.warn("Tenant config for {} has no OAuth2 clients configured", tenantId);
                }
                // Cache the config
                oauth2ConfigCache.put(cacheKey, config);
                LOG.debug("Cached OAuth2 client config for tenant: {}, clientType: {}", tenantId, clientType);
            })
            .doOnError(error -> {
                LOG.error(
                    "Error fetching OAuth2 client config for tenant {} and clientType {}: {}",
                    tenantId,
                    clientType,
                    error.getMessage()
                );
            });
    }

    /**
     * Invalidate cached OAuth2 client configuration for a tenant.
     *
     * @param tenantId the tenant ID
     */
    public void invalidateCache(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return;
        }
        // Invalidate all client types for this tenant
        oauth2ConfigCache.asMap().keySet().removeIf(key -> key.startsWith(tenantId + ":"));
        LOG.debug("Invalidated OAuth2 client config cache for tenant: {}", tenantId);
    }

    /**
     * Clear all cached OAuth2 client configurations.
     */
    public void clearCache() {
        oauth2ConfigCache.invalidateAll();
        LOG.debug("Cleared all OAuth2 client config cache");
    }
}
