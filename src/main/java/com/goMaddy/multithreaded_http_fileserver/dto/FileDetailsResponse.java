package com.goMaddy.multithreaded_http_fileserver.dto;

import java.time.Instant;
import java.util.UUID;

public record FileDetailsResponse(UUID id, String originalFilename, String contentType, Long fileSize,
                                  Instant uploadTime, String downloadUrl,String checksum){}
