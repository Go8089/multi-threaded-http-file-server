package com.goMaddy.multithreaded_http_fileserver.storage;

import com.goMaddy.multithreaded_http_fileserver.exception.FileStorageException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;
import java.util.stream.Stream;

@Service
public class FileStorageService {
    private final Path storageLocation = Paths.get("uploads");

    public FileStorageService() throws IOException {
        Files.createDirectories(storageLocation);
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new FileStorageException("File is empty.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new FileStorageException("Invalid filename.");
        }
        if (originalFilename.contains("..")) {
            throw new FileStorageException("Invalid filename.");
        }
        String storedFilename = UUID.randomUUID() + "_" + originalFilename;
        Path targetLocation = storageLocation.resolve(storedFilename);
        Files.copy(
                file.getInputStream(),
                targetLocation,
                StandardCopyOption.REPLACE_EXISTING
        );
        return storedFilename;
    }
    public Resource loadFile(String storedFilename) throws MalformedURLException {
        Path filePath = storageLocation.resolve(storedFilename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        throw new RuntimeException("File not found.");
    }
    public void deleteFile(String storedFilename) throws IOException {
        Path filePath = storageLocation.resolve(storedFilename).normalize();
        Files.deleteIfExists(filePath);
    }
    public Resource loadFileAsResource(String storedFilename)
            throws MalformedURLException {
        Path filePath = storageLocation.resolve(storedFilename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        throw new FileStorageException("File not found.");
    }
    public String getContentType(Resource resource) throws IOException {
        Path path = resource.getFile().toPath();
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }
    public void deleteAllFiles() throws IOException {
        try (Stream<Path> files = Files.list(storageLocation)) {
            files.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
