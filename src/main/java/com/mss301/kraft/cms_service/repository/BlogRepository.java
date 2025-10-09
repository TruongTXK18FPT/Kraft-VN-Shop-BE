package com.mss301.kraft.cms_service.repository;

import com.mss301.kraft.cms_service.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogRepository extends JpaRepository<Blog, UUID> {
    
    Optional<Blog> findBySlug(String slug);
    
    List<Blog> findByPublishedTrue();
    
    List<Blog> findByPublishedTrueAndFeaturedTrue();
    
    List<Blog> findByPublishedTrueAndCategory(String category);
    
    List<Blog> findByCategory(String category);
    
    List<Blog> findAllByOrderByCreatedAtDesc();
    
    List<Blog> findByPublishedTrueOrderByCreatedAtDesc();
}
