package com.bix.processor;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableRabbit
public class ImageProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageProcessorApplication.class, args);
	}

}
