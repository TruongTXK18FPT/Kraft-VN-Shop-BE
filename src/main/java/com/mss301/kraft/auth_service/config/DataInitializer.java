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
            // Basic admin user
            userRepository.findByEmail("admin@example.com").orElseGet(() -> {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@example.com");
                admin.setPhone("0000000000");
                admin.setPasswordHash(passwordEncoder.encode("password"));
                admin.setRole(Role.ADMIN);
                return userRepository.save(admin);
            });

            // Basic normal user
            userRepository.findByEmail("user@example.com").orElseGet(() -> {
                User user = new User();
                user.setName("User");
                user.setEmail("user@example.com");
                user.setPhone("0000000001");
                user.setPasswordHash(passwordEncoder.encode("password"));
                user.setRole(Role.USER);
                return userRepository.save(user);
            });
        };
    }
}
