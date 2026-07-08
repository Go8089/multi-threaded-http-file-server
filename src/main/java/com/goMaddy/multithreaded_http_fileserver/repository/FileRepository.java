package com.goMaddy.multithreaded_http_fileserver.repository;

import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileMetadata, UUID> {
}