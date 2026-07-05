package com.diako.order_service.config;

import com.diako.order_service.client.ProductClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

@Configuration
@ImportHttpServices(group = "product-service", types = ProductClient.class)
public class HttpClientConfig {

    @Bean
    public RestClientHttpServiceGroupConfigurer productServiceGroupConfigurer(
            @Value("${product-service.base-url}") String baseUrl) {
        return groups -> groups.filterByName("product-service")
                .forEachClient((group, builder) -> builder.baseUrl(baseUrl));
    }
}