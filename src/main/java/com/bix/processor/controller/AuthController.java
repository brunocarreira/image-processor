package com.bix.processor.controller;

import com.bix.processor.controller.domain.UserRequest;
import com.bix.processor.domain.User;
import com.bix.processor.exception.BadRequestException;
import com.bix.processor.security.JwtAuthenticationResponse;
import com.bix.processor.security.JwtUtil;
import com.bix.processor.security.UserPrincipal;
import com.bix.processor.security.domain.LoginRequest;
import com.bix.processor.service.AuthService;
import com.bix.processor.service.UserService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/auth", produces = "application/json; charset=utf-8")
public class AuthController {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationProvider authenticationProvider;
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationProvider authenticationProvider, AuthService authService, UserService userService, JwtUtil jwtUtil) {
        this.authenticationProvider = authenticationProvider;
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public String signUp(@RequestBody UserRequest userRequest) {
        User created = authService.createUser(userRequest);
        return "User " + created.getName() + " registered successfully";
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
            User user = userService.findByEmail(userPrincipal.getUsername());
            String jwt = jwtUtil.generateToken(user.getEmail(), user.getSubscriptionPlan().name());

            return new JwtAuthenticationResponse(jwt, user);
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error building authentication response.", e);
            throw new BadRequestException("Error.");
        }
    }
}