package com.bix.processor.controller;

import com.bix.processor.controller.domain.UserRequest;
import com.bix.processor.domain.User;
import com.bix.processor.exception.BadRequestException;
import com.bix.processor.repository.UserRepository;
import com.bix.processor.security.JwtAuthenticationResponse;
import com.bix.processor.security.JwtUtil;
import com.bix.processor.security.UserPrincipal;
import com.bix.processor.security.domain.LoginRequest;
import com.bix.processor.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth", produces = "application/json; charset=utf-8")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationProvider authenticationProvider;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserRequest userRequest) {
        User created = authService.createUser(userRequest);
        return ResponseEntity.ok("User " + created.getName() + " registered successfully");
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        // Validate user credentials
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        return buildJwtAuthenticationResponse(authentication);
    }

    private JwtAuthenticationResponse buildJwtAuthenticationResponse(Authentication authentication) {
        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findByName(userPrincipal.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            String jwt = jwtUtil.generateToken(user.getName(), user.getSubscriptionPlan().name());

            return new JwtAuthenticationResponse(jwt, user);
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error building authentication response.", e);
            throw new BadRequestException("Error.");
        }
    }
}