package com.atparui.rmsservice.tenant;

import com.atparui.rmsservice.tenant.domain.TenantDatabaseConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Service to fetch tenant database configuration from Gateway.
 * Implements caching to reduce Gateway API calls.
 */
@Service
public class GatewayTenantService {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayTenantService.class);

    private final WebClient webClient;
    private final MultiTenantProperties properties;
    private final Cache<String, TenantDatabaseConfig> tenantConfigCache;

    public GatewayTenantService(MultiTenantProperties properties) {
        this.properties = properties;

        // Build WebClient for Gateway communication
        this.webClient = WebClient.builder().baseUrl(properties.getGateway().getBaseUrl()).build();

        // Initialize cache with TTL
        this.tenantConfigCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(properties.getConnection().getCacheTtl()))
            .maximumSize(1000)
            .build();
    }

    /**
     * Get tenant database configuration.
     * First checks cache, then fetches from Gateway if not cached.
     *
     * @param tenantId the tenant ID
     * @return Mono containing TenantDatabaseConfig
     */
    public Mono<TenantDatabaseConfig> getTenantDatabaseConfig(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Tenant ID cannot be null or blank"));
        }

        // Check cache first
        TenantDatabaseConfig cached = tenantConfigCache.getIfPresent(tenantId);
        if (cached != null) {
            LOG.debug("Retrieved tenant config from cache for tenant: {}", tenantId);
            return Mono.just(cached);
        }

        // Fetch from Gateway
        return fetchFromGateway(tenantId)
            .doOnNext(config -> {
                // Cache the result
                tenantConfigCache.put(tenantId, config);
                LOG.debug("Cached tenant config for tenant: {}", tenantId);
            })
            .doOnError(error -> {
                if (error instanceof WebClientResponseException) {
                    WebClientResponseException ex = (WebClientResponseException) error;
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        LOG.warn("Tenant not found in Gateway: {}", tenantId);
                    } else {
                        LOG.error("Error fetching tenant config from Gateway for tenant {}: {}", tenantId, ex.getMessage());
                    }
                } else {
                    LOG.error("Unexpected error fetching tenant config for tenant {}: {}", tenantId, error.getMessage());
                }
            });
    }

    private Mono<TenantDatabaseConfig> fetchFromGateway(String tenantId) {
        String endpoint = properties.getGateway().getTenantConfigEndpoint().replace("{tenantId}", tenantId);

        String url = properties.getGateway().getBaseUrl() + endpoint;
        LOG.debug("Fetching tenant config from Gateway: {}", url);

        return webClient
            .get()
            .uri(endpoint)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(TenantDatabaseConfig.class)
            .timeout(Duration.ofMillis(properties.getGateway().getReadTimeout()))
            .doOnSuccess(config -> {
                config.setTenantId(tenantId); // Ensure tenant ID is set
                LOG.info("Successfully fetched tenant config for tenant: {}", tenantId);
            })
            .doOnError(error -> {
                LOG.error("Failed to fetch tenant config from Gateway for tenant {}: {}", tenantId, error.getMessage());
            });
    }

    /**
     * Invalidate cached tenant configuration.
     * Useful when tenant configuration is updated in Gateway.
     *
     * @param tenantId the tenant ID
     */
    public void invalidateCache(String tenantId) {
        tenantConfigCache.invalidate(tenantId);
        LOG.debug("Invalidated cache for tenant: {}", tenantId);
    }

    /**
     * Clear all cached tenant configurations.
     */
    public void clearCache() {
        tenantConfigCache.invalidateAll();
        LOG.debug("Cleared all tenant config cache");
    }
}
