package com.bix.processor.service;

import com.bix.processor.domain.Image;
import com.bix.processor.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageProcessorService {

    private final RabbitTemplate rabbitTemplate;
    private final ImageRepository imageRepository;

    public void processImage(Long imageId) {
        // Add image to processing queue
        rabbitTemplate.convertAndSend("imageExchange", "image.processing.start", imageId);
    }

    @RabbitListener(queues = "imageProcessingQueue")
    public void processImageAsync(Long imageId) {
        // Retrieve image from database
        Optional<Image> imageOptional = imageRepository.findById(imageId);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            // Apply grayscale and resize processing here
            // Update image status and save
        }
    }
}