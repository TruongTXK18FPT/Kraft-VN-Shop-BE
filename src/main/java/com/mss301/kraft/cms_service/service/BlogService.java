package com.mss301.kraft.cms_service.service;

import com.mss301.kraft.cms_service.dto.BlogRequest;
import com.mss301.kraft.cms_service.dto.BlogResponse;
import com.mss301.kraft.cms_service.entity.Blog;
import com.mss301.kraft.cms_service.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    // Admin methods
    @Transactional(readOnly = true)
    public List<BlogResponse> listAll() {
        return blogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public com.mss301.kraft.admin_service.dto.BlogPageResponse listAllPaged(int page, int size) {
        Page<Blog> result = blogRepository.findAll(PageRequest.of(page, size));
        com.mss301.kraft.admin_service.dto.BlogPageResponse resp = new com.mss301.kraft.admin_service.dto.BlogPageResponse();
        resp.setItems(result.getContent().stream().map(this::toResponse).toList());
        resp.setPage(result.getNumber());
        resp.setSize(result.getSize());
        resp.setTotalElements(result.getTotalElements());
        resp.setTotalPages(result.getTotalPages());
        return resp;
    }

    @Transactional(readOnly = true)
    public BlogResponse get(UUID id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found"));
        return toResponse(blog);
    }

    @Transactional
    public BlogResponse create(BlogRequest request) {
        Blog blog = Blog.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .excerpt(request.getExcerpt())
                .content(request.getContent())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .author(request.getAuthor())
                .readTime(request.getReadTime())
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .published(request.getPublished() != null ? request.getPublished() : false)
                .tags(request.getTags())
                .viewCount(0)
                .build();

        blog = blogRepository.save(blog);
        return toResponse(blog);
    }

    @Transactional
    public BlogResponse update(UUID id, BlogRequest request) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found"));

        if (request.getTitle() != null)
            blog.setTitle(request.getTitle());
        if (request.getSlug() != null)
            blog.setSlug(request.getSlug());
        if (request.getExcerpt() != null)
            blog.setExcerpt(request.getExcerpt());
        if (request.getContent() != null)
            blog.setContent(request.getContent());
        if (request.getCategory() != null)
            blog.setCategory(request.getCategory());
        if (request.getImageUrl() != null)
            blog.setImageUrl(request.getImageUrl());
        if (request.getAuthor() != null)
            blog.setAuthor(request.getAuthor());
        if (request.getReadTime() != null)
            blog.setReadTime(request.getReadTime());
        if (request.getFeatured() != null)
            blog.setFeatured(request.getFeatured());
        if (request.getPublished() != null)
            blog.setPublished(request.getPublished());
        if (request.getTags() != null)
            blog.setTags(request.getTags());

        blog = blogRepository.save(blog);
        return toResponse(blog);
    }

    @Transactional
    public void delete(UUID id) {
        blogRepository.deleteById(id);
    }

    // Public methods
    @Transactional(readOnly = true)
    public List<BlogResponse> listPublished() {
        return blogRepository.findByPublishedTrueOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlogResponse> listFeatured() {
        return blogRepository.findByPublishedTrueAndFeaturedTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlogResponse> listByCategory(String category) {
        return blogRepository.findByPublishedTrueAndCategory(category).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BlogResponse getBySlug(String slug) {
        Blog blog = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found"));

        // Increment view count
        blog.setViewCount(blog.getViewCount() + 1);
        blog = blogRepository.save(blog);

        return toResponse(blog);
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return blogRepository.findAll().stream()
                .map(Blog::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private BlogResponse toResponse(Blog blog) {
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .slug(blog.getSlug())
                .excerpt(blog.getExcerpt())
                .content(blog.getContent())
                .category(blog.getCategory())
                .imageUrl(blog.getImageUrl())
                .author(blog.getAuthor())
                .readTime(blog.getReadTime())
                .featured(blog.getFeatured())
                .published(blog.getPublished())
                .tags(blog.getTags())
                .viewCount(blog.getViewCount())
                .createdAt(blog.getCreatedAt() != null
                        ? blog.getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .updatedAt(blog.getUpdatedAt() != null
                        ? blog.getUpdatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .build();
    }
}
