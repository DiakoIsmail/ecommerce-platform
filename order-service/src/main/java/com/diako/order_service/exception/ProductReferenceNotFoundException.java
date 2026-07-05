package com.diako.order_service.exception;

public class ProductReferenceNotFoundException extends RuntimeException {
    public ProductReferenceNotFoundException(Long productId) {
        super("Product not found: " + productId);
    }
}