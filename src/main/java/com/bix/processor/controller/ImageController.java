package com.bix.processor.controller;

import com.bix.processor.controller.domain.ProcessImageRequest;
import com.bix.processor.domain.Image;
import com.bix.processor.domain.User;
import com.bix.processor.exception.ForbiddenException;
import com.bix.processor.security.UserPrincipal;
import com.bix.processor.service.ImageService;
import com.bix.processor.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image Controller", description = "Image operations")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;

    @PostMapping("/{imageId}/process")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Process Image",
            description = "Process image",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> processImage(
            @PathVariable Long imageId,
            @RequestBody ProcessImageRequest request,
            Authentication authentication) {

        // Verify image belongs to user
        Image image = imageService.findById(imageId);

        User user = this.getUser(authentication);

        userService.validateUserForProcessing(user, image);
        imageService.processImage(imageId, user.getId(), request.getOperations());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Image processing job submitted successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Upload Image",
            description = "Upload image",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         Authentication authentication) {
        Long imageId = imageService.createImage(file, getUser(authentication));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("imageId", imageId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{imageId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Download Image",
            description = "Download image",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) {
        Resource resource = imageService.downloadImage(imageId);

        String contentType = imageService.determineContentType(Objects.requireNonNull(resource.getFilename()));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private User getUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof String email) {
            return userService.findByEmail(email);
        }
        throw new ForbiddenException("Invalid token");
    }
}
