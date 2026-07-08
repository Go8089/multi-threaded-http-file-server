package com.goMaddy.multithreaded_http_fileserver.dto;

import org.springframework.core.io.Resource;

public record DownloadFileResponse(
        Resource resource,
        String originalFilename,
        String contentType,
        long contentLength) {}

