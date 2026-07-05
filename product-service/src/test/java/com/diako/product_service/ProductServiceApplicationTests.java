package com.diako.product_service;

import com.diako.product_service.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ProductServiceApplicationTests {

	@Test
	void contextLoads() {
		// Hela kedjan — controller, service, repository, Flyway-migrering,
		// entity-mappning — måste knyta ihop korrekt mot en riktig Postgres.
	}
}