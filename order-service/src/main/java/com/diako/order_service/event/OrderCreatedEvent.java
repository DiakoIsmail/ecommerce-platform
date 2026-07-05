package com.diako.order_service.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        BigDecimal totalPrice
) {}