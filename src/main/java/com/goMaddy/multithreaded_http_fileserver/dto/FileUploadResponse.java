package com.goMaddy.multithreaded_http_fileserver.dto;

import java.time.Instant;
import java.util.UUID;

public class FileUploadResponse {
    private UUID id;
    private String originalFilename;
    private String storedFilename;
    private Long fileSize;
    private Instant uploadTime;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getOriginalFilename() {
        return originalFilename;
    }
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    public String getStoredFilename() {
        return storedFilename;
    }
    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }
    public Long getFileSize() {
        return fileSize;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public Instant getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(Instant uploadTime) {
        this.uploadTime = uploadTime;
    }
}
