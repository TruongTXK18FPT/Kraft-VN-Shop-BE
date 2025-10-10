package com.mss301.kraft.cms_service.controller;

import com.mss301.kraft.cms_service.dto.BlogRequest;
import com.mss301.kraft.cms_service.dto.BlogResponse;
import com.mss301.kraft.cms_service.service.BlogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/blogs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Blog Admin", description = "Admin blog management endpoints")
public class BlogAdminController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<List<BlogResponse>> list() {
        return ResponseEntity.ok(blogService.listAll());
    }

    @GetMapping("/paged")
    public ResponseEntity<com.mss301.kraft.admin_service.dto.BlogPageResponse> listPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogService.listAllPaged(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(blogService.get(id));
    }

    @PostMapping
    public ResponseEntity<BlogResponse> create(@RequestBody BlogRequest request) {
        return ResponseEntity.ok(blogService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogResponse> update(@PathVariable UUID id, @RequestBody BlogRequest request) {
        return ResponseEntity.ok(blogService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        blogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(blogService.getCategories());
    }
}
