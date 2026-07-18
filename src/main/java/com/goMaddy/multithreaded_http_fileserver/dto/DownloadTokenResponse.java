package com.goMaddy.multithreaded_http_fileserver.dto;

import java.time.Instant;

public record DownloadTokenResponse(

        String downloadToken,

        Instant expiresIn

) {}
