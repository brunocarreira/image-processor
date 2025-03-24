package com.bix.processor.service;

import com.bix.processor.domain.Image;
import com.bix.processor.domain.User;
import com.bix.processor.exception.ResourceNotFoundException;
import com.bix.processor.message.ImageProcessingMessage;
import com.bix.processor.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.Image.SCALE_SMOOTH;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessorService {

    private final RabbitTemplate rabbitTemplate;
    private final ImageRepository imageRepository;
    private final EmailService emailService;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public void processImage(Long imageId, Long userId, java.util.List<String> operations) {
        // Create and send message to RabbitMQ
        ImageProcessingMessage message = new ImageProcessingMessage();
        message.setImageId(imageId);
        message.setUserId(userId);
        message.setOperations(operations);
        // Add image to processing queue
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    @RabbitListener(queues = "${rabbitmq.queue.image-processing}")
    public void processImageAsync(ImageProcessingMessage message) {
        try {
            Image originalImage = imageRepository.findById(message.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

            for (String operation : message.getOperations()) {
                switch (operation) {
                    case "GRAYSCALE":
                        applyGrayscaleFilter(originalImage);
                        break;
                    case "RESIZE":
                        resizeImage(originalImage);
                        break;
                    // Other operations
                }
            }
            String emailText = "Image " +
                    originalImage.getFileName() +
                    " " +
                    message.getOperations() +
                    " applied!";

            // Send email notification
            User user = originalImage.getUser();
            emailService.sendEmail(user.getEmail(), "Image Processed", emailText);
        } catch (Exception e) {
            // Log error
            log.error("Error processing image id {}", message.getImageId(), e);
        }
    }

    private void applyGrayscaleFilter(Image originalImage) {
        try {
            // Load the image
            BufferedImage bufferedImage = ImageIO.read(new File(originalImage.getFilePath()));

            // Convert to grayscale
            BufferedImage grayImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_BYTE_GRAY);

            Graphics g = grayImage.getGraphics();
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();

            ImageIO.write(grayImage, "jpg", new File(originalImage.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to apply grayscale filter", e);
        }
    }

    private void resizeImage(Image originalImage) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(originalImage.getFilePath()));

            // Calculate new dimensions (e.g., half the size)
            int newWidth = bufferedImage.getWidth() / 2;
            int newHeight = bufferedImage.getHeight() / 2;


            java.awt.Image scaledImage = bufferedImage.getScaledInstance(newWidth, newHeight, SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

            resizedImage.getGraphics().drawImage(scaledImage, 0, 0, null);
            ImageIO.write(resizedImage, "jpg", new File(originalImage.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to resize image", e);
        }
    }

}