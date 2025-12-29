package com.atparui.rmsservice.config;

import com.atparui.rmsservice.tenant.TenantContextHolder;
import com.atparui.rmsservice.tenant.TenantOAuth2ClientService;
import com.atparui.rmsservice.tenant.domain.TenantDatabaseConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Dynamic OAuth2 Client Registration Repository that resolves client registrations
 * based on tenant ID and client type (web/mobile) from the Gateway.
 *
 * This allows the application to use different OAuth2 clients for different tenants
 * and client types dynamically.
 */
@Component
public class DynamicOAuth2ClientRegistrationRepository implements ReactiveClientRegistrationRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicOAuth2ClientRegistrationRepository.class);

    private final TenantOAuth2ClientService tenantOAuth2ClientService;

    // Cache client registrations to avoid recreating them
    private final Cache<String, ClientRegistration> registrationCache = Caffeine.newBuilder()
        .maximumSize(500)
        .expireAfterWrite(Duration.ofHours(1))
        .build();

    // Default registration ID used by Spring Security
    private static final String DEFAULT_REGISTRATION_ID = "oidc";

    public DynamicOAuth2ClientRegistrationRepository(TenantOAuth2ClientService tenantOAuth2ClientService) {
        this.tenantOAuth2ClientService = tenantOAuth2ClientService;
    }

    /**
     * Find client registration by registration ID.
     * In a multi-tenant setup, we need to resolve the registration based on:
     * 1. Tenant ID (from context or request)
     * 2. Client type (web or mobile, from request header or default to web)
     *
     * @param registrationId the registration ID (typically "oidc")
     * @return Mono containing ClientRegistration
     */
    @Override
    public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
        if (!DEFAULT_REGISTRATION_ID.equals(registrationId)) {
            LOG.warn("Unknown registration ID: {}", registrationId);
            return Mono.empty();
        }

        // Get tenant ID from context
        return TenantContextHolder.getCurrentTenantId()
            .flatMap(tenantId ->
                // Determine client type from context or default to "web"
                getClientTypeFromContext()
                    .flatMap(clientType -> {
                        final String finalClientType = clientType;
                        return resolveClientRegistration(tenantId, finalClientType);
                    })
            )
            .switchIfEmpty(
                Mono.defer(() -> {
                    LOG.warn("No tenant ID found in context, cannot resolve OAuth2 client registration");
                    return Mono.empty();
                })
            );
    }

    /**
     * Resolve client registration for a tenant and client type.
     * This method is used when we have explicit tenant and client type information.
     *
     * @param tenantId the tenant ID
     * @param clientType the client type (web or mobile)
     * @return Mono containing ClientRegistration
     */
    public Mono<ClientRegistration> resolveClientRegistration(String tenantId, String clientType) {
        if (tenantId == null || tenantId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Tenant ID cannot be null or blank"));
        }
        final String finalClientType = (clientType == null || clientType.isBlank()) ? "web" : clientType;

        String cacheKey = tenantId + ":" + finalClientType.toLowerCase();

        // Check cache first
        ClientRegistration cached = registrationCache.getIfPresent(cacheKey);
        if (cached != null) {
            LOG.debug("Retrieved client registration from cache for tenant: {}, clientType: {}", tenantId, finalClientType);
            return Mono.just(cached);
        }

        // Fetch OAuth2 client config from gateway
        return tenantOAuth2ClientService
            .getOAuth2ClientConfig(tenantId, finalClientType)
            .flatMap(config -> {
                TenantDatabaseConfig.TenantClientConfig clientConfig = config.getClientByType(finalClientType);

                if (clientConfig == null) {
                    LOG.error("No OAuth2 client configuration found for tenant {} and clientType {}", tenantId, finalClientType);
                    return Mono.error(
                        new IllegalStateException(
                            "No OAuth2 client configuration found for tenant: " + tenantId + ", clientType: " + finalClientType
                        )
                    );
                }

                if (config.getIssuerUri() == null) {
                    LOG.error("No issuer URI found for tenant {}", tenantId);
                    return Mono.error(new IllegalStateException("No issuer URI found for tenant: " + tenantId));
                }

                // Build ClientRegistration
                ClientRegistration registration = buildClientRegistration(
                    tenantId,
                    finalClientType,
                    config.getIssuerUri(),
                    clientConfig.getClientId(),
                    clientConfig.getClientSecret()
                );

                // Cache the registration
                registrationCache.put(cacheKey, registration);
                LOG.info("Created and cached client registration for tenant: {}, clientType: {}", tenantId, finalClientType);

                return Mono.just(registration);
            })
            .doOnError(error -> {
                LOG.error(
                    "Failed to resolve client registration for tenant {} and clientType {}: {}",
                    tenantId,
                    finalClientType,
                    error.getMessage()
                );
            });
    }

    /**
     * Build a ClientRegistration from the provided parameters.
     */
    private ClientRegistration buildClientRegistration(
        String tenantId,
        String clientType,
        String issuerUri,
        String clientId,
        String clientSecret
    ) {
        String registrationId = tenantId + "-" + clientType;

        return ClientRegistration.withRegistrationId(registrationId)
            .issuerUri(issuerUri)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid", "profile", "email", "offline_access")
            .userNameAttributeName("preferred_username")
            .clientName("Tenant: " + tenantId + ", Type: " + clientType)
            .build();
    }

    /**
     * Extract client type from Reactor context.
     * Client type is set by ClientTypeFilter based on X-Client-Type header.
     */
    private Mono<String> getClientTypeFromContext() {
        return Mono.deferContextual(ctx -> {
            String clientType = ctx.getOrDefault("CLIENT_TYPE", "web");
            return Mono.just(clientType);
        });
    }

    /**
     * Invalidate cached client registration for a tenant.
     *
     * @param tenantId the tenant ID
     */
    public void invalidateCache(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return;
        }
        registrationCache.asMap().keySet().removeIf(key -> key.startsWith(tenantId + ":"));
        LOG.debug("Invalidated client registration cache for tenant: {}", tenantId);
    }

    /**
     * Clear all cached client registrations.
     */
    public void clearCache() {
        registrationCache.invalidateAll();
        LOG.debug("Cleared all client registration cache");
    }
}
