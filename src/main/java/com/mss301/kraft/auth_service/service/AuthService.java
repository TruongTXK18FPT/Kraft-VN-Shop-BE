package com.mss301.kraft.auth_service.service;

import com.mss301.kraft.auth_service.dto.LoginRequest;
import com.mss301.kraft.auth_service.dto.RegisterRequest;
import com.mss301.kraft.auth_service.security.JwtService;
import com.mss301.kraft.common.enums.Role;
import com.mss301.kraft.user_service.entity.Address;
import com.mss301.kraft.user_service.repository.AddressRepository;
import com.mss301.kraft.user_service.entity.User;
import com.mss301.kraft.user_service.entity.UserProfile;
import com.mss301.kraft.user_service.repository.UserProfileRepository;
import com.mss301.kraft.user_service.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AddressRepository addressRepository;

    public AuthService(UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.addressRepository = addressRepository;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()
                && userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone already in use");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        userProfileRepository.save(profile);

        // Create structured default address if provided
        if (request.getLine1() != null || request.getProvince() != null || request.getDistrict() != null
                || request.getWard() != null) {
            Address addr = new Address();
            addr.setUser(user);
            addr.setFullName(request.getFullName() != null ? request.getFullName() : user.getName());
            addr.setPhone(user.getPhone());
            addr.setProvince(request.getProvince());
            addr.setDistrict(request.getDistrict());
            addr.setWard(request.getWard());
            addr.setLine1(request.getLine1());
            addr.setDefaultAddress(true);
            addressRepository.save(addr);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId().toString());
        claims.put("email", user.getEmail());
        return jwtService.generateToken(user.getEmail(), claims);
    }

    public Map<String, String> login(LoginRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId().toString());
        claims.put("email", user.getEmail());
        String accessToken = jwtService.generateToken(user.getEmail(), claims);
        String refreshToken = null; // refresh disabled
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        if (refreshToken != null) {
            tokens.put("refreshToken", refreshToken);
        }
        return tokens;
    }

    public void logout() {
        // Stateless JWT: client drops token; if we track refresh tokens per session,
        // we'd revoke them here based on context (e.g., from cookie/session).
    }

    // refresh removed
}
