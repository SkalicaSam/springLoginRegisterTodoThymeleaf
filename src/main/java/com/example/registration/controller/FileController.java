package com.example.registration.controller;

import com.example.registration.model.File;
import com.example.registration.repository.FileRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {
    // Predpokladám, že máte inštanciu fileRepository a fileStorageLocation
    private final FileRepository fileRepository;
    private final Path fileStorageLocation;

    public FileController(FileRepository fileRepository, Path fileStorageLocation) {
        this.fileRepository = fileRepository;
        this.fileStorageLocation = fileStorageLocation;
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        System.out.println("Requested fileId: " + fileId);

        // Retrieve file from database
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found in database with ID: " + fileId));

        System.out.println("File found in database: " + file.getName());

        // Build the file path
        Path filePath = fileStorageLocation.resolve(file.getName()).normalize();
        System.out.println("Resolved file path: " + filePath.toString());

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found in storage: " + filePath.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("File not found in storage: " + filePath.toString(), e);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
