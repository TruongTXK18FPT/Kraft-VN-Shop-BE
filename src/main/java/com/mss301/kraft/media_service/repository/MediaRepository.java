package com.mss301.kraft.media_service.repository;

import com.mss301.kraft.media_service.entity.CloudinaryMedia;
import com.mss301.kraft.media_service.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<CloudinaryMedia, UUID> {
    
    List<CloudinaryMedia> findByEntityIdAndEntityTypeAndIsActiveTrue(UUID entityId, String entityType);
    
    List<CloudinaryMedia> findByMediaTypeAndIsActiveTrue(MediaType mediaType);
    
    Optional<CloudinaryMedia> findByPublicId(String publicId);
    
    List<CloudinaryMedia> findByEntityIdAndMediaTypeAndIsActiveTrue(UUID entityId, MediaType mediaType);
    
    void deleteByPublicId(String publicId);
}
