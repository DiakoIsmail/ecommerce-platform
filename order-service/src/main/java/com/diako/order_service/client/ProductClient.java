package com.diako.order_service.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ProductClient {

    @GetExchange("/api/products/{id}")
    ProductClientResponse getProduct(@PathVariable Long id);
}