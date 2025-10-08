package com.mss301.kraft.media_service.controller;

import com.mss301.kraft.media_service.dto.MediaResponse;
import com.mss301.kraft.media_service.dto.MediaUpdateRequest;
import com.mss301.kraft.media_service.dto.MediaUploadRequest;
import com.mss301.kraft.media_service.enums.MediaType;
import com.mss301.kraft.media_service.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Media management APIs for uploading and managing images")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a single file", description = "Upload a file to Cloudinary")
    public ResponseEntity<MediaResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "mediaType", required = false, defaultValue = "OTHER") MediaType mediaType,
            @RequestParam(value = "entityId", required = false) UUID entityId,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "caption", required = false) String caption,
            @RequestParam(value = "folder", required = false) String folder) {

        MediaUploadRequest request = MediaUploadRequest.builder()
            .mediaType(mediaType)
            .entityId(entityId)
            .entityType(entityType)
            .altText(altText)
            .caption(caption)
            .folder(folder)
            .build();

        MediaResponse response = mediaService.uploadFile(file, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/upload/multiple", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple files", description = "Upload multiple files to Cloudinary")
    public ResponseEntity<List<MediaResponse>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "mediaType", required = false, defaultValue = "OTHER") MediaType mediaType,
            @RequestParam(value = "entityId", required = false) UUID entityId,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "caption", required = false) String caption,
            @RequestParam(value = "folder", required = false) String folder) {

        MediaUploadRequest request = MediaUploadRequest.builder()
            .mediaType(mediaType)
            .entityId(entityId)
            .entityType(entityType)
            .altText(altText)
            .caption(caption)
            .folder(folder)
            .build();

        List<MediaResponse> responses = mediaService.uploadMultipleFiles(files, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get media by ID", description = "Retrieve media information by ID")
    public ResponseEntity<MediaResponse> getMediaById(@PathVariable UUID id) {
        MediaResponse response = mediaService.getMediaById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/entity/{entityId}/{entityType}")
    @Operation(summary = "Get media by entity", description = "Retrieve all media for a specific entity")
    public ResponseEntity<List<MediaResponse>> getMediaByEntity(
            @PathVariable UUID entityId,
            @PathVariable String entityType) {
        List<MediaResponse> responses = mediaService.getMediaByEntity(entityId, entityType);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{mediaType}")
    @Operation(summary = "Get media by type", description = "Retrieve all media of a specific type")
    public ResponseEntity<List<MediaResponse>> getMediaByType(@PathVariable MediaType mediaType) {
        List<MediaResponse> responses = mediaService.getMediaByType(mediaType);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all media", description = "Retrieve all media files")
    public ResponseEntity<List<MediaResponse>> getAllMedia() {
        List<MediaResponse> responses = mediaService.getAllMedia();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update media metadata", description = "Update media information (alt text, caption, etc.)")
    public ResponseEntity<MediaResponse> updateMedia(
            @PathVariable UUID id,
            @RequestBody MediaUpdateRequest request) {
        MediaResponse response = mediaService.updateMedia(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/soft")
    @Operation(summary = "Soft delete media", description = "Mark media as inactive without deleting from Cloudinary")
    public ResponseEntity<Void> softDeleteMedia(@PathVariable UUID id) {
        mediaService.softDeleteMedia(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete media permanently", description = "Delete media from both Cloudinary and database")
    public ResponseEntity<Void> deleteMediaPermanently(@PathVariable UUID id) {
        mediaService.deleteMediaPermanently(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/multiple")
    @Operation(summary = "Delete multiple media", description = "Delete multiple media files permanently")
    public ResponseEntity<Void> deleteMultipleMedia(@RequestBody List<UUID> ids) {
        mediaService.deleteMultipleMedia(ids);
        return ResponseEntity.noContent().build();
    }
}
