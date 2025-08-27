package dev.nathanlively;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController {

    @PostMapping("/api/shutdown")
    public ResponseEntity<Void> shutdown() {
        // Use a separate thread to avoid blocking the HTTP response
        new Thread(() -> {
            try {
                Thread.sleep(100); // Give time for response to be sent
                Application.shutdownRequested();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        return ResponseEntity.ok().build();
    }
}