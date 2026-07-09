package com.goMaddy.multithreaded_http_fileserver.repository;

import com.goMaddy.multithreaded_http_fileserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByDisplayName(String displayName);
}