package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.CollectionRequest;
import com.mss301.kraft.admin_service.dto.CollectionResponse;
import com.mss301.kraft.product_service.entity.Collection;
import com.mss301.kraft.product_service.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionAdminService {

    private final CollectionRepository collectionRepository;

    @Transactional(readOnly = true)
    public List<CollectionResponse> list() {
        return collectionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public com.mss301.kraft.admin_service.dto.CollectionPageResponse listPaged(int page, int size) {
        Page<Collection> result = collectionRepository.findAll(PageRequest.of(page, size));
        com.mss301.kraft.admin_service.dto.CollectionPageResponse resp = new com.mss301.kraft.admin_service.dto.CollectionPageResponse();
        resp.setItems(result.getContent().stream().map(this::toResponse).toList());
        resp.setPage(result.getNumber());
        resp.setSize(result.getSize());
        resp.setTotalElements(result.getTotalElements());
        resp.setTotalPages(result.getTotalPages());
        return resp;
    }

    @Transactional(readOnly = true)
    public CollectionResponse get(UUID id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));
        return toResponse(collection);
    }

    @Transactional
    public CollectionResponse create(CollectionRequest request) {
        if (collectionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Collection name already exists");
        }

        Collection collection = new Collection();
        collection.setName(request.getName());
        collection.setDescription(request.getDescription());
        collection.setImageUrl(request.getImageUrl());

        collection = collectionRepository.save(collection);
        return toResponse(collection);
    }

    @Transactional
    public CollectionResponse update(UUID id, CollectionRequest request) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        collection.setName(request.getName());
        collection.setDescription(request.getDescription());
        collection.setImageUrl(request.getImageUrl());

        collection = collectionRepository.save(collection);
        return toResponse(collection);
    }

    @Transactional
    public void delete(UUID id) {
        collectionRepository.deleteById(id);
    }

    private CollectionResponse toResponse(Collection collection) {
        return CollectionResponse.builder()
                .id(collection.getId())
                .name(collection.getName())
                .slug(collection.getSlug())
                .description(collection.getDescription())
                .imageUrl(collection.getImageUrl())
                .productCount(collection.getProducts() != null ? collection.getProducts().size() : 0)
                .build();
    }
}
