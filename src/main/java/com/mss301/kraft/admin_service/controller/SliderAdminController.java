package com.mss301.kraft.admin_service.controller;

import com.mss301.kraft.admin_service.dto.SliderDtos.SliderRequest;
import com.mss301.kraft.admin_service.dto.SliderDtos.SliderResponse;
import com.mss301.kraft.admin_service.service.SliderAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/sliders")
@PreAuthorize("hasRole('ADMIN')")
public class SliderAdminController {

    private final SliderAdminService sliderAdminService;

    public SliderAdminController(SliderAdminService sliderAdminService) {
        this.sliderAdminService = sliderAdminService;
    }

    @GetMapping
    public ResponseEntity<List<SliderResponse>> list() {
        return ResponseEntity.ok(sliderAdminService.list());
    }

    @GetMapping("/paged")
    public ResponseEntity<com.mss301.kraft.admin_service.dto.SliderPageResponse> listPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sliderAdminService.listPaged(page, size));
    }

    @GetMapping("/active")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<SliderResponse>> listActive() {
        return ResponseEntity.ok(sliderAdminService.listActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SliderResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(sliderAdminService.get(id));
    }

    @PostMapping
    public ResponseEntity<SliderResponse> create(@RequestBody SliderRequest request) {
        return ResponseEntity.ok(sliderAdminService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SliderResponse> update(@PathVariable UUID id, @RequestBody SliderRequest request) {
        return ResponseEntity.ok(sliderAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        sliderAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
