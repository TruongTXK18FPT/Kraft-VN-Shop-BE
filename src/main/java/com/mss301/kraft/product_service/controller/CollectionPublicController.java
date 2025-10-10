package com.mss301.kraft.product_service.controller;

import com.mss301.kraft.product_service.entity.Collection;
import com.mss301.kraft.product_service.repository.CollectionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/collections")
public class CollectionPublicController {

    private final CollectionRepository collectionRepository;

    public CollectionPublicController(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Collection>> list() {
        return ResponseEntity.ok(collectionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collection> get(@PathVariable UUID id) {
        return collectionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Collection> getBySlug(@PathVariable String slug) {
        return collectionRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
