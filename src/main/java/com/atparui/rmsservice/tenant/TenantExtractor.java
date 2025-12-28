package com.atparui.rmsservice.tenant;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Utility class to extract tenant information from JWT tokens.
 */
@Component
public class TenantExtractor {

    private static final Logger log = LoggerFactory.getLogger(TenantExtractor.class);

    // Pattern to match tenant realm names: {tenantId}_realm
    private static final Pattern TENANT_REALM_PATTERN = Pattern.compile("/([^/]+)_realm$");

    /**
     * Extract tenant ID from JWT token.
     * The tenant ID can be extracted from:
     * 1. The 'tenant_id' claim (if present)
     * 2. The 'iss' (issuer) claim by parsing the realm name
     *
     * @param jwt the JWT token
     * @return the tenant ID, or null if not found
     */
    public String extractTenantId(Jwt jwt) {
        // First, try to get tenant_id from claims
        if (jwt.hasClaim("tenant_id")) {
            String tenantId = jwt.getClaimAsString("tenant_id");
            if (tenantId != null && !tenantId.isEmpty()) {
                log.debug("Extracted tenant_id from claim: {}", tenantId);
                return tenantId;
            }
        }

        // Fallback: extract from issuer URI
        String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : null;
        if (issuer != null) {
            String tenantId = extractTenantIdFromIssuer(issuer);
            if (tenantId != null) {
                log.debug("Extracted tenant_id from issuer: {} -> {}", issuer, tenantId);
                return tenantId;
            }
        }

        log.warn("Could not extract tenant_id from JWT token. Issuer: {}", issuer);
        return null;
    }

    /**
     * Extract tenant ID from issuer URI.
     * Expected format: https://keycloak.example.com/realms/{tenantId}_realm
     *
     * @param issuerUri the issuer URI
     * @return the tenant ID, or null if not found
     */
    public String extractTenantIdFromIssuer(String issuerUri) {
        if (issuerUri == null || issuerUri.isEmpty()) {
            return null;
        }

        try {
            URI uri = URI.create(issuerUri);
            String path = uri.getPath();

            // Match pattern: /realms/{tenantId}_realm
            Matcher matcher = TENANT_REALM_PATTERN.matcher(path);
            if (matcher.find()) {
                String tenantId = matcher.group(1);
                log.debug("Extracted tenant_id '{}' from issuer URI: {}", tenantId, issuerUri);
                return tenantId;
            }

            // Also check for gateway realm
            if (path.endsWith("/realms/gateway")) {
                log.debug("Token is from gateway realm");
                return "gateway";
            }
        } catch (Exception e) {
            log.warn("Failed to parse issuer URI: {}", issuerUri, e);
        }

        return null;
    }

    /**
     * Extract realm name from JWT token issuer.
     *
     * @param jwt the JWT token
     * @return the realm name, or null if not found
     */
    public String extractRealmName(Jwt jwt) {
        String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : null;
        if (issuer == null) {
            return null;
        }

        try {
            URI uri = URI.create(issuer);
            String path = uri.getPath();

            // Extract realm name from path: /realms/{realmName}
            if (path.contains("/realms/")) {
                String realmName = path.substring(path.lastIndexOf("/realms/") + 8);
                log.debug("Extracted realm name '{}' from issuer: {}", realmName, issuer);
                return realmName;
            }
        } catch (Exception e) {
            log.warn("Failed to extract realm name from issuer: {}", issuer, e);
        }

        return null;
    }
}
