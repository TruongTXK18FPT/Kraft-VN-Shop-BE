package com.mss301.kraft.media_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mss301.kraft.media_service.dto.MediaResponse;
import com.mss301.kraft.media_service.dto.MediaUpdateRequest;
import com.mss301.kraft.media_service.dto.MediaUploadRequest;
import com.mss301.kraft.media_service.entity.CloudinaryMedia;
import com.mss301.kraft.media_service.enums.MediaType;
import com.mss301.kraft.media_service.repository.MediaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final MediaRepository mediaRepository;
    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder.products}")
    private String productsFolder;

    @Value("${cloudinary.folder.avatars}")
    private String avatarsFolder;

    @Value("${cloudinary.folder.categories}")
    private String categoriesFolder;

    @Value("${cloudinary.folder.banners}")
    private String bannersFolder;

    /**
     * Upload a single file to Cloudinary
     */
    @Transactional
    public MediaResponse uploadFile(MultipartFile file, MediaUploadRequest request) {
        try {
            // Validate file
            validateFile(file);
            
            // Determine folder based on media type
            String folder = determineFolder(request.getMediaType(), request.getFolder());

            // Upload to Cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto",
                    "use_filename", true,
                    "unique_filename", true
                ));

            // Create media entity
            CloudinaryMedia media = CloudinaryMedia.builder()
                .url((String) uploadResult.get("secure_url"))
                .publicId((String) uploadResult.get("public_id"))
                .originalFileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .width((Integer) uploadResult.get("width"))
                .height((Integer) uploadResult.get("height"))
                .mediaType(request.getMediaType())
                .folder(folder)
                .altText(request.getAltText())
                .caption(request.getCaption())
                .entityId(request.getEntityId())
                .entityType(request.getEntityType())
                .isActive(true)
                .build();

            media = mediaRepository.save(media);
            log.info("Successfully uploaded file: {} to Cloudinary with public_id: {}", 
                file.getOriginalFilename(), media.getPublicId());

            return convertToResponse(media);

        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    /**
     * Upload multiple files
     */
    @Transactional
    public List<MediaResponse> uploadMultipleFiles(List<MultipartFile> files, MediaUploadRequest request) {
        return files.stream()
            .map(file -> uploadFile(file, request))
            .collect(Collectors.toList());
    }

    /**
     * Get media by ID
     */
    public MediaResponse getMediaById(UUID id) {
        CloudinaryMedia media = mediaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Media not found with id: " + id));
        return convertToResponse(media);
    }

    /**
     * Get all media by entity
     */
    public List<MediaResponse> getMediaByEntity(UUID entityId, String entityType) {
        List<CloudinaryMedia> mediaList = mediaRepository.findByEntityIdAndEntityTypeAndIsActiveTrue(entityId, entityType);
        return mediaList.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get all media by type
     */
    public List<MediaResponse> getMediaByType(MediaType mediaType) {
        List<CloudinaryMedia> mediaList = mediaRepository.findByMediaTypeAndIsActiveTrue(mediaType);
        return mediaList.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Update media metadata
     */
    @Transactional
    public MediaResponse updateMedia(UUID id, MediaUpdateRequest request) {
        CloudinaryMedia media = mediaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Media not found with id: " + id));

        if (request.getAltText() != null) {
            media.setAltText(request.getAltText());
        }
        if (request.getCaption() != null) {
            media.setCaption(request.getCaption());
        }
        if (request.getEntityId() != null) {
            media.setEntityId(request.getEntityId());
        }
        if (request.getEntityType() != null) {
            media.setEntityType(request.getEntityType());
        }

        media = mediaRepository.save(media);
        log.info("Updated media metadata for id: {}", id);
        
        return convertToResponse(media);
    }

    /**
     * Delete media (soft delete)
     */
    @Transactional
    public void softDeleteMedia(UUID id) {
        CloudinaryMedia media = mediaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Media not found with id: " + id));
        
        media.setIsActive(false);
        mediaRepository.save(media);
        log.info("Soft deleted media with id: {}", id);
    }

    /**
     * Delete media permanently from Cloudinary and database
     */
    @Transactional
    public void deleteMediaPermanently(UUID id) {
        CloudinaryMedia media = mediaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Media not found with id: " + id));

        try {
            // Delete from Cloudinary
            cloudinary.uploader().destroy(media.getPublicId(), ObjectUtils.emptyMap());
            log.info("Deleted file from Cloudinary with public_id: {}", media.getPublicId());

            // Delete from database
            mediaRepository.delete(media);
            log.info("Deleted media from database with id: {}", id);

        } catch (IOException e) {
            log.error("Failed to delete file from Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    /**
     * Delete multiple media files
     */
    @Transactional
    public void deleteMultipleMedia(List<UUID> ids) {
        ids.forEach(this::deleteMediaPermanently);
    }

    /**
     * Get all media
     */
    public List<MediaResponse> getAllMedia() {
        List<CloudinaryMedia> mediaList = mediaRepository.findAll();
        return mediaList.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    // Helper methods

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }

        // Validate file size (max 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File không được vượt quá 5MB");
        }

        // Validate content type - only allow image formats
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedImageType(contentType)) {
            throw new IllegalArgumentException(
                "Chỉ chấp nhận file ảnh với định dạng: PNG, JPG, JPEG, WEBP. " +
                "File của bạn có type: " + contentType
            );
        }

        // Validate file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!isAllowedExtension(extension)) {
                throw new IllegalArgumentException(
                    "Chỉ chấp nhận file ảnh với đuôi: .png, .jpg, .jpeg, .webp. " +
                    "File của bạn có đuôi: ." + extension
                );
            }
        }
    }

    private boolean isAllowedImageType(String contentType) {
        return contentType.equals("image/png") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/webp");
    }

    private boolean isAllowedExtension(String extension) {
        return extension.equals("png") ||
               extension.equals("jpg") ||
               extension.equals("jpeg") ||
               extension.equals("webp");
    }

    private String determineFolder(MediaType mediaType, String customFolder) {
        if (customFolder != null && !customFolder.isEmpty()) {
            return customFolder;
        }

        return switch (mediaType) {
            case PRODUCT -> productsFolder;
            case AVATAR -> avatarsFolder;
            case CATEGORY -> categoriesFolder;
            case BANNER -> bannersFolder;
            default -> "kraft/other";
        };
    }

    private MediaResponse convertToResponse(CloudinaryMedia media) {
        return MediaResponse.builder()
            .id(media.getId())
            .url(media.getUrl())
            .publicId(media.getPublicId())
            .originalFileName(media.getOriginalFileName())
            .fileSize(media.getFileSize())
            .contentType(media.getContentType())
            .width(media.getWidth())
            .height(media.getHeight())
            .mediaType(media.getMediaType())
            .folder(media.getFolder())
            .altText(media.getAltText())
            .caption(media.getCaption())
            .entityId(media.getEntityId())
            .entityType(media.getEntityType())
            .isActive(media.getIsActive())
            .createdAt(media.getCreatedAt() != null ? 
                media.getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime() : null)
            .updatedAt(media.getUpdatedAt() != null ? 
                media.getUpdatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime() : null)
            .build();
    }
}
