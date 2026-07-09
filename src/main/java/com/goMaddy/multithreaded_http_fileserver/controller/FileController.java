package com.goMaddy.multithreaded_http_fileserver.controller;

import com.goMaddy.multithreaded_http_fileserver.dto.DownloadFileResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileDetailsResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileSummaryResponse;
import com.goMaddy.multithreaded_http_fileserver.dto.FileUploadResponse;
import com.goMaddy.multithreaded_http_fileserver.entity.FileMetadata;
import com.goMaddy.multithreaded_http_fileserver.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(
    name = "File APIs",
    description = "Upload, download and manage files")
@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
@Operation(
        summary = "Upload a file",
        description = "Uploads a file belonging to the authenticated user."
)
@ApiResponse(
        responseCode = "201",
        description = "File uploaded successfully"
)    
@PostMapping("/upload")
public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Thread = " + Thread.currentThread().getName());
        FileUploadResponse savedFile = fileService.uploadFile(file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedFile);
    }
@Operation(
        summary = "Download file",
        description = "Downloads a file owned by the authenticated user."
)
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
@Operation(
        summary = "List my files",
        description = "Returns every file uploaded by the authenticated user."
)    
@GetMapping
public ResponseEntity<Page<FileSummaryResponse>> getMyFiles(
        @RequestParam(defaultValue = "0")
        int page,
        @RequestParam(defaultValue = "10")
        int size,
        @RequestParam(defaultValue = "uploadTime")
        String sortBy,
        @RequestParam(defaultValue = "desc")
        String direction,
        @RequestParam(required = false)
        String filename,
        @RequestParam(required = false)
String contentType
     ){
    return ResponseEntity.ok(fileService.getMyFiles(
                    page,size,
                    sortBy, direction,
                    filename,contentType));}
@Operation(
        summary = "Delete file",
        description = "Deletes one file belonging to the authenticated user."
)
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
