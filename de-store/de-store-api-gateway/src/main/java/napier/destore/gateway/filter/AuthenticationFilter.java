package napier.destore.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Custom authentication filter for API Gateway.
 * 
 * In production, this would:
 * - Validate JWT tokens
 * - Check API keys
 * - Verify user permissions
 * - Add user context to downstream requests
 * 
 * For the prototype, this is a placeholder showing where authentication
 * logic would be implemented.
 */
@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            log.debug("Authentication filter - Path: {}, Method: {}", 
                    request.getPath(), request.getMethod());

            // In production, validate authentication here
            // For prototype, we just log and pass through
            if (config.isEnabled()) {
                // Example: Check for API key header
                if (!request.getHeaders().containsKey("X-API-Key")) {
                    log.warn("Missing API key for request to: {}", request.getPath());
                    // In production, would return 401 Unauthorized
                    // For prototype, we allow it through
                }
            }

            // Add custom headers for downstream services
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Gateway-Request-ID", java.util.UUID.randomUUID().toString())
                    .header("X-Gateway-Timestamp", String.valueOf(System.currentTimeMillis()))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .then(Mono.fromRunnable(() -> {
                        ServerHttpResponse response = exchange.getResponse();
                        log.debug("Response status: {}", response.getStatusCode());
                    }));
        };
    }

    public static class Config {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("Authentication error: {}", err);
        return response.setComplete();
    }
}