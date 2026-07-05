package org.diako.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(
            @Value("${product-service.base-url}") String productServiceUrl,
            @Value("${order-service.base-url}") String orderServiceUrl) {

        RouterFunction<ServerResponse> productRoutes =
                route().GET("/api/products/**", http()).before(uri(productServiceUrl)).build()
                        .and(route().POST("/api/products/**", http()).before(uri(productServiceUrl)).build())
                        .and(route().PUT("/api/products/**", http()).before(uri(productServiceUrl)).build())
                        .and(route().DELETE("/api/products/**", http()).before(uri(productServiceUrl)).build());

        RouterFunction<ServerResponse> orderRoutes =
                route().GET("/api/orders/**", http()).before(uri(orderServiceUrl)).build()
                        .and(route().POST("/api/orders/**", http()).before(uri(orderServiceUrl)).build());

        return productRoutes.and(orderRoutes);
    }
}