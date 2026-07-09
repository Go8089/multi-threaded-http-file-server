package com.goMaddy.multithreaded_http_fileserver.controller;

import com.goMaddy.multithreaded_http_fileserver.dto.DownloadFileResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileDetailsResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileSummaryResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileUploadResponse;
import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Thread = " + Thread.currentThread().getName());
        FileUploadResponse savedFile = fileService.uploadFile(file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedFile);
    }
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") UUID id)
            throws IOException {
        DownloadFileResponse response = fileService.downloadFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .contentLength(response.contentLength())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                response.originalFilename() + "\""
                )
                .body(response.resource());
    }
    @GetMapping
    public ResponseEntity<List<FileSummaryResponse>> getMyFiles() {
    return ResponseEntity.ok(fileService.getMyFiles());
}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable("id") UUID id) throws IOException {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
   /*  @DeleteMapping
    public ResponseEntity<Void> deleteAllFiles() throws IOException {
        fileService.deleteAllFiles();
        return ResponseEntity.noContent().build();
    }*/
    @GetMapping("/{id}")
    public ResponseEntity<FileDetailsResponse> getFileDetails(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                fileService.getFileDetails(id)
        );
    }
}
