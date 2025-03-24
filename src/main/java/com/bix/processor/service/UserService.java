package com.bix.processor.service;

import com.bix.processor.domain.Image;
import com.bix.processor.domain.SubscriptionPlan;
import com.bix.processor.domain.User;
import com.bix.processor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${app.quota.daily-images}")
    private Integer quotaDailyImages;

    public User findByName(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void validateUserForProcessing(User user, Image image) {
        if (!image.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to process this image");
        }

        // Verify user's subscription plan allows these operations
        if (user.getSubscriptionPlan() != SubscriptionPlan.PREMIUM) {
            if (user.getLastImageProcessed().truncatedTo(ChronoUnit.DAYS)
                    .equals(Instant.now().truncatedTo(ChronoUnit.DAYS))) {
                if (user.getProcessCount() >= quotaDailyImages) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Your subscription plan doesn't support these operations");
                }
            }
        }
    }

}
