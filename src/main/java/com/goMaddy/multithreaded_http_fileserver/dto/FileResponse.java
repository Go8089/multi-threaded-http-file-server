package com.goMaddy.multithreaded_http_fileserver.dto;
import java.time.Instant;
import java.util.UUID;

public record FileResponse(
        UUID id,
        String originalFilename,
        Long fileSize,
        Instant uploadTime
){}