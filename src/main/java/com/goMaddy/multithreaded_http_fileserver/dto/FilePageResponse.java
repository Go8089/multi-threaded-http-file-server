package com.goMaddy.multithreaded_http_fileserver.dto;

import java.util.List;

public record FilePageResponse(

        List<FileSummaryResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages,

        boolean first,

        boolean last

) {}
