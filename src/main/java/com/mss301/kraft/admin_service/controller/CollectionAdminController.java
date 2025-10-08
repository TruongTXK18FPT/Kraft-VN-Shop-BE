package com.mss301.kraft.admin_service.controller;

import com.mss301.kraft.admin_service.dto.CollectionRequest;
import com.mss301.kraft.admin_service.dto.CollectionResponse;
import com.mss301.kraft.admin_service.service.CollectionAdminService;
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
@RequestMapping("/api/admin/collections")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Collection Admin", description = "Collection management APIs")
public class CollectionAdminController {

    private final CollectionAdminService collectionAdminService;

    @GetMapping
    @Operation(summary = "Get all collections")
    public ResponseEntity<List<CollectionResponse>> list() {
        return ResponseEntity.ok(collectionAdminService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get collection by ID")
    public ResponseEntity<CollectionResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(collectionAdminService.get(id));
    }

    @PostMapping
    @Operation(summary = "Create new collection")
    public ResponseEntity<CollectionResponse> create(@RequestBody CollectionRequest request) {
        CollectionResponse response = collectionAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update collection")
    public ResponseEntity<CollectionResponse> update(
            @PathVariable UUID id,
            @RequestBody CollectionRequest request) {
        return ResponseEntity.ok(collectionAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete collection")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        collectionAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
