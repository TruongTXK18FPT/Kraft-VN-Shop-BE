package com.mss301.kraft.common.controller;

import com.mss301.kraft.common.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            // Validate file size (5MB limit)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size must be less than 5MB");
            }

            String fileName = fileStorageService.storeFile(file);
            String fileUrl = fileStorageService.getFileUrl(fileName);

            return ResponseEntity.ok().body(new FileUploadResponse(fileName, fileUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not upload file: " + e.getMessage());
        }
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);

            // Determine file's content type
            String contentType = "application/octet-stream";
            if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileName.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (fileName.toLowerCase().endsWith(".webp")) {
                contentType = "image/webp";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public static class FileUploadResponse {
        private String fileName;
        private String fileUrl;

        public FileUploadResponse(String fileName, String fileUrl) {
            this.fileName = fileName;
            this.fileUrl = fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }
    }
}
