package com.mss301.kraft.user_service.controller;

import com.mss301.kraft.user_service.dto.UserProfileResponse;
import com.mss301.kraft.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMe(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUserProfile(authentication));
    }
}
