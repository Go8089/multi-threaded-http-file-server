package com.goMaddy.multithreaded_http_fileserver.service;

import com.goMaddy.multithreaded_http_fileserver.dto.DownloadFileResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileDetailsResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileSummaryResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileUploadResponse;
import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.entity.User;
import com.goMaddy.multithreaded_http_fileserver.event.FileUploadedEvent;
import com.goMaddy.multithreaded_http_fileserver.exception.ResourceNotFoundException;
import com.goMaddy.multithreaded_http_fileserver.repository.FileRepository;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.springframework.context.ApplicationEventPublisher;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final ExecutorService executorService;
    private final ChecksumService checksumService;
    private final ApplicationEventPublisher eventPublisher;
    public FileService(FileRepository fileRepository,
                       FileStorageService fileStorageService, ExecutorService executorService,
                       ChecksumService checksumService, ApplicationEventPublisher eventPublisher) {
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
        this.executorService = executorService;
        this.checksumService = checksumService;
        this.eventPublisher = eventPublisher;

    }
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file) throws IOException {
        System.out.println("[" + Thread.currentThread().getName() + "] Upload started");
        // Save file to disk
        String storedFilename = fileStorageService.saveFile(file);
        // Create metadata
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalFilename(file.getOriginalFilename());
        metadata.setStoredFilename(storedFilename);
        metadata.setContentType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setUploadTime(Instant.now());
        metadata.setUser(getCurrentUser());
        // Save metadata to database
        FileMetadata savedMetadata = fileRepository.save(metadata);
        // Background task
        eventPublisher.publishEvent(
                new FileUploadedEvent(savedMetadata.getId())
        );
        System.out.println("[" + Thread.currentThread().getName() + "] Upload finished");
        return new FileUploadResponse(
                savedMetadata.getId(),
                savedMetadata.getOriginalFilename(),
                savedMetadata.getContentType(),
                savedMetadata.getFileSize(),
                savedMetadata.getUploadTime()
        );
    }
    public FileMetadata getFileMetadata(UUID id) {
        return fileRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found."));
    }
    public DownloadFileResponse downloadFile(UUID id) throws IOException {
       FileMetadata metadata = getUserFile(id);
        Resource resource = fileStorageService.loadFileAsResource(
                metadata.getStoredFilename());
        String contentType = fileStorageService.getContentType(resource);
        long contentLength = resource.contentLength();
        return new DownloadFileResponse(
                resource,
                metadata.getOriginalFilename(),
                contentType,
                contentLength
        );
    }
    @Transactional(readOnly = true)
    public List<FileSummaryResponse> getMyFiles() {
    Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
    User currentUser = (User) authentication.getPrincipal();
    return fileRepository.findByUser(currentUser)
            .stream()
            .map(file -> new FileSummaryResponse(
                    file.getId(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getFileSize(),
                    file.getUploadTime()
            ))
            .toList();
}
    @Transactional
    public void deleteFile(UUID id) throws IOException {
        FileMetadata metadata = getUserFile(id);
        fileStorageService.deleteFile(
                metadata.getStoredFilename());
        fileRepository.delete(metadata);
    }

 /*    @Transactional
    public void deleteAllFiles() throws IOException {
        fileStorageService.deleteAllFiles();
        fileRepository.deleteAll();
    }*/

    public FileDetailsResponse getFileDetails(UUID id) {
        FileMetadata metadata = getUserFile(id);
        return new FileDetailsResponse(
                metadata.getId(),
                metadata.getOriginalFilename(),
                metadata.getContentType(),
                metadata.getFileSize(),
                metadata.getUploadTime(),
                metadata.getChecksum()
        );
    }

    private User getCurrentUser() {
      return (User) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
    }

     private FileMetadata getUserFile(UUID fileId) {
     User currentUser = getCurrentUser();
     FileMetadata file = fileRepository.findById(fileId)
            .orElseThrow(() -> new ResourceNotFoundException("File not found"));
     if (!file.getUser().getId().equals(currentUser.getId())) {
        throw new AccessDeniedException(
                "You are not allowed to access this file.");}
      return file;
    }
     
}
