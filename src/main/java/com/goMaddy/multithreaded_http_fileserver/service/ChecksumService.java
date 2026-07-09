package com.goMaddy.multithreaded_http_fileserver.service;

import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.exception.FileStorageException;
import com.goMaddy.multithreaded_http_fileserver.repository.FileRepository;
import com.goMaddy.multithreaded_http_fileserver.util.ChecksumUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ChecksumService {

    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    public ChecksumService(FileRepository fileRepository,
                           FileStorageService fileStorageService) {
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public void updateChecksum(UUID fileId) {
        try {
            FileMetadata metadata = fileRepository.findById(fileId)
                    .orElseThrow(() ->
                            new FileStorageException("File not found"));
            Path filePath = fileStorageService.getFilePath(
                    metadata.getStoredFilename());
            String checksum = ChecksumUtil.calculateSHA256(filePath);
            metadata.setChecksum(checksum);
            fileRepository.save(metadata);
            System.out.println("[" + Thread.currentThread().getName() + "] Checksum saved for: "
                    + metadata.getOriginalFilename());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}