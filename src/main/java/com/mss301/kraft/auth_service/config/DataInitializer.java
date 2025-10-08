package com.mss301.kraft.auth_service.config;

import com.mss301.kraft.common.enums.Role;
import com.mss301.kraft.user_service.entity.User;
import com.mss301.kraft.user_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Basic admin user - matches frontend demo
            userRepository.findByEmail("admin@kraft.vn").ifPresentOrElse(
                    existingAdmin -> {
                        // Update existing admin user to ensure correct role
                        existingAdmin.setRole(Role.ADMIN);
                        existingAdmin.setPasswordHash(passwordEncoder.encode("admin123"));
                        userRepository.save(existingAdmin);
                        System.out.println("Updated existing admin user role");
                    },
                    () -> {
                        // Create new admin user
                        User admin = new User();
                        admin.setName("Admin");
                        admin.setEmail("admin@kraft.vn");
                        admin.setPhone("0000000000");
                        admin.setPasswordHash(passwordEncoder.encode("admin123"));
                        admin.setRole(Role.ADMIN);
                        userRepository.save(admin);
                        System.out.println("Created new admin user");
                    });

            // Basic normal user - matches frontend demo
            userRepository.findByEmail("user@example.com").ifPresentOrElse(
                    existingUser -> {
                        // Update existing user to ensure correct role
                        existingUser.setRole(Role.USER);
                        existingUser.setPasswordHash(passwordEncoder.encode("123456"));
                        userRepository.save(existingUser);
                        System.out.println("Updated existing user role");
                    },
                    () -> {
                        // Create new normal user
                        User user = new User();
                        user.setName("User");
                        user.setEmail("user@example.com");
                        user.setPhone("0000000001");
                        user.setPasswordHash(passwordEncoder.encode("123456"));
                        user.setRole(Role.USER);
                        userRepository.save(user);
                        System.out.println("Created new user");
                    });
        };
    }
}
