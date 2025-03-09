package com.example.text_drive.service;

import com.example.text_drive.model.Role;
import com.example.text_drive.model.User;
import com.example.text_drive.repository.UserRepository;
import com.example.text_drive.security.JWTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service class for user-related operations.
 * Implements UserDetailsService for Spring Security integration.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with the specified username and password.
     * Validates the user input and checks if the username is available.
     *
     * @param username The desired username for the new user.
     * @param password The password for the new user.
     * @return The created user entity.
     * @throws IllegalArgumentException if the username or password does not meet the required criteria.
     */
    @Transactional
    public User createUser(String username, String password) {
        validateUserInput(username, password);
        checkUsernameAvailability(username);  // Ensure the username is not already taken

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, Role.ROLE_USER);
        return userRepository.save(user);
    }

    /**
     * Creates a new user with OpenID-connect information.
     * It also creates a random password that is saved to the database
     * The idea is to be able to reset it,
     * if you want to log in with a password instead (though there is no functionality for that yet).
     *
     * @param username The username (GitHub username).
     * @param email The email address (if available).
     * @param oidcId The OpenID-connect user ID.
     * @param oidcProvider The OpenID-connect provider (Github).
     * @return The created user entity.
     */
    @Transactional
    public User createOpenIdUser(String username, String email, String oidcId, String oidcProvider) {
        // Use the email address as the username if available, otherwise use the GitHub username.
        String finalUsername = (email != null && !email.isBlank()) ? email : username;

        // Set default provider to "github" if none is provided.
        String finalOidcProvider = (oidcProvider != null && !oidcProvider.isBlank()) ? oidcProvider : "github";

        // Create a new user with a random password.
        User user = new User();
        user.setUsername(finalUsername);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Random password
        user.setRole(Role.ROLE_USER);
        user.setOidcId(oidcId);
        user.setOidcProvider(finalOidcProvider); // Use the default or provided provider
        return userRepository.save(user);
    }

    /**
     * Authenticates the user with the given username and password.
     * If the credentials are valid, generates and returns a JWT token for the user.
     *
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @return A JWT token for the authenticated user.
     * @throws BadCredentialsException if the username or password is incorrect.
     */
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtService.generateToken(user.getId());
    }

    /**
     * Finds a user by their OpenID identifier.
     *
     * @param oidcId The OpenID identifier of the user.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    public Optional<User> findByOpenId(String oidcId) {
        return userRepository.findByOidcId(oidcId);
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username of the user to search for.
     * @return An Optional containing the user if found, otherwise empty.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Loads the user details by the username.
     * Implements the `loadUserByUsername` method from `UserDetailsService` for Spring Security integration.
     *
     * @param username The username of the user to load.
     * @return The user details for the given username.
     * @throws UsernameNotFoundException if no user is found with the specified username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Validates the input for username and password.
     * Ensures that both the username and password are not null, empty, and meet minimum length requirements.
     *
     * @param username The username to validate.
     * @param password The password to validate.
     * @throws IllegalArgumentException if the username or password does not meet the required criteria.
     */
    private void validateUserInput(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username may not be null or empty");
        }

        if (username.length() < 5) {
            throw new IllegalArgumentException("Username must be at least 5 characters");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password may not be null or empty");
        }

        if (password.length() < 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters");
        }
    }

    /**
     * Checks if the provided username is already taken.
     *
     * @param username The username to check for availability.
     * @throws IllegalArgumentException if the username is already taken.
     */
    private void checkUsernameAvailability(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");  // Throw an exception if the username is taken
        }
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user to search for.
     * @return An Optional containing the user if found, otherwise empty.
     */
    public Optional<User> findUserById(UUID id) {
        return userRepository.findById(id);
    }
}