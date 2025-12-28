package com.atparui.rmsservice.tenant;

import reactor.util.context.Context;

/**
 * Utility class for managing tenant context in reactive streams.
 * Uses Reactor Context to propagate tenant ID through the reactive chain.
 */
public final class TenantContextHolder {

    private static final String TENANT_ID_KEY = "TENANT_ID";

    private TenantContextHolder() {}

    /**
     * Get the current tenant ID from Reactor Context.
     *
     * @return Mono containing the tenant ID, or empty if not set
     */
    public static reactor.core.publisher.Mono<String> getCurrentTenantId() {
        return reactor.core.publisher.Mono.deferContextual(ctx -> {
            String tenantId = ctx.getOrDefault(TENANT_ID_KEY, null);
            return tenantId != null ? reactor.core.publisher.Mono.just(tenantId) : reactor.core.publisher.Mono.empty();
        });
    }

    /**
     * Set the tenant ID in Reactor Context.
     *
     * @param tenantId the tenant ID to set
     * @return Context with tenant ID added
     */
    public static Context setTenantId(String tenantId) {
        return Context.of(TENANT_ID_KEY, tenantId);
    }

    /**
     * Get tenant ID from Context directly (for non-reactive code).
     *
     * @param context the Reactor Context
     * @return the tenant ID or null if not present
     */
    public static String getTenantIdFromContext(Context context) {
        return context.getOrDefault(TENANT_ID_KEY, null);
    }
}
