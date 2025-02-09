package com.example.text_drive.service;

import com.example.text_drive.model.User;
import com.example.text_drive.model.Role;
import com.example.text_drive.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SystemUserService {

    private final UserRepository userRepository;

    public SystemUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a system user with the provided password if it does not exist in the database.
     *
     * @param fallbackPassword The password to set for the system user if it needs to be created.
     */
    public void createSystemUserIfNotExists(String fallbackPassword) {
        // Check if a user with the username "systemUser" already exists
        Optional<User> existingUser = userRepository.findByUsername("systemUser");

        if (existingUser.isEmpty()) {
            User systemUser = new User();
            systemUser.setUsername("systemUser");
            systemUser.setPassword(new BCryptPasswordEncoder().encode(fallbackPassword));  // Encrypt the password
            systemUser.setRole(Role.ROLE_ADMIN);  // For now the only Admin
            userRepository.save(systemUser);
            System.out.println("System user 'systemUser' created successfully, and has role: " + systemUser.getRole().toString());
        }
    }
}