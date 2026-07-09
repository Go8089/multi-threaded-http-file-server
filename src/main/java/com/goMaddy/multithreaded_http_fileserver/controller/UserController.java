package com.goMaddy.multithreaded_http_fileserver.controller;

import com.goMaddy.multithreaded_http_fileserver.dto.AuthenticationResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.LoginRequest;
import com.goMaddy.multithreaded_http_fileserver.dto.LoginResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.UserRegistrationRequest;
import com.goMaddy.multithreaded_http_fileserver.dto.UserResponse;
import com.goMaddy.multithreaded_http_fileserver.service.AuthenticationService;
import com.goMaddy.multithreaded_http_fileserver.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
@Tag(
    name = "User APIs",
    description = "Registration and authentication endpoints")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService= authenticationService;
    }

@Operation(
        summary = "Register a new user",
        description = "Creates a new user account."
)
@ApiResponse(
        responseCode = "201",
        description = "User registered successfully"
)
@ApiResponse(
        responseCode = "409",
        description = "Email or display name already exists"
)       
@PostMapping("/register")
public ResponseEntity<UserResponse> register(
        @Valid @RequestBody UserRegistrationRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
@Operation(
        summary = "Login user",
        description = "Authenticates the user and returns a JWT token.")
@ApiResponse(
        responseCode = "200",
        description = "Login successful")
@ApiResponse(
        responseCode = "401",
        description = "Invalid credentials"
)   
   @PostMapping("/login")
public ResponseEntity<AuthenticationResponse> login(
        @RequestBody LoginRequest request) {

    return ResponseEntity.ok(
            authenticationService.login(request)
    );
}
}