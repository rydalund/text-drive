package com.example.text_drive.repository;

import com.example.text_drive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID userId);
    Optional<User> findByOidcId(String oidcId);
}