package com.goMaddy.multithreaded_http_fileserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank(message = "Display name is required")
        @Size(min = 3, max = 30,
                message = "Display name must be between 3 and 30 characters")
        String displayName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 50,
                message = "Password must be between 8 and 50 characters")
        String password) {}
