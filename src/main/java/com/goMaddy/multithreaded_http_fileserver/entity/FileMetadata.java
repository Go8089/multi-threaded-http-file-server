package com.goMaddy.multithreaded_http_fileserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "filess")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private Long fileSize;
    private Instant uploadTime;
    private String checksum;

    public FileMetadata() {
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public User getUser() {
    return user;
    }
    public void setUser(User user) {
    this.user = user;
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
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
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
    public String getChecksum() {
        return checksum;
    }
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
