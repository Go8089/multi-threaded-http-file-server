package com.goMaddy.multithreaded_http_fileserver.service;

import com.goMaddy.multithreaded_http_fileserver.dto.DownloadFileResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileUploadResponse;
import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.exception.ResourceNotFoundException;
import com.goMaddy.multithreaded_http_fileserver.repository.FileRepository;
import com.goMaddy.multithreaded_http_fileserver.storage.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    public FileService(FileRepository fileRepository,
                       FileStorageService fileStorageService) {
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
    }
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file) throws IOException {
        String storedFilename = fileStorageService.saveFile(file);
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalFilename(file.getOriginalFilename());
        metadata.setStoredFilename(storedFilename);
        metadata.setContentType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setUploadTime(Instant.now());
        // Save entity in database
        FileMetadata savedMetadata = fileRepository.save(metadata);
        // Convert Entity -> DTO
        FileUploadResponse response = new FileUploadResponse();
        response.setId(savedMetadata.getId());
        response.setOriginalFilename(savedMetadata.getOriginalFilename());
        response.setStoredFilename(savedMetadata.getStoredFilename());
        response.setFileSize(savedMetadata.getFileSize());
        response.setUploadTime(savedMetadata.getUploadTime());
        return response;
    }
    public FileMetadata getFileMetadata(UUID id) {
        return fileRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found."));
    }
    public DownloadFileResponse downloadFile(UUID id) throws IOException {
        FileMetadata metadata = getFileMetadata(id);
        Resource resource = fileStorageService.loadFileAsResource(
                metadata.getStoredFilename());
        String contentType =
                fileStorageService.getContentType(resource);
        long contentLength = resource.contentLength();
        return new DownloadFileResponse(
                resource,
                metadata.getOriginalFilename(),
                contentType,
                contentLength
        );
    }
    public List<FileResponse> getAllFiles() {
        return fileRepository.findAll()
                .stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getOriginalFilename(),
                        file.getFileSize(),
                        file.getUploadTime()
                ))
                .toList();
    }
    @Transactional
    public void deleteFile(UUID id) throws IOException {
        FileMetadata metadata = getFileMetadata(id);
        fileStorageService.deleteFile(
                metadata.getStoredFilename());
        fileRepository.delete(metadata);
    }
    @Transactional
    public void deleteAllFiles() throws IOException {
        fileStorageService.deleteAllFiles();
        fileRepository.deleteAll();
    }
}
