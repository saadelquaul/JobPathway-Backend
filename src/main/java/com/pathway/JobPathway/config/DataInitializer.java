package com.pathway.JobPathway.config;

import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.entity.enums.Role;
import com.pathway.JobPathway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@jobpathway.com")) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@jobpathway.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Default admin created: admin@jobpathway.com / admin123");
        } else {
            log.info("Default admin already exists, skipping initialization");
        }
    }
}
