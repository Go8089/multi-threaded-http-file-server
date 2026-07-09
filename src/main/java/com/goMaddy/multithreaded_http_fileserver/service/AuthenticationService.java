package com.goMaddy.multithreaded_http_fileserver.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goMaddy.multithreaded_http_fileserver.dto.AuthenticationResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.LoginRequest;
import com.goMaddy.multithreaded_http_fileserver.entity.User;
import com.goMaddy.multithreaded_http_fileserver.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final Logger logger =  LoggerFactory.getLogger(AuthenticationService.class);
    public AuthenticationService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse login(LoginRequest request) {
    User user = userService.getUserByEmail(request.email());

if (!passwordEncoder.matches(request.password(), user.getPassword())) {

    logger.warn(
        "Failed login attempt for email '{}'.",
        request.email()
    );

    throw new BadCredentialsException("Invalid email or password");
}

logger.info(
    "User '{}' logged in successfully.",
    user.getEmail()
);

String token = jwtService.generateToken(user);

return new AuthenticationResponse(token);
        
    }
}