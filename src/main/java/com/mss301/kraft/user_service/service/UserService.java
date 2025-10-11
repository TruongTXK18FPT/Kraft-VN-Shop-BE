package com.mss301.kraft.user_service.service;

import com.mss301.kraft.user_service.dto.UserProfileResponse;
import com.mss301.kraft.user_service.dto.AddressResponse;
import com.mss301.kraft.user_service.dto.UpdateProfileRequest;
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
                                user.getRole(),
                                addresses);
        }

        public UserProfileResponse updateProfile(Authentication authentication, UpdateProfileRequest request) {
                String email = authentication.getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // Update user basic info
                user.setName(request.getName());
                if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                        user.setPhone(request.getPhone());
                }
                user = userRepository.save(user);

                // Handle address update
                if (request.getProvince() != null || request.getDistrict() != null ||
                                request.getWard() != null || request.getLine1() != null) {

                        // Find existing default address or create new one
                        Address defaultAddress = addressRepository.findByUser(user).stream()
                                        .filter(Address::isDefaultAddress)
                                        .findFirst()
                                        .orElse(null);

                        if (defaultAddress == null) {
                                // Create new default address
                                defaultAddress = new Address();
                                defaultAddress.setUser(user);
                                defaultAddress.setDefaultAddress(true);
                        }

                        // Update address fields
                        if (request.getProvince() != null) {
                                defaultAddress.setProvince(request.getProvince());
                        }
                        if (request.getDistrict() != null) {
                                defaultAddress.setDistrict(request.getDistrict());
                        }
                        if (request.getWard() != null) {
                                defaultAddress.setWard(request.getWard());
                        }
                        if (request.getLine1() != null) {
                                defaultAddress.setLine1(request.getLine1());
                        }

                        addressRepository.save(defaultAddress);
                }

                // Return updated profile
                return getCurrentUserProfile(authentication);
        }
}
