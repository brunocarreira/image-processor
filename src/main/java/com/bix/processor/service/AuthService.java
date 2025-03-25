package com.bix.processor.service;

import com.bix.processor.controller.domain.UserRequest;
import com.bix.processor.domain.SubscriptionPlan;
import com.bix.processor.domain.User;
import com.bix.processor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRequest userRequest) {
        User user = new User(userRequest);
        String passwordHash = passwordEncoder.encode(userRequest.getPassword());
        user.setPasswordHash(passwordHash);
        user.setSubscriptionPlan(userRequest.getSubscriptionPlan() == null ? SubscriptionPlan.FREE : userRequest.getSubscriptionPlan());
        User created = userRepository.save(user);
        emailService.sendEmail(user.getEmail(), "Welcome!", "Thank you for registering!");
        return created;
    }
}
