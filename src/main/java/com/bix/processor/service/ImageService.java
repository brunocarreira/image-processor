package com.bix.processor.service;

import com.bix.processor.domain.Image;
import com.bix.processor.domain.ImageStatus;
import com.bix.processor.domain.ProcessOperation;
import com.bix.processor.domain.User;
import com.bix.processor.exception.ResourceNotFoundException;
import com.bix.processor.message.ImageProcessingMessage;
import com.bix.processor.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static java.awt.Image.SCALE_SMOOTH;

@Service
@Slf4j
public class ImageService {

    private final RabbitTemplate rabbitTemplate;
    private final ImageRepository imageRepository;
    private final EmailService emailService;
    private final Path fileStorageLocation;

    private final String exchange;
    private final String routingKey;
    private final String uploadDir;

    public ImageService(@Value("${file.upload-dir}") String uploadDir,
                        @Value("${rabbitmq.exchange.name}") String exchange,
                        @Value("${rabbitmq.routing.key}") String routingKey,
                        RabbitTemplate rabbitTemplate,
                        ImageRepository imageRepository,
                        EmailService emailService
                        ) {
        try {
            this.exchange = exchange;
            this.routingKey = routingKey;
            this.uploadDir = uploadDir;
            this.rabbitTemplate = rabbitTemplate;
            this.imageRepository = imageRepository;
            this.emailService = emailService;
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Error creating upload path.", ex);
        }
    }

    @Transactional
    public Long createImage(MultipartFile file) {
        try {
            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            Path targetLocation = this.fileStorageLocation.resolve(originalFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Image newImage = Image.builder()
                    .filePath(uploadDir + "/" + originalFileName)
                    .fileName(originalFileName)
                    .status(ImageStatus.PENDING)
                    .build();

            return imageRepository.save(newImage).getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Image findById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
    }

    public Resource downloadImage(Long imageId) {
        try {
            Image image = findById(imageId);

            Path filePath = this.fileStorageLocation.resolve(image.getFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found: " + image.getFileName());
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".jpg") ||
                filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }

    public void processImage(Long imageId, Long userId, java.util.List<ProcessOperation> operations) {
        // Create and send message to RabbitMQ
        ImageProcessingMessage message = new ImageProcessingMessage();
        message.setImageId(imageId);
        message.setUserId(userId);
        message.setOperations(operations);
        // Add image to processing queue
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    @RabbitListener(queues = "${rabbitmq.queue.image-processing}")
    @Transactional
    public void processImageAsync(ImageProcessingMessage message) {
        try {
            Image originalImage = imageRepository.findById(message.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

            for (ProcessOperation operation : message.getOperations()) {
                switch (operation) {
                    case ProcessOperation.GRAYSCALE:
                        applyGrayscaleFilter(originalImage);
                        break;
                    case ProcessOperation.RESIZE:
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

            User user = originalImage.getUser();

            if (user.getLastImageProcessed().truncatedTo(ChronoUnit.DAYS)
                    .equals(Instant.now().truncatedTo(ChronoUnit.DAYS))) {
                user.setProcessCount(user.getProcessCount() + 1);
            } else {
                // reset quota every new day based on UTC
                user.setProcessCount(1);
            }
            user.setLastImageProcessed(Instant.now());
            originalImage.setStatus(ImageStatus.PROCESSED);
            imageRepository.save(originalImage);
            // Send email notification
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