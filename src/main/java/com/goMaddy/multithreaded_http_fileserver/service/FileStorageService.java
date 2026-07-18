package com.goMaddy.multithreaded_http_fileserver.service;

import com.goMaddy.multithreaded_http_fileserver.config.AwsS3Properties;
import com.goMaddy.multithreaded_http_fileserver.config.StorageProperties;
import com.goMaddy.multithreaded_http_fileserver.exception.FileStorageException;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileStorageService {
    private final S3Client s3Client;
    private final AwsS3Properties properties;
    private final Path storageLocation;

private static final Logger logger =
        LoggerFactory.getLogger(FileStorageService.class);

public FileStorageService(
        S3Client s3Client,
        AwsS3Properties properties,
         StorageProperties storageProperties
) throws IOException {
    this.s3Client = s3Client;
    this.properties = properties;
    this.storageLocation = Paths
            .get(storageProperties.getPath())
            .toAbsolutePath()
            .normalize();
    Files.createDirectories(storageLocation);
}

public String saveFile(MultipartFile file)
        throws IOException {

    if (file.isEmpty()) {
        throw new FileStorageException("File is empty.");
    }

    String originalFilename = file.getOriginalFilename();

    if (originalFilename == null || originalFilename.isBlank()) {
        throw new FileStorageException("Invalid filename.");
    }

    String storedFilename =
            UUID.randomUUID() + "_" + originalFilename;

    PutObjectRequest request =
            PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storedFilename)
                    .contentType(file.getContentType())
                    .build();

    s3Client.putObject(
            request,
            RequestBody.fromInputStream(
                    file.getInputStream(),
                    file.getSize()
            )
    );

    logger.info(
            "Uploaded {} to S3",
            storedFilename
    );

    return storedFilename;
}   
public ResponseInputStream<GetObjectResponse> loadFile(String storedFilename) {

    GetObjectRequest request =
            GetObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storedFilename)
                    .build();

    return s3Client.getObject(request);
}    
    public void deleteFile(String storedFilename) throws IOException {
        Path filePath = storageLocation.resolve(storedFilename).normalize();
        Files.deleteIfExists(filePath);
        logger.info("Deleted file: {}", storedFilename);
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
public String getContentType(String storedFilename) {

    HeadObjectRequest request =
            HeadObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storedFilename)
                    .build();

    HeadObjectResponse response =
            s3Client.headObject(request);

    return response.contentType();
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
        logger.info("All uploaded files deleted.");
    }
    public Path getFilePath(String storedFilename) {

        return storageLocation.resolve(storedFilename).normalize();
    }
}
