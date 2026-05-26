package com.example.rentflat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Value("${upload.base-url:http://192.168.0.106:8080}")
    private String baseUrl;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        String original  = file.getOriginalFilename();
        String extension = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.'))
                : ".jpg";
        String filename = UUID.randomUUID() + extension;

        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);
        file.transferTo(dir.resolve(filename));

        String url = baseUrl + "/uploads/" + filename;
        log.info("Uploaded file: {}", url);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
