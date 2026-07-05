package com.diako.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@SpringBootTest
@Testcontainers
class NotificationServiceApplicationTests {

	@Container
	@ServiceConnection
	static KafkaContainer kafka = new KafkaContainer("apache/kafka:latest");

	@Test
	void contextLoads() {
	}
}