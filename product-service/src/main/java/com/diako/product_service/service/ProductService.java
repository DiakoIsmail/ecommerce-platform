package com.diako.product_service.service;

import com.diako.product_service.dto.ProductRequest;
import com.diako.product_service.dto.ProductResponse;
import com.diako.product_service.entity.Product;
import com.diako.product_service.exception.ProductNotFoundException;
import com.diako.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public ProductResponse create(ProductRequest request) {
        Product saved = productRepository.save(
                new Product(request.name(), request.description(), request.price()));
        log.info("Created product id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getOrThrow(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        Product saved = productRepository.save(product);
        log.info("Updated product id={}", saved.getId());
        return toResponse(saved);
    }

    public void delete(Long id) {
        Product product = getOrThrow(id);
        productRepository.delete(product);
        log.info("Deleted product id={}", id);
    }

    private Product getOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }
}