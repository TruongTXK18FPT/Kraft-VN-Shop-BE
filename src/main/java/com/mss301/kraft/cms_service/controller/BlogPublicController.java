package com.mss301.kraft.cms_service.controller;

import com.mss301.kraft.cms_service.dto.BlogResponse;
import com.mss301.kraft.cms_service.service.BlogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Tag(name = "Public Blogs", description = "Public blog endpoints")
public class BlogPublicController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<List<BlogResponse>> listPublished() {
        return ResponseEntity.ok(blogService.listPublished());
    }

    @GetMapping("/featured")
    public ResponseEntity<List<BlogResponse>> listFeatured() {
        return ResponseEntity.ok(blogService.listFeatured());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<BlogResponse>> listByCategory(@PathVariable String category) {
        return ResponseEntity.ok(blogService.listByCategory(category));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<BlogResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(blogService.getBySlug(slug));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(blogService.getCategories());
    }
}
