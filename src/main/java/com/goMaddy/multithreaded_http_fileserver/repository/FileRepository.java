package com.goMaddy.multithreaded_http_fileserver.repository;

import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileMetadata, UUID> {
    List<FileMetadata> findByUser(User user);
}