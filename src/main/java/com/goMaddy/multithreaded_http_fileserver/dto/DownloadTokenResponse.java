package com.goMaddy.multithreaded_http_fileserver.dto;

public record DownloadTokenResponse(

        String downloadToken,

        long expiresIn

) {}
