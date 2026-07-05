package com.diako.product_service.repository;

import com.diako.product_service.config.TestcontainersConfiguration;
import com.diako.product_service.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void savesAndRetrievesProduct() {
        Product saved = productRepository.save(new Product("Desk Lamp", "LED, adjustable", new BigDecimal("29.90")));

        Product found = productRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("Desk Lamp");
        assertThat(found.getPrice()).isEqualByComparingTo("29.90");
        assertThat(found.getCreatedAt()).isNotNull();
    }
}