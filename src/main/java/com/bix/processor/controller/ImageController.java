package com.bix.processor.controller;

import com.bix.processor.controller.domain.ProcessImageRequest;
import com.bix.processor.domain.Image;
import com.bix.processor.domain.SubscriptionPlan;
import com.bix.processor.domain.User;
import com.bix.processor.exception.ResourceNotFoundException;
import com.bix.processor.repository.ImageRepository;
import com.bix.processor.service.ImageProcessorService;
import com.bix.processor.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageProcessorService imageProcessorService;
    private final ImageRepository imageRepository;
    private final UserService userService;

    @PostMapping("/{imageId}/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> processImage(
            @PathVariable Long imageId,
            @RequestBody ProcessImageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Verify image belongs to user
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));


        User user = userService.findByName(userDetails.getUsername());
        validateUserForProcessing(user, image);

        if (!image.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to process this image");
        }

        // Verify user's subscription plan allows these operations
        if (user.getSubscriptionPlan() != SubscriptionPlan.PREMIUM) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Your subscription plan doesn't support these operations");
        }

        imageProcessorService.processImage();

//        Map<String, Object> response = new HashMap<>();
//        response.put("jobId", jobId);
//        response.put("message", "Image processing job submitted successfully");

        return ResponseEntity.ok(response);
    }

}
