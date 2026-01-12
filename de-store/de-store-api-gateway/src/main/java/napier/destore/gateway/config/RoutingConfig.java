package napier.destore.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RoutingConfig {


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Health check route
                .route("health-check", r -> r
                        .path("/health")
                        .filters(f -> f
                                .setStatus(200)
                                .addResponseHeader("X-Gateway", "DE-Store-API-Gateway"))
                        .uri("no://op"))
                
                // Documentation aggregation route
                .route("swagger-ui", r -> r
                        .path("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                        .filters(f -> f
                                .addResponseHeader("X-Gateway-Docs", "Aggregated"))
                        .uri("http://localhost:8081"))
                
                .build();
    }
}