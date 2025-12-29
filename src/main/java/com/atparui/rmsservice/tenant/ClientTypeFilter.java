package com.atparui.rmsservice.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter to extract client type (web/mobile) from HTTP header
 * and set it in the Reactor context for downstream processing.
 */
@Component
@Order(-99) // Execute early, after TenantContextFilter
public class ClientTypeFilter implements WebFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientTypeFilter.class);

    private static final String CLIENT_TYPE_HEADER = "X-Client-Type";
    private static final String DEFAULT_CLIENT_TYPE = "web";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Extract client type from header
        String headerValue = exchange.getRequest().getHeaders().getFirst(CLIENT_TYPE_HEADER);

        final String clientType;
        if (headerValue == null || headerValue.isBlank()) {
            clientType = DEFAULT_CLIENT_TYPE;
        } else {
            String normalized = headerValue.toLowerCase().trim();
            // Validate client type
            if (!"web".equals(normalized) && !"mobile".equals(normalized)) {
                LOG.warn("Invalid client type '{}' in header, defaulting to 'web'", normalized);
                clientType = DEFAULT_CLIENT_TYPE;
            } else {
                clientType = normalized;
            }
        }

        LOG.debug("Extracted client type: {}", clientType);

        // Set client type in Reactor Context
        final String finalClientType = clientType;
        return chain.filter(exchange).contextWrite(ctx -> ctx.put("CLIENT_TYPE", finalClientType));
    }
}
