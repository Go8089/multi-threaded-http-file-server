package com.goMaddy.multithreaded_http_fileserver.repository;

import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileMetadata, UUID>, JpaSpecificationExecutor<FileMetadata>  {
    List<FileMetadata> findByUser(User user);
    Page<FileMetadata> findByUser(User user, Pageable pageable);
    Page<FileMetadata> findByUserAndOriginalFilenameContainingIgnoreCase(
        User user,
        String originalFilename,
        Pageable pageable);
    Page<FileMetadata> findByUserAndContentType(
        User user,
        String contentType,
        Pageable pageable);
}