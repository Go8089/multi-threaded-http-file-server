package com.goMaddy.multithreaded_http_fileserver.service;

import com.goMaddy.multithreaded_http_fileserver.dto.DownloadFileResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileDetailsResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FilePageResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileSummaryResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileUploadResponse;
import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.entity.User;
import com.goMaddy.multithreaded_http_fileserver.event.FileUploadedEvent;
import com.goMaddy.multithreaded_http_fileserver.exception.ResourceNotFoundException;
import com.goMaddy.multithreaded_http_fileserver.repository.FileRepository;
import com.goMaddy.multithreaded_http_fileserver.specification.FileSpecification;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.springframework.context.ApplicationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileService {
        private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "uploadTime",
        "originalFilename",
        "fileSize",
        "contentType");
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final ExecutorService executorService;
    private final ChecksumService checksumService;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
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
        logger.info("[{}] Upload started: {}", Thread.currentThread().getName(), file.getOriginalFilename());
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
        eventPublisher.publishEvent(new FileUploadedEvent(savedMetadata.getId()));
        logger.info("Background checksum event published for file {}", savedMetadata.getId());
       logger.info("[{}] Upload completed: {}", Thread.currentThread().getName(), savedMetadata.getOriginalFilename());
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
        logger.info( "Downloading file {}", metadata.getOriginalFilename());
        return new DownloadFileResponse(
                resource,
                metadata.getOriginalFilename(),
                contentType,
                contentLength
        );
    }
   @Transactional(readOnly = true)
    public FilePageResponse getMyFiles( int page, int size, String sortBy, String direction,  String filename, String contentType) {
      logger.info("Loading file list from PostgreSQL");
        User currentUser = getCurrentUser();
     if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
        throw new IllegalArgumentException(
                "Invalid sort field: " + sortBy
        );
    }
    Sort.Direction sortDirection =
    direction.equalsIgnoreCase("asc")
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(
    page,
    size,
    Sort.by(sortDirection, sortBy)
);
   Specification<FileMetadata> specification =
        FileSpecification.hasUser(currentUser);

if (filename != null && !filename.isBlank()) {

    specification = specification.and(
            FileSpecification.filenameContains(filename)
    );

}

if (contentType != null && !contentType.isBlank()) {

    specification = specification.and(
            FileSpecification.hasContentType(contentType)
    );
}
Page<FileMetadata> files =
        fileRepository.findAll(
                specification,
                pageable
        );
List<FileSummaryResponse> response =

        files.getContent()
                .stream()
                .map(file -> new FileSummaryResponse(
                        file.getId(),
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getFileSize(),
                        file.getUploadTime(),
                        "/api/files/" + file.getId() + "/download"
                ))
                .toList();

return new FilePageResponse(

        response,

        files.getNumber(),

        files.getSize(),

        files.getTotalElements(),

        files.getTotalPages(),

        files.isFirst(),

        files.isLast()

);
}
    @Transactional
    public void deleteFile(UUID id) throws IOException {
        FileMetadata metadata = getUserFile(id);
        logger.info("Deleting file {}",metadata.getOriginalFilename());
        fileStorageService.deleteFile(
                metadata.getStoredFilename());
        fileRepository.delete(metadata);
    }

 /*    @Transactional
    public void deleteAllFiles() throws IOException {
        fileStorageService.deleteAllFiles();
        fileRepository.deleteAll();
        logger.warn(
    "Deleting all files uploaded by user '{}'",
    getCurrentUser().getEmail()
);
    }*/

@Transactional(readOnly = true)
public FileDetailsResponse getFileDetails(UUID id) {

    logger.info("Loading file {} from PostgreSQL", id);

    FileMetadata metadata = getUserFile(id);

    return new FileDetailsResponse(
            metadata.getId(),
            metadata.getOriginalFilename(),
            metadata.getContentType(),
            metadata.getFileSize(),
            metadata.getUploadTime(),
            "/api/files/" + metadata.getId() + "/download",
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
    public static Specification<FileMetadata> hasChecksum(String checksum){
    return (root, query, cb) ->  cb.equal(root.get("checksum"), checksum); }
     
}
