package com.goMaddy.multithreaded_http_fileserver.event;

import com.goMaddy.multithreaded_http_fileserver.service.ChecksumService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.concurrent.ExecutorService;

@Component
public class FileUploadedEventListener {
    private final ExecutorService executorService;
    private final ChecksumService checksumService;
    public FileUploadedEventListener(ExecutorService executorService, ChecksumService checksumService) {
        this.executorService = executorService;
        this.checksumService = checksumService;
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFileUploaded(FileUploadedEvent event) {
        System.out.println("Transaction committed. Scheduling checksum calculation...");
        executorService.submit(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Processing file: " + event.fileId());
            checksumService.updateChecksum(event.fileId());
        });
    }
}
