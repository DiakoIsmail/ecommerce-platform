package com.diako.order_service;

import com.diako.order_service.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OrderServiceApplicationTests {

	@Test
	void contextLoads() {
		// Bekräftar att hela kedjan — inklusive ProductClient-proxyn — går att
		// koppla ihop korrekt. Proxyn kräver inte att product-service faktiskt
		// körs här, bara att den kan skapas; det riktiga nätverksanropet sker
		// först när en metod på den faktiskt anropas.
	}
}