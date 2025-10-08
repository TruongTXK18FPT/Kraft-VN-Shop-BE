package com.mss301.kraft.user_service.service;

import com.mss301.kraft.user_service.dto.UserProfileResponse;
import com.mss301.kraft.user_service.dto.AddressResponse;
import com.mss301.kraft.user_service.entity.Address;
import com.mss301.kraft.user_service.entity.User;
import com.mss301.kraft.user_service.entity.UserProfile;
import com.mss301.kraft.user_service.repository.UserProfileRepository;
import com.mss301.kraft.user_service.repository.AddressRepository;
import com.mss301.kraft.user_service.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AddressRepository addressRepository;

    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository,
            AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.addressRepository = addressRepository;
    }

    public UserProfileResponse getCurrentUserProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserProfile profile = userProfileRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);
        java.util.List<AddressResponse> addresses = new java.util.ArrayList<>();
        for (Address a : addressRepository.findByUser(user)) {
            addresses.add(new AddressResponse(
                    a.getId(),
                    a.getFullName(),
                    a.getPhone(),
                    a.getProvince(),
                    a.getDistrict(),
                    a.getWard(),
                    a.getLine1(),
                    a.isDefaultAddress()));
        }
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                profile != null ? profile.getAvatarUrl() : null,
                profile != null ? profile.getBio() : null,
                addresses);
    }
}
