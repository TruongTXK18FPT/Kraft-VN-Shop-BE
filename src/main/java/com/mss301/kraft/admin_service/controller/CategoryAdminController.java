package com.mss301.kraft.admin_service.controller;

import com.mss301.kraft.admin_service.dto.CategoryRequest;
import com.mss301.kraft.admin_service.dto.CategoryResponse;
import com.mss301.kraft.admin_service.service.CategoryAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Category Admin", description = "Category management APIs")
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<CategoryResponse>> list() {
        return ResponseEntity.ok(categoryAdminService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryAdminService.get(id));
    }

    @PostMapping
    @Operation(summary = "Create new category")
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {
        CategoryResponse response = categoryAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
