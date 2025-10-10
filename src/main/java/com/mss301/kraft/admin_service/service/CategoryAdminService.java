package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.CategoryRequest;
import com.mss301.kraft.admin_service.dto.CategoryResponse;
import com.mss301.kraft.product_service.entity.Category;
import com.mss301.kraft.product_service.repository.CategoryRepository;
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
public class CategoryAdminService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public com.mss301.kraft.admin_service.dto.CategoryPageResponse listPaged(int page, int size) {
        Page<Category> result = categoryRepository.findAll(PageRequest.of(page, size));
        com.mss301.kraft.admin_service.dto.CategoryPageResponse resp = new com.mss301.kraft.admin_service.dto.CategoryPageResponse();
        resp.setItems(result.getContent().stream().map(this::toResponse).toList());
        resp.setPage(result.getNumber());
        resp.setSize(result.getSize());
        resp.setTotalElements(result.getTotalElements());
        resp.setTotalPages(result.getTotalPages());
        return resp;
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return toResponse(category);
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category category = new Category();
        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        return toResponse(category);
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category = categoryRepository.save(category);
        return toResponse(category);
    }

    @Transactional
    public void delete(UUID id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .build();
    }
}
