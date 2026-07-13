package com.goMaddy.multithreaded_http_fileserver.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.entity.User;

@DataJpaTest
class FileRepositoryTest {
@Autowired
private FileRepository fileRepository;
@Autowired
private UserRepository userRepository;
 @Test
    void contextLoads() {}
@Test
void shouldSaveFile() {

    User user = new User();
    user.setDisplayName("Gopal");
    user.setEmail("gopal@gmail.com");
    user.setPassword("password");

    user = userRepository.save(user);

    FileMetadata file = new FileMetadata();
    file.setUser(user);
    file.setOriginalFilename("resume.pdf");
    file.setStoredFilename("abc123.pdf");
    file.setContentType("application/pdf");
    file.setFileSize(1024L);
    file.setUploadTime(Instant.now());
    file.setChecksum("checksum");

    FileMetadata saved = fileRepository.save(file);

    assertNotNull(saved.getId());
    assertEquals("resume.pdf", saved.getOriginalFilename());
}

@Test
void shouldFindFileById() {

    User user = new User();
    user.setDisplayName("Gopal");
    user.setEmail("gopal@gmail.com");
    user.setPassword("password");

    user = userRepository.save(user);

    FileMetadata file = new FileMetadata();
    file.setUser(user);
    file.setOriginalFilename("notes.pdf");
    file.setStoredFilename("xyz.pdf");
    file.setContentType("application/pdf");
    file.setFileSize(500L);
    file.setUploadTime(Instant.now());

    FileMetadata saved = fileRepository.save(file);

    Optional<FileMetadata> result =
            fileRepository.findById(saved.getId());

    assertTrue(result.isPresent());
    assertEquals(
            "notes.pdf",
            result.get().getOriginalFilename()
    );
 }

}
