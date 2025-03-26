package com.bix.processor;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ImageProcessorApplicationTests {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
	}

}
