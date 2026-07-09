package com.goMaddy.multithreaded_http_fileserver.event;

import java.util.UUID;

public record FileUploadedEvent(UUID fileId) {
}
