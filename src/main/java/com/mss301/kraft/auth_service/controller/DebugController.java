package com.mss301.kraft.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class DebugController {

    @GetMapping("/whoami")
    public ResponseEntity<Map<String, Object>> whoAmI(Authentication authentication) {
        Map<String, Object> body = new HashMap<>();
        if (authentication == null) {
            body.put("authenticated", false);
            return ResponseEntity.ok(body);
        }
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        body.put("authenticated", authentication.isAuthenticated());
        body.put("principal", authentication.getName());
        body.put("authorities", authorities);
        return ResponseEntity.ok(body);
    }
}


