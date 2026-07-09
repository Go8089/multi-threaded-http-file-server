package com.goMaddy.multithreaded_http_fileserver.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String username, String email, Instant createdAt){}
