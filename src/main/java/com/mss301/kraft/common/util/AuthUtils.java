package com.mss301.kraft.common.util;

import com.mss301.kraft.auth_service.security.CustomUserDetails;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class AuthUtils {
    
    /**
     * Get user ID from authentication
     * @param authentication Spring Security authentication
     * @return User ID as UUID
     */
    public static UUID getUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication is null or principal is null");
        }
        
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return UUID.fromString(userDetails.getUserId());
        }
        
        throw new IllegalArgumentException("Authentication principal is not CustomUserDetails");
    }
    
    /**
     * Get user email from authentication
     * @param authentication Spring Security authentication
     * @return User email
     */
    public static String getUserEmail(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication is null or principal is null");
        }
        
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        
        throw new IllegalArgumentException("Authentication principal is not CustomUserDetails");
    }
}
