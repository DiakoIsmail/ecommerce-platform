package com.diako.order_service.client;

import java.math.BigDecimal;

public record ProductClientResponse(Long id, String name, BigDecimal price) {}