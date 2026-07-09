package com.goMaddy.multithreaded_http_fileserver.controller;

import com.goMaddy.multithreaded_http_fileserver.dto.AuthenticationResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.LoginRequest;
import com.goMaddy.multithreaded_http_fileserver.dto.LoginResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.UserRegistrationRequest;
import com.goMaddy.multithreaded_http_fileserver.dto.UserResponse;
import com.goMaddy.multithreaded_http_fileserver.service.AuthenticationService;
import com.goMaddy.multithreaded_http_fileserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService= authenticationService;

    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody UserRegistrationRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
   @PostMapping("/login")
public ResponseEntity<AuthenticationResponse> login(
        @RequestBody LoginRequest request) {

    return ResponseEntity.ok(
            authenticationService.login(request)
    );
}
}