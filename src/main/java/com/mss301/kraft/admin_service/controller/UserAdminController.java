package com.mss301.kraft.admin_service.controller;

import com.mss301.kraft.admin_service.dto.UserAdminDtos.ToggleActiveRequest;
import com.mss301.kraft.admin_service.dto.UserAdminDtos.UserSummary;
import com.mss301.kraft.admin_service.service.UserAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public ResponseEntity<List<UserSummary>> list() {
        return ResponseEntity.ok(userAdminService.listUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSummary> get(@PathVariable UUID id) {
        return ResponseEntity.ok(userAdminService.getUser(id));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<UserSummary> toggleActive(@PathVariable UUID id, @RequestBody ToggleActiveRequest req) {
        return ResponseEntity.ok(userAdminService.toggleActive(id, req));
    }
}
